package ctrlS.totori.book.client;

import ctrlS.totori.book.dto.fastApi.FastApiSttResponse;
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

@Component
@RequiredArgsConstructor
public class FastApiSttClient {

    private final WebClient fastApiSttWebClient;

    public String transcribe(MultipartFile audioFile) {
        FastApiSttResponse response = fastApiSttWebClient.post()
                .uri("/ai/stt/transcribe?preset=balanced")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(buildMultipartBody(audioFile).build()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        Mono.error(new CustomException(ErrorCode.STT_TRANSCRIBE_FAILED)))
                .bodyToMono(FastApiSttResponse.class)
                .block();

        if (response == null || response.text() == null || response.text().isBlank()) {
            throw new CustomException(ErrorCode.STT_EMPTY_RESULT);
        }

        return response.text();
    }

    private MultipartBodyBuilder buildMultipartBody(MultipartFile audioFile) {
        try {
            String filename = audioFile.getOriginalFilename() != null
                    ? audioFile.getOriginalFilename()
                    : "audio.m4a";
            MediaType contentType = audioFile.getContentType() != null
                    ? MediaType.parseMediaType(audioFile.getContentType())
                    : MediaType.parseMediaType("audio/m4a");

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
