package ctrlS.totori.book.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SentenceData {
    private String text;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String audioS3Key;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer durationMs;

    public static SentenceData ofText(String text) {
        SentenceData data = new SentenceData();
        data.text = text;
        return data;
    }

    public boolean hasAudio() {
        return audioS3Key != null;
    }

    public void updateAudio(String audioS3Key, Integer durationMs) {
        this.audioS3Key = audioS3Key;
        this.durationMs = durationMs;
    }
}
