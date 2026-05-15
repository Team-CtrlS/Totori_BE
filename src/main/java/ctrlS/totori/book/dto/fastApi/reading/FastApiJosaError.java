package ctrlS.totori.book.dto.fastApi.reading;

import com.fasterxml.jackson.annotation.JsonProperty;
import ctrlS.totori.book.dto.fastApi.reading.FastApiErrorItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FastApiJosaError extends FastApiErrorItem {
    private String kind;
    private String stem;
    @JsonProperty("target_josa")
    private String targetJosa;
    @JsonProperty("stt_josa")
    private String sttJosa;
}
