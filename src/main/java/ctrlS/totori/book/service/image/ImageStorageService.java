package ctrlS.totori.book.service.image;

public interface ImageStorageService {
    String uploadImage(byte[] imageBytes, String fileName);
}
