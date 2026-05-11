package ctrlS.totori.book.service;

import ctrlS.totori.badge.dto.MemberBadgeResponseDto;
import ctrlS.totori.badge.service.BadgeService;
import ctrlS.totori.book.client.FastApiStoryClient;
import ctrlS.totori.book.dto.fastApi.FastApiGenerateStoryRequest;
import ctrlS.totori.book.dto.fastApi.FastApiStoryResponse;
import ctrlS.totori.book.dto.request.BookGenerateRequest;
import ctrlS.totori.book.dto.response.*;
import ctrlS.totori.book.dto.summary.BookCardSummary;
import ctrlS.totori.book.dto.summary.BookCoverSummary;
import ctrlS.totori.book.entity.Book;
import ctrlS.totori.book.entity.BookPage;
import ctrlS.totori.book.entity.BookReadingRecord;
import ctrlS.totori.book.repository.BookPageRepository;
import ctrlS.totori.book.repository.BookReadingRecordRepository;
import ctrlS.totori.book.repository.BookRepository;
import ctrlS.totori.book.service.audio.S3AudioStorageService;
import ctrlS.totori.book.service.audio.TtsService;
import ctrlS.totori.book.service.image.PageImageAsyncService;
import ctrlS.totori.book.service.image.S3ImageStorageService;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.global.util.AudioFileValidator;
import ctrlS.totori.member.dto.response.AcornResponse;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final MemberService memberService;
    private final BookRepository bookRepository;
    private final BadgeService badgeService;
    private final BookReadingRecordRepository bookReadingRecordRepository;
    private final BookPageRepository bookPageRepository;
    private final FastApiStoryClient fastApiStoryClient;
    private final PageImageAsyncService pageImageAsyncService;
    private final S3ImageStorageService s3ImageStorageService;
    private final TtsService ttsService;
    private final S3AudioStorageService s3AudioStorageService;
    private final AudioFileValidator audioFileValidator;

    private final static String BOOK_IMAGE_PREFIX = "bookImages";
    private final static String BOOK_AUDIO_PREFIX = "bookAudios";

    // 텍스트 기반 동화 생성
    public BookGenerateResponse generateBook(Long memberId, BookGenerateRequest request) {
        Member member = memberService.findById(memberId);
        BookReadingRecord latestRecord = getLatestRecord(memberId);

        FastApiGenerateStoryRequest fastApiRequest = createFastApiRequest(request, member, latestRecord);
        FastApiStoryResponse fastApiResponse = fastApiStoryClient.generateStory(fastApiRequest);

        return buildAndSaveBook(member, fastApiResponse);
    }

    // 관심사 음성 기반 동화 생성
    public BookGenerateResponse generateBookFromVoice(Long memberId, MultipartFile audioFile) {
        audioFileValidator.validate(audioFile);

        Member member = memberService.findById(memberId);
        BookReadingRecord latestRecord = getLatestRecord(memberId);

        float recentWcpm = latestRecord != null ? latestRecord.getWcpm() : 0f;
        List<String> weakPhonemes = extractWeakPhonemes(latestRecord);

        FastApiStoryResponse fastApiStoryResponse = fastApiStoryClient.generateStoryFromAudio(
                audioFile, member.getLevel().toString(), recentWcpm, weakPhonemes);

        return buildAndSaveBook(member, fastApiStoryResponse);
    }

    @Transactional(readOnly = true)
    public MainPageResponse getMainStatus(Long memberId) {
        Member member = memberService.findById(memberId);
        // TODO: 레벨테스트 연결 시 없으면 에러처리하도록 수정
        BookReadingRecord latestRecord = bookReadingRecordRepository.findLatestRecord(memberId)
                .orElse(null);
        // 대표 뱃지 조회
        MemberBadgeResponseDto badgeDto = badgeService.getRepresentativeBadge(memberId);
        // TODO: 레벨테스트 연결 시 수정
        if (latestRecord == null)
            return new MainPageResponse(AcornResponse.from(member), null, badgeDto.badgeResponseDto());

        String presignedCoverUrl = s3ImageStorageService.getPresignedUrl(BOOK_IMAGE_PREFIX, latestRecord.getBook().getCoverImageUrl());
        BookCoverSummary currentBookDto = BookCoverSummary.of(latestRecord.getBook(), latestRecord, presignedCoverUrl);

        return new MainPageResponse(AcornResponse.from(member), currentBookDto, badgeDto.badgeResponseDto());
    }

    @Transactional(readOnly = true)
    public BookListResponse getBookList(Long memberId, Pageable pageable) {
        // 유저의 전체 책 id 리스트 (페이징)
        Page<Book> bookPage = bookRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        List<Long> bookIds = bookPage.getContent().stream().map(Book::getId).toList();

        // 각 책별로 최신 bookRecord 매핑
        Map<Long, BookReadingRecord> latestRecordMap = bookReadingRecordRepository.findLatestRecordsByBookIds(bookIds)
                .stream()
                .collect(Collectors.toMap(r -> r.getBook().getId(), r -> r));

        Page<BookCardSummary> summaryPage = bookPage.map(book -> {
            String presignedCoverUrl = s3ImageStorageService.getPresignedUrl(BOOK_IMAGE_PREFIX, book.getCoverImageUrl());
            return BookCardSummary.of(book, latestRecordMap.get(book.getId()), presignedCoverUrl);
        });
        return BookListResponse.of(summaryPage);
    }

    // 동화 낭독 음성 전송
    public void forwardReadingAudio(
            Long memberId, Long bookId, int sentenceNum, MultipartFile audioFile) {
        audioFileValidator.validate(audioFile);

        Member member = memberService.findById(memberId);
        bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        int pageOrder = (sentenceNum - 1) / 3 + 1;
        int sentenceIndex = (sentenceNum - 1) % 3;

        BookPage page = bookPageRepository.findByBook_idAndPageOrder(bookId, pageOrder)
                .orElseThrow(() -> new CustomException(ErrorCode.PAGE_NOT_FOUND));

        String originalText = page.getSentences().get(sentenceIndex).getText();

        fastApiStoryClient.analyzeReading(
                audioFile,
                originalText,
                member.getId(),
                bookId,
                member.getLevel().toString()
        );
    }

    protected BookReadingRecord getLatestRecord(Long memberId) {
        return bookReadingRecordRepository
                .findTopByBook_Member_IdOrderByUpdatedAtDesc(memberId)
                .orElse(null);
    }

    private FastApiGenerateStoryRequest createFastApiRequest(BookGenerateRequest request, Member member, BookReadingRecord latestRecord) {
        float recentWcpm = latestRecord != null ? latestRecord.getWcpm() : 0f;
        List<String> weakPhonemes = extractWeakPhonemes(latestRecord);

        return new FastApiGenerateStoryRequest(
                request.sttText(),
                member.getLevel().toString(),
                recentWcpm,
                weakPhonemes
        );
    }

    private List<String> extractWeakPhonemes(BookReadingRecord latestRecord) {
        if (latestRecord == null || latestRecord.getMistakes() == null || latestRecord.getMistakes().isEmpty()) {
            return List.of();
        }

        return latestRecord.getMistakes().entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() > 0)
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
    }

    private BookGenerateResponse buildAndSaveBook(Member member, FastApiStoryResponse fastApiResponse) {
        Book book = Book.of(member, fastApiResponse);

        List<BookPage> pages = fastApiResponse.pages().stream()
                .map(pageResponse -> BookPage.of(book, pageResponse))
                .toList();

        long bookSeed = ThreadLocalRandom.current().nextInt(10000000);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        String coverPrompt = book.getCoverImagePrompt();
        if (coverPrompt != null && !coverPrompt.isBlank()) {
            String coverFileName = UUID.randomUUID() + "_cover.png";
            CompletableFuture<Void> coverFuture = pageImageAsyncService.generateAndUpload(coverPrompt, bookSeed, coverFileName)
                    .thenAccept(book::updateCoverImageUrl);

            futures.add(coverFuture);
        }

        int pageNumber = 1;
        for (BookPage page : pages) {
            String prompt = page.getImagePrompt();
            String fileName = UUID.randomUUID() + "_page_" + pageNumber + ".png";

            CompletableFuture<Void> future = pageImageAsyncService.generateAndUpload(prompt, bookSeed, fileName)
                    .thenAccept(page::updateImageUrl);

            futures.add(future);
            pageNumber++;
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        book.getPages().addAll(pages);

        Book savedBook = bookRepository.save(book);

        try {
            ttsService.generateAllAudios(savedBook);
            bookRepository.save(savedBook);
        } catch (Exception e) {
            // TTS 실패해도 책 자체는 저장 (사용자가 글로 읽을 수는 있게)
            log.error("책 TTS 생성 실패 (책은 정상 저장됨): bookId(미저장), error={}", e.getMessage());
        }

        String presignedCoverUrl = s3ImageStorageService.getPresignedUrl(BOOK_IMAGE_PREFIX, savedBook.getCoverImageUrl());

        List<BookPageResponse> pageResponses = savedBook.getPages().stream()
                .map(page -> {
                    String presignedPageUrl = s3ImageStorageService.getPresignedUrl(
                            BOOK_IMAGE_PREFIX, page.getImageUrl());
                    return BookPageResponse.of(
                            page, presignedPageUrl, s3AudioStorageService, BOOK_AUDIO_PREFIX);
                })
                .toList();

        return BookGenerateResponse.of(savedBook, presignedCoverUrl, pageResponses);
    }
}
