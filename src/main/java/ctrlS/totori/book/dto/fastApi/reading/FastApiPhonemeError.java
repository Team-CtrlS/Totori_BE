package ctrlS.totori.book.dto.fastApi.reading;

import ctrlS.totori.book.dto.fastApi.reading.FastApiErrorItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FastApiPhonemeError extends FastApiErrorItem {
    private String pattern;
    private String word;
}
