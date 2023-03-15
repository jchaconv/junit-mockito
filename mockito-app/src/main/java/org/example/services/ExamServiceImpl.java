package org.example.services;

import org.example.models.Exam;
import org.example.repositories.ExamRepository;
import org.example.repositories.QuestionRepository;

import java.util.List;
import java.util.Optional;

public class ExamServiceImpl implements ExamService {

    private ExamRepository examRepository;
    private QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Exam findExamByName(String name) {
        Optional<Exam> optionalExam = examRepository.findAll().stream().filter(e -> e.getName().contains(name))
                .findFirst();
        Exam exam = null;
        if(optionalExam.isPresent())
            exam = optionalExam.orElseThrow();

        return exam;
    }

    @Override
    public Optional<Exam> findOptionalExamByName(String name) {
        return examRepository.findAll().stream()
                .filter(e -> e.getName().contains(name))
                .findFirst();
    }

    @Override
    public Exam findExamByNameWithQuestions(String name) {
        System.out.println("ExamServiceImpl.findExamByNameWithQuestions");
        Optional<Exam> optionalExam = findOptionalExamByName(name);
        Exam exam = null;
        if(optionalExam.isPresent()) {
            exam = optionalExam.orElseThrow();
            List<String> questions = questionRepository.findQuestionsByExamId(exam.getId());
            //Se agrega para la prueba de invocaciones:
            //questionRepository.findQuestionsByExamId(exam.getId());
            exam.setQuestions(questions);
        }
        return exam;
    }

    @Override
    public Exam save(Exam exam) {
        if(!exam.getQuestions().isEmpty()) {
            questionRepository.saveManyQuestions(exam.getQuestions());
        }
        return examRepository.save(exam);
    }
}
