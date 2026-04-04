package ctrlS.totori.book.client;

import ctrlS.totori.book.dto.fastApi.FastApiGenerateStoryRequest;
import ctrlS.totori.book.dto.fastApi.FastApiStoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class FastApiStoryClient {

    private final WebClient fastApiWebClient;

    public FastApiStoryResponse generateStory(FastApiGenerateStoryRequest request) {
        return fastApiWebClient.post()
                .uri("/api/story/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FastApiStoryResponse.class)
                .block();
    }
}
