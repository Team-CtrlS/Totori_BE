package ctrlS.totori.book.controller;

import ctrlS.totori.book.dto.BookGenerateRequest;
import ctrlS.totori.book.dto.BookGenerateResponse;
import ctrlS.totori.book.service.BookService;
import ctrlS.totori.global.response.dto.BaseResponse;
import ctrlS.totori.global.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "동화 API", description = "동화 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "동화 생성", description = "아동의 입력갑을 바탕으로 맞춤형 동화를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동화 생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookGenerateResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @PostMapping("/generate")
    public BaseResponse<BookGenerateResponse> generateBook(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody BookGenerateRequest request
            ) {
        return BaseResponse.ok(bookService.generateBook(principal.getMemberId(), request));
    }
}
