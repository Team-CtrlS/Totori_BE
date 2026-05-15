package ctrlS.totori.member.entity;

import ctrlS.totori.quiz.entity.QuizType;

public enum MemberLevel {
    L1, L2, L3, L4, L5, L6;

    public QuizType toQuizType() {
        return this.ordinal() < 3 ? QuizType.PHONEME : QuizType.JOSA;
    }
}
