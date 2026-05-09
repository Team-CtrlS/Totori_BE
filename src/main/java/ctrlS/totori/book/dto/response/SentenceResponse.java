package ctrlS.totori.book.dto.response;

import ctrlS.totori.book.entity.SentenceData;
import ctrlS.totori.book.service.S3AudioStorageService;

public record SentenceResponse(
        String text,
        String audioUrl,
        Integer durationMs
) {
    public static SentenceResponse of(
            SentenceData data,
            S3AudioStorageService s3AudioStorageService,
            String audioPrefix
    ) {
        String audioUrl = s3AudioStorageService.getPresignedUrl(audioPrefix, data.getAudioS3Key());
        return new SentenceResponse(data.getText(), audioUrl, data.getDurationMs());
    }
}
