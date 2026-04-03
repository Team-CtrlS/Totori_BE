package ctrlS.totori.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PageImageAsyncService {

    private final StableDiffusionService stableDiffusionService;
    private final ImageStorageService imageStorageService;

    @Async("imageExecutor")
    public CompletableFuture<String> generateAndUpload(String prompt, Long bookSeed, String fileName) {
        byte[] imageBytes = stableDiffusionService.generateImage(prompt, bookSeed);
        String imageUrl = imageStorageService.uploadImage(imageBytes, fileName);
        return CompletableFuture.completedFuture(imageUrl);
    }
}
