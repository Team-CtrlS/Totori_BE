package ctrlS.totori.book.service.audio;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3AudioStorageService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일명 형식: {bookId}/{pageOrder}/{sentenceIndex}.mp3
     * 전체 키: bookAudios/{bookId}/{pageOrder}/{sentenceIndex}.mp3
     */
    public String uploadAudio(byte[] audioBytes, String fileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key("bookAudios/" + fileName)
                    .contentType("audio/mpeg")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(audioBytes));
            return fileName;
        } catch (Exception e) {
            System.err.println("S3 오디오 업로드 에러" + e.getMessage());
            throw new CustomException(ErrorCode.TTS_UPLOAD_FAILED);
        }
    }

    public String getPresignedUrl(String prefix, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        try {
            String key = (prefix != null && !prefix.isBlank())
                    ? prefix + "/" + fileName
                    : fileName;

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest =
                    s3Presigner.presignGetObject(getObjectPresignRequest);

            return presignedGetObjectRequest.url().toString();

        } catch (Exception e) {
            System.err.println("🚨 Audio Presigned URL 생성 에러: " + e.getMessage());
            throw new CustomException(ErrorCode.TTS_UPLOAD_FAILED);
        }
    }
}
