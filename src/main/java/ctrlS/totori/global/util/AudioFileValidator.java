package ctrlS.totori.global.util;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AudioFileValidator {

    public void validate(MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new CustomException(ErrorCode.STT_EMPTY_RESULT);
        }

        String contentType = audioFile.getContentType();
        if (!contentType.startsWith("audio/")) {
            throw new CustomException(ErrorCode.INVALID_AUDIO_FILE);
        }

        // 음성 파일 30MB 제한
        if (audioFile.getSize() > 30 * 1024 * 1024) {
            throw new CustomException(ErrorCode.AUDIO_FILE_TOO_LARGE);
        }
    }
}
