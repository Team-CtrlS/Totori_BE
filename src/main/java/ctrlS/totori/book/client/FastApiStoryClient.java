package ctrlS.totori.book.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ctrlS.totori.book.dto.fastApi.FastApiCompleteStoryResponse;
import ctrlS.totori.book.dto.fastApi.FastApiGenerateStoryRequest;
import ctrlS.totori.book.dto.fastApi.FastApiStoryResponse;
import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FastApiStoryClient {
    private final WebClient fastApiWebClient;
    private final WebClient fastApiSttWebClient;
    private final ObjectMapper objectMapper;

    // 동화 생성
    public FastApiStoryResponse generateStory(FastApiGenerateStoryRequest request) {
        return fastApiWebClient.post()
                .uri("/ai/story/generate")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CustomException(ErrorCode.STT_TRANSCRIBE_FAILED)))
                .bodyToMono(FastApiStoryResponse.class)
                .block();
    }

    // 관심사 음성 기반 동화 생성
    public FastApiStoryResponse generateStoryFromAudio(
            MultipartFile audioFile,
            String level,
            Float recentWcpm,
            List<String> weakPhonemes) {
        MultipartBodyBuilder builder = buildMultipartBody(audioFile, level, recentWcpm, weakPhonemes);

        return fastApiSttWebClient.post()
                .uri("/ai/story/make")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CustomException(ErrorCode.STT_TRANSCRIBE_FAILED)))
                .bodyToMono(FastApiStoryResponse.class)
                .block();
    }

    // 동화 낭독
    public void analyzeReading(
            MultipartFile audioFile,
            String originalText,
            Long childId,
            Long bookId,
            String level) {
        MultipartBodyBuilder builder = buildReadingMultipartBody(audioFile, originalText, childId, bookId, level);

        fastApiSttWebClient.post()
                .uri("/ai/reading/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData((builder.build())))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CustomException(ErrorCode.STT_TRANSCRIBE_FAILED)))
                .bodyToMono(Void.class)
                .block();
    }

    private MultipartBodyBuilder buildMultipartBody(
            MultipartFile audioFile,
            String level,
            Float recentWcpm,
            List<String> weakPhonemes) {
        MultipartBodyBuilder builder = createMultipartBuilderWithAudio(audioFile);

        builder.part("level", level);
        if (recentWcpm != null) {
            builder.part("recent_wcpm", recentWcpm.toString());
        }
        if (weakPhonemes != null && !weakPhonemes.isEmpty()) {
            try {
                builder.part("weak_phonemes", objectMapper.writeValueAsString(weakPhonemes));
            } catch (IOException e) {
                throw new CustomException(ErrorCode.STT_FILE_READ_FAILED);
            }
        }

        return builder;
    }

    private MultipartBodyBuilder buildReadingMultipartBody(
            MultipartFile audioFile,
            String originalText,
            Long childId,
            Long bookId,
            String level) {
        MultipartBodyBuilder builder = createMultipartBuilderWithAudio(audioFile);

        builder.part("original_text", originalText);
        builder.part("child_id", childId.toString());
        builder.part("book_id", bookId.toString());
        builder.part("level", level);

        return builder;
    }

    private MultipartBodyBuilder createMultipartBuilderWithAudio(MultipartFile audioFile) {
        try {
            String filename = audioFile.getOriginalFilename();

            audioFile.getContentType();
            MediaType contentType = MediaType.parseMediaType(audioFile.getContentType());

            ByteArrayResource resource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", resource).contentType(contentType);

            return builder;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.STT_FILE_READ_FAILED);
        }
    }
}