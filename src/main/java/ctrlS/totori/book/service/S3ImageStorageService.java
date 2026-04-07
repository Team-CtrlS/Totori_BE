package ctrlS.totori.book.service;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.awt.*;

@Service
@RequiredArgsConstructor
public class S3ImageStorageService implements ImageStorageService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String uploadImage(byte[] imageBytes, String fileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key("images/" + fileName)
                    .contentType("image/png")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
            return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/images/" + fileName;
        } catch (Exception e) {
            System.err.println("S3 업로드 에러" + e.getMessage());
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }
    }
}
