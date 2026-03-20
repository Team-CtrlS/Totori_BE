package ctrlS.totori.book.service;

import ctrlS.totori.book.client.FastApiStoryClient;
import ctrlS.totori.book.dto.*;
import ctrlS.totori.book.entity.Book;
import ctrlS.totori.book.entity.BookPage;
import ctrlS.totori.book.entity.BookReadingRecord;
import ctrlS.totori.book.repository.BookReadingRecordRepository;
import ctrlS.totori.book.repository.BookRepository;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final BookReadingRecordRepository bookReadingRecordRepository;
    private final FastApiStoryClient fastApiStoryClient;

    public BookGenerateResponse generateBook(BookGenerateRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BookReadingRecord latestRecord = bookReadingRecordRepository
                .findTopByBook_Member_IdOrderByCreatedAtDesc(member.getId())
                .orElse(null);

        float recentWcpm = latestRecord != null ? latestRecord.getWcpm() : 0f;

        List<String> weakPhonemes = extractWeakPhonemes(latestRecord);

        FastApiGenerateStoryRequest fastApiRequest = new FastApiGenerateStoryRequest(
                request.sttText(),
                member.getLevel().toString(),
                recentWcpm,
                weakPhonemes
        );

        FastApiStoryResponse fastApiResponse = fastApiStoryClient.generateStory(fastApiRequest);

        Book book = Book.builder()
                .member(member)
                .title(fastApiResponse.title())
                .coverImageURL(null)
                .totalPages(fastApiResponse.pages().size())
                .build();

        List<BookPage> pages = new ArrayList<>();

        for (FastApiPageResponse pageResponse : fastApiResponse.pages()) {
            BookPage page = BookPage.builder()
                    .book(book)
                    .pageOrder(pageResponse.pageOrder())
                    .sentences(pageResponse.sentences())
                    .imagePrompt(pageResponse.imagePrompt())
                    .imageUrl(null)
                    .build();

            pages.add(page);
        }

        book.getPages().addAll(pages);

        Book savedBook = bookRepository.save(book);

        List<BookPageResponse> pageResponses = savedBook.getPages().stream()
                .map(page -> new BookPageResponse(
                        page.getId(),
                        page.getPageOrder(),
                        page.getSentences(),
                        page.getImagePrompt(),
                        page.getImageUrl()
                )).toList();

        return new BookGenerateResponse(
                savedBook.getId(),
                savedBook.getTitle(),
                savedBook.getTotalPages(),
                pageResponses
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
}
