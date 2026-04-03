package ctrlS.totori.book.service;

public interface ImageStorageService {
    String uploadImage(byte[] imageBytes, String fileName);
}
