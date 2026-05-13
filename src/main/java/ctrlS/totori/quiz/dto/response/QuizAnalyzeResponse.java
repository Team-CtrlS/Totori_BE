package ctrlS.totori.quiz.dto.response;

public record QuizAnalyzeResponse(
        boolean isCorrect,
        boolean rewarded,
        int currentAcorn
) {
}
