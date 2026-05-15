package ctrlS.totori.book.controller;

import ctrlS.totori.book.dto.request.BookCompleteRequest;
import ctrlS.totori.book.dto.request.BookGenerateRequest;
import ctrlS.totori.book.dto.response.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "동화 API", description = "동화 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "관심사 음성 기반 동화 생성", description = "아동의 음성 녹음을 받아 STT 변환 후 맞춤형 동화를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동화 생성 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookGenerateResponse.class)))
    })
    @PostMapping( value = "/make", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<BookGenerateResponse> makeBookFromVoice(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestPart("audio") MultipartFile audioFile
            ) {
        return BaseResponse.ok(bookService.generateBookFromVoice(principal.memberId(), audioFile));
    }

    @Operation(summary = "동화 생성", description = "아동의 입력값을 바탕으로 맞춤형 동화를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동화 생성 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookGenerateResponse.class))),
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
            @ApiResponse(responseCode = "200", description = "메인페이지 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MainPageResponse.class)))
    })
    @GetMapping("/main-status")
    public BaseResponse<MainPageResponse> getBookList(@AuthenticationPrincipal CustomUserPrincipal principal){
        return BaseResponse.ok(bookService.getMainStatus(principal.memberId()));
    }

    @Operation(summary = "유저의 전체 책 조회(페이징)", description = "유저의 전체 책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 리스트 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookListPagingResponse.class)))
    })
    @GetMapping
    public BaseResponse<BookListPagingResponse> getBooks(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        BookListPagingResponse response = bookService.getBookList(principal.memberId(), pageable);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "유저의 최근 1주일 동화 조회", description = "유저의 최근 1주일의 책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 1주일 리스트 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookWeeklyListResponse.class)))
    })
    @GetMapping("/week")
    public BaseResponse<BookWeeklyListResponse> getBooks(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        BookWeeklyListResponse response = bookService.getWeeklyReport(principal.memberId());
        return BaseResponse.ok(response);
    }

    @Operation(summary = "동화 낭독 음성 전송", description = "페이지 낭독 음성을 수신하여 AI 서버로 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동화 낭독 음성 전송 성공", content = @Content)
    })
    @PostMapping(value = "/{bookId}/reading/{sentenceNum}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> forwardReadingAudio(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable("bookId") Long bookId,
            @PathVariable("sentenceNum") int sentenceNum,
            @RequestPart("audio") MultipartFile audioFile
    ) {
        bookService.forwardReadingAudio(principal.memberId(), bookId, sentenceNum, audioFile);
        return BaseResponse.ok();
    }

    @Operation(summary = "동화 상세 조회", description = "특정 동화의 표지, 페이지 내용, 읽기 진행 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동화 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookDetailResponse.class))),
            @ApiResponse(responseCode = "403", description = "본인의 책이 아님", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 책", content = @Content)
    })
    @GetMapping("/{bookId}")
    public BaseResponse<BookDetailResponse> getBookDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long bookId
    ) {
        return BaseResponse.ok(bookService.getBookDetail(principal.memberId(), bookId));
    }

    @Operation(summary = "책 완독 처리", description = "책 읽기를 완료하고 획득한 뱃지를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "완독 처리 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookCompleteResponse.class))),
            @ApiResponse(responseCode = "404", description = "책 또는 읽기 기록 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 완독된 책", content = @Content)
    })
    @PostMapping("/{bookId}/complete")
    public BaseResponse<BookCompleteResponse> completeBook(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long bookId,
            @Valid @RequestBody BookCompleteRequest request
    ) {
        return BaseResponse.ok(bookService.completeBook(principal.memberId(), bookId, request));
    }
}
