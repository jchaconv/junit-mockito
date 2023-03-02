package org.example.repositories;

import java.util.List;

public interface QuestionRepository {

    List<String> findQuestionsByExamId(Long id);

    void saveManyQuestions(List<String> questions);

}
