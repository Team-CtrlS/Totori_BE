package ctrlS.totori.book.controller;

import ctrlS.totori.book.dto.request.BookGenerateRequest;
import ctrlS.totori.book.dto.response.BookGenerateResponse;
import ctrlS.totori.book.dto.response.BookListResponse;
import ctrlS.totori.book.dto.response.MainPageResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "동화 API", description = "동화 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "동화 생성", description = "아동의 입력값을 바탕으로 맞춤형 동화를 생성합니다.")
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
        return BaseResponse.ok(bookService.generateBook(principal.memberId(), request));
    }

    @Operation(summary = "메인페이지 조회", description = "메인페이지의 가장 최근 읽은 책과 대표 뱃지 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메인페이지 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MainPageResponse.class)))
    })
    @GetMapping("/main-status")
    public BaseResponse<MainPageResponse> getBookList(@AuthenticationPrincipal CustomUserPrincipal principal){
        return BaseResponse.ok(bookService.getMainStatus(principal.getMemberId()));
    }

    @Operation(summary = "유저의 전체 책 조회(페이징)", description = "유저의 전체 책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 리스트 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookListResponse.class)))
    })
    @GetMapping
    public BaseResponse<BookListResponse> getBooks(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        BookListResponse response = bookService.getBookList(principal.getMemberId(), pageable);
        return BaseResponse.ok(response);
    }
}
