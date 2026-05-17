package ctrlS.totori.book.dto.fastApi.reading;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FastApiPhonemeError.class, name = "phoneme"),
        @JsonSubTypes.Type(value = FastApiJosaError.class, name = "josa")
})
public abstract class FastApiErrorItem {
}
