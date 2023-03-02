package org.example.services;

import org.example.models.Exam;
import org.example.repositories.ExamRepository;

import java.util.Optional;

public class ExamServiceJunitImpl implements ExamServiceJunit {

    private ExamRepository examRepository;

    public ExamServiceJunitImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam findExamByName(String name) {
        Optional<Exam> optionalExam = examRepository.findAll().stream().filter(e -> e.getName().contains(name))
                .findFirst();
        Exam exam = null;
        if(optionalExam.isPresent())
            System.out.println("Exam is present in Junit implementation");
            exam = optionalExam.orElseThrow();

        return exam;
    }
}
