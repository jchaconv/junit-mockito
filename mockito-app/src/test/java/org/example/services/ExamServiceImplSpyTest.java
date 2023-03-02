package org.example.services;

import org.example.models.Exam;
import org.example.repositories.ExamRepository;
import org.example.repositories.ExamRepositoryImpl;
import org.example.repositories.QuestionRepository;
import org.example.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplSpyTest {

    @Spy
    ExamRepositoryImpl examRepository;

    @Spy
    QuestionRepositoryImpl questionRepository;

    @InjectMocks
    ExamServiceImpl service;

    @Test
    void spyTest() {
        ExamRepository examRepository = spy(ExamRepositoryImpl.class);
        QuestionRepository questionRepository = spy(QuestionRepositoryImpl.class);
        ExamService examService = new ExamServiceImpl(examRepository, questionRepository);

        //Para comprobar que se invoca al mock a pesar de mostrar que pasa por el método real
        List<String> questions = Arrays.asList("Question 5");
        //when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(questions);

        doReturn(questions).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = examService.findExamByNameWithQuestions("OOP with Java");
        assertEquals(1L, exam.getId());
        assertEquals("OOP with Java", exam.getName());
        assertEquals(1, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 5"));

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());

    }


}