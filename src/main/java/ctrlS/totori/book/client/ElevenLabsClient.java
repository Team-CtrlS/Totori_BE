package ctrlS.totori.book.client;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElevenLabsClient {
    private final @Qualifier("elevenLabsWebClient") WebClient elevenLabsWebClient;

    @Value("${elevenlabs.default-voice-id}")
    private String defaultVoiceId;

    @Value("${elevenlabs.default-model-id}")
    private String defaultModelId;

    public byte[] synthesize(String text) {
        Map<String, Object> body = Map.of(
                "text", text,
                "model_id", defaultModelId,
                "voice_settings", Map.of(
                        "stability", 0.75,
                        "similarity_boost", 0.75,
                        "style", 0.5,
                        "speed", 0.80
                )
        );

        try {
            byte[] audio = elevenLabsWebClient.post()
                    .uri("/text-to-speech/{voiceId}", defaultVoiceId)
                    .header("Accept", "audio/mpeg")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("응답 본문 없음")
                                    .map(errorBody -> {
                                        log.error("ElevenLabs API 에러: status={}, body={}",
                                                response.statusCode(), errorBody);
                                        return new CustomException(ErrorCode.TTS_GENERATION_FAILED);
                                    })
                    )
                    .bodyToMono(byte[].class)
                    .block();

            if (audio == null || audio.length == 0) {
                throw new CustomException(ErrorCode.TTS_GENERATION_FAILED);
            }
            return audio;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("ElevenLabs 호출 실패: text={}, error={}",
                    text.substring(0, Math.min(20, text.length())), e.getMessage());
            throw new CustomException(ErrorCode.TTS_GENERATION_FAILED);
        }
    }
}
