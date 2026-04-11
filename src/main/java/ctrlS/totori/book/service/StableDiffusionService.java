package ctrlS.totori.book.service;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StableDiffusionService {

    private final RestTemplate restTemplate;

    @Value("${stability.api-key}")
    private String apiKey;

    public byte[] generateImage(String prompt, long seed) {
        String url = "https://api.stability.ai/v2beta/stable-image/generate/sd3";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(apiKey);
        headers.set("Accept", "application/json");

        String enhancedPrompt = "cute children's book illustration, watercolor style, " + prompt;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("prompt", enhancedPrompt);
        body.add("model", "sd3.5-flash");
        body.add("aspect-ratio", "4:6");
        body.add("output_format", "png");
        body.add("seed", seed); //하나의 동화책에 같은 시드를 부여하여 이미지간 연결성을 높입니다.

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            String base64Image = (String) response.getBody().get("image");
            return Base64.getDecoder().decode(base64Image);
        } catch (Exception e) {
            System.err.println("🚨 기타 에러: " + e.getMessage());
            throw new CustomException(ErrorCode.IMAGE_CREATE_ERROR);
        }
    }
}
