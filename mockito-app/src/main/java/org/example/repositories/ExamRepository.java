package org.example.repositories;

import org.example.models.Exam;

import java.util.List;

public interface ExamRepository {

    List<Exam> findAll();

    Exam save(Exam exam);

}
