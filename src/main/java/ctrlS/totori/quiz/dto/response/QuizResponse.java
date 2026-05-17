package ctrlS.totori.quiz.dto.response;

import ctrlS.totori.quiz.entity.Quiz;

import java.util.List;

public record QuizResponse(
        Long quizId,
        List<String> quizItems,
        List<String> audioUrls
) {
    public static QuizResponse of(Quiz quiz, List<String> audioUrls) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getQuizItems(),
                audioUrls
        );
    }
}