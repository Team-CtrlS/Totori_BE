package ctrlS.totori.quiz.client;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.quiz.dto.fastapi.FastApiAnalyzeQuizResponse;
import ctrlS.totori.quiz.dto.fastapi.FastApiGenerateQuizRequest;
import ctrlS.totori.quiz.dto.fastapi.FastApiGenerateQuizResponse;
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

@Component
@RequiredArgsConstructor
public class FastApiQuizClient {

    private final WebClient fastApiWebClient;
    private final WebClient fastApiSttWebClient;

    public FastApiGenerateQuizResponse generateQuiz(FastApiGenerateQuizRequest request) {
        return fastApiWebClient.post()
                .uri("/ai/quiz/generate")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CustomException(ErrorCode.QUIZ_GENERATE_FAILED)))
                .bodyToMono(FastApiGenerateQuizResponse.class)
                .block();
    }

    private MultipartBodyBuilder buildQuizMultipartBody(
            MultipartFile audioFile,
            String originalQuiz) {
        try {
            String filename = audioFile.getOriginalFilename();
            MediaType contentType = MediaType.parseMediaType(audioFile.getContentType());

            ByteArrayResource resource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", resource).contentType(contentType);
            builder.part("original_quiz", originalQuiz);
            return builder;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.STT_FILE_READ_FAILED);
        }
    }
}
