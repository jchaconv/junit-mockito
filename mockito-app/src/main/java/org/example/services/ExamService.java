package org.example.services;

import org.example.models.Exam;

import java.util.Optional;

public interface ExamService {

    Exam findExamByName(String name);

    Optional<Exam> findOptionalExamByName(String name);

    Exam findExamByNameWithQuestions(String name);

    Exam save(Exam exam);

}
