package ctrlS.totori.book.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalImageStorageService implements ImageStorageService {
    private final String UPLOAD_DIR = "C:/totori/images/";

    @Override
    public String uploadImage(byte[] imageBytes, String fileName) {
        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, imageBytes);

            return "/images/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("로컬 이미지 저장 실패", e);
        }
    }
}
