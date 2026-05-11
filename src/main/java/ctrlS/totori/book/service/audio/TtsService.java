package ctrlS.totori.book.service.audio;

import ctrlS.totori.book.client.ElevenLabsClient;
import ctrlS.totori.book.entity.Book;
import ctrlS.totori.book.entity.BookPage;
import ctrlS.totori.book.entity.SentenceData;
import ctrlS.totori.book.repository.BookRepository;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TtsService {
    private final ElevenLabsClient elevenLabsClient;
    private final S3AudioStorageService s3AudioStorageService;
    private final BookRepository bookRepository;

    @Value("${tts.parallelism:3}")
    private int parallelism;

    public void generateAllAudios(Book book) {
        ExecutorService executor = Executors.newFixedThreadPool(parallelism);
        List<CompletableFuture<Void>> futures = new java.util.ArrayList<>();

        try {
            for (BookPage page : book.getPages()) {
                List<SentenceData> sentences = page.getSentences();
                for (int i = 0; i < sentences.size(); i++) {
                    final int sentenceIdx = i;
                    final SentenceData sentence = sentences.get(i);
                    final int pageOrder = page.getPageOrder();

                    if (sentence.hasAudio()) {
                        continue;
                    }

                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        int maxRetries = 3;
                        for (int attempt = 1; attempt <= maxRetries; attempt++) {
                            try {
                                byte[] audio = elevenLabsClient.synthesize(sentence.getText());
                                String fileName = String.format("%d_%d_%d.mp3",
                                        book.getId(), pageOrder, sentenceIdx);
                                s3AudioStorageService.uploadAudio(audio, fileName);
                                sentence.updateAudio(fileName, null);
                                return; // 성공
                            } catch (Exception e) {
                                log.warn("문장 TTS 시도 {}/{} 실패: bookId={}, page={}, idx={}",
                                        attempt, maxRetries, book.getId(), pageOrder, sentenceIdx);

                                if (attempt < maxRetries) {
                                    try {
                                        Thread.sleep(1000L * (1L << (attempt - 1)));
                                    } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                } else {
                                    log.error("문장 TTS 최종 실패: bookId={}, page={}, idx={}",
                                            book.getId(), pageOrder, sentenceIdx, e);
                                }
                            }
                        }
                    }, executor);

                    futures.add(future);
                }
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // 비동기 호출 버전
    @Async
    @Transactional
    public CompletableFuture<Void> generateAllAudiosAsync(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.TTS_NOT_READY));
        generateAllAudios(book);
        bookRepository.save(book);  // sentences JSON 변경 사항 저장
        return CompletableFuture.completedFuture(null);
    }
}
