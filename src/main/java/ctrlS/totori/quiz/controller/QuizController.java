package ctrlS.totori.quiz.controller;

import ctrlS.totori.global.response.dto.BaseResponse;
import ctrlS.totori.global.security.CustomUserPrincipal;
import ctrlS.totori.quiz.dto.response.QuizAnalyzeResponse;
import ctrlS.totori.quiz.dto.response.QuizResponse;
import ctrlS.totori.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "퀴즈 API", description = "퀴즈 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "퀴즈 생성", description = "레벨에 따라 읽기 오류 바탕으로 퀴즈를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "퀴즈 생성 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuizResponse.class))),
            @ApiResponse(responseCode = "204", description = "퀴즈 없음", content = @Content)
    })
    @PostMapping("/generate")
    public BaseResponse<?> generateQuizFromAudio(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam("bookId") Long bookId) {
        QuizResponse response = quizService.generateQuizFromAudio(principal.memberId(), bookId);

        if (response == null) {
            return BaseResponse.noContent();
        }
        return BaseResponse.ok(response);
    }

}
