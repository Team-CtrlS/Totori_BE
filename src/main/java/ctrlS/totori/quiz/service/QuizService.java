package ctrlS.totori.quiz.service;

import ctrlS.totori.badge.entity.BadgeCategory;
import ctrlS.totori.badge.service.BadgeService;
import ctrlS.totori.book.entity.Book;
import ctrlS.totori.book.repository.BookRepository;
import ctrlS.totori.book.service.audio.S3AudioStorageService;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.global.util.AudioFileValidator;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.MemberStat;
import ctrlS.totori.member.repository.MemberStatRepository;
import ctrlS.totori.member.service.MemberService;
import ctrlS.totori.quiz.client.FastApiQuizClient;
import ctrlS.totori.quiz.dto.fastapi.FastApiAnalyzeQuizResponse;
import ctrlS.totori.quiz.dto.fastapi.FastApiGenerateQuizRequest;
import ctrlS.totori.quiz.dto.fastapi.FastApiGenerateQuizResponse;
import ctrlS.totori.quiz.dto.response.QuizAnalyzeResponse;
import ctrlS.totori.quiz.dto.response.QuizResponse;
import ctrlS.totori.quiz.entity.Quiz;
import ctrlS.totori.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private static final String QUIZ_AUDIO_PREFIX = "quizAudios";

    private final MemberService memberService;
    private final MemberStatRepository memberStatRepository;
    private final BookRepository bookRepository;
    private final QuizRepository quizRepository;
    private final FastApiQuizClient fastApiQuizClient;
    private final S3AudioStorageService s3AudioStorageService;
    private final AudioFileValidator audioFileValidator;
    private final BadgeService badgeService;

    // 퀴즈 생성
    public QuizResponse generateQuizFromAudio(Long memberId, Long bookId) {
        Member member = memberService.findById(memberId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        if (!Objects.equals(book.getMember().getId(), member.getId())) {
            throw new CustomException(ErrorCode.BOOK_ACCESS_DENIED);
        }

        FastApiGenerateQuizRequest request = new FastApiGenerateQuizRequest(member.getId(), book.getId(), member.getLevel().name());
        FastApiGenerateQuizResponse fastApiResponse = fastApiQuizClient.generateQuiz(request);

        if (fastApiResponse == null) {
            return null;
        }

        // 퀴즈 내용 먼저 저장
        Quiz quiz = Quiz.of(book, member, fastApiResponse.quizItems(), new ArrayList<>());
        Quiz savedQuiz = quizRepository.save(quiz);

        // base64 디코딩 후 S3 업로드
        List<String> audioKeys = uploadQuizAudios(savedQuiz.getId(), fastApiResponse.audioData());
        savedQuiz.getAudioKeys().addAll(audioKeys);

        List<String> audioUrls = resolveAudioUrls(audioKeys);

        return QuizResponse.of(savedQuiz, audioUrls);
    }

    // 퀴즈 음성 전송
    public QuizAnalyzeResponse forwardQuizAudio(
            Long memberId, Long quizId, MultipartFile audioFile, String originalQuiz) {
        audioFileValidator.validate(audioFile);

        Member member = memberService.findById(memberId);

        MemberStat stat = memberStatRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.STAT_NOT_FOUND));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

        if (!Objects.equals(quiz.getBook().getMember().getId(), member.getId())) {
            throw new CustomException(ErrorCode.BOOK_ACCESS_DENIED);
        }

        FastApiAnalyzeQuizResponse fastApiResponse = fastApiQuizClient.analyzeQuiz(
                audioFile,
                originalQuiz
        );

        boolean isCorrect = fastApiResponse.isCorrect();
        boolean rewarded = false;

        if (isCorrect) {
            quiz.incrementCorrect();
        }

        if (quiz.isAllCorrect() && quiz.isRewardable()) {
            quiz.markAsRewarded();
            quiz.getBook().addAcorn();
            member.earnAcorn();
            stat.addAcquiredAcorn(1);
            badgeService.checkAndGrantBadge(memberId, BadgeCategory.ACORN);
            rewarded = true;
        }

        return new QuizAnalyzeResponse(isCorrect, rewarded, member.getAcorn());
    }

    // 퀴즈 조회
    @Transactional(readOnly = true)
    public QuizResponse getQuiz(Long memberId, Long quizId) {
        Member member = memberService.findById(memberId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

        if (!Objects.equals(quiz.getBook().getMember().getId(), member.getId())) {
            throw new CustomException(ErrorCode.BOOK_ACCESS_DENIED);
        }

        List<String> audioUrls = resolveAudioUrls(quiz.getAudioKeys());
        return QuizResponse.of(quiz, audioUrls);
    }

    private List<String> uploadQuizAudios(Long quizId, List<String> audioData) {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < audioData.size(); i++) {
            String base64 = audioData.get(i);
            if (base64 == null || base64.isBlank()) {
                keys.add("");
                continue;
            }
            byte[] audioBytes = Base64.getDecoder().decode(base64);
            String fileName = String.format("quiz_%d_%d.mp3", quizId, i);
            s3AudioStorageService.uploadAudio(audioBytes, QUIZ_AUDIO_PREFIX + "/" + fileName);
            keys.add(fileName);
        }
        return keys;
    }

    private List<String> resolveAudioUrls(List<String> audioKeys) {
        return audioKeys.stream()
                .map(key -> key.isBlank() ? "" : s3AudioStorageService.getPresignedUrl(QUIZ_AUDIO_PREFIX, key))
                .toList();
    }
}
