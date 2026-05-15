package ctrlS.totori.quiz.dto.response;

import ctrlS.totori.quiz.entity.Quiz;

import java.util.List;

public record QuizResponse(
        Long quizId,
        List<String> quizItems
) {
    public static QuizResponse from(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getQuizItems()
        );
    }
}