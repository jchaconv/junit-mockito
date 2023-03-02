package org.example.services;

import org.example.Data;
import org.example.models.Exam;
import org.example.repositories.ExamRepository;
import org.example.repositories.ExamRepositoryImpl;
import org.example.repositories.QuestionRepository;
import org.example.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    ExamRepositoryImpl examRepository;

    @Mock
    QuestionRepositoryImpl questionRepository;

    @InjectMocks
    ExamServiceImpl service;
    //ExamService service;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
        /*MockitoAnnotations.openMocks(this);
        examRepository = mock(ExamRepository.class);
        questionRepository = mock(QuestionRepository.class);
        service = new ExamServiceImpl(examRepository, questionRepository);*/
    }

    @Test
    void findExamByName() {
        ExamRepository examRepository = new ExamRepositoryImpl();
        ExamServiceJunit examServiceJunit = new ExamServiceJunitImpl(examRepository);
        Exam exam = examServiceJunit.findExamByName("OOP with Java");
        assertNotNull(exam);
        assertEquals(1L, exam.getId());
        assertEquals("OOP with Java", exam.getName());
    }

    @Test
    void findExamByNameWithMockito() {

        when(examRepository.findAll()).thenReturn(Data.EXAMS);

        Exam exam = service.findExamByName("OOP with Java");

        assertNotNull(exam);
        assertEquals(1L, exam.getId());
        assertEquals("OOP with Java", exam.getName());

    }

    @Test
    void findOptionalExamByNameWithMockito() {

        when(examRepository.findAll()).thenReturn(Data.EXAMS);

        Optional<Exam> exam = service.findOptionalExamByName("OOP with Java");

        assertTrue(exam.isPresent());
        assertEquals(1L, exam.orElseThrow().getId());
        assertEquals("OOP with Java", exam.get().getName());

    }

    @Test
    void findOptionalExamByNameEmptyListWithMockito() {

        List<Exam> exams = Collections.emptyList();

        when(examRepository.findAll()).thenReturn(exams);

        Optional<Exam> exam = service.findOptionalExamByName("OOP with Java");

        assertTrue(!exam.isPresent());
        assertFalse(exam.isPresent());

        /*Ya no tiene sentido testear esto porque falla al no encontrar valores
        assertEquals(1L, exam.orElseThrow().getId());
        assertEquals("OOP with Java", exam.get().getName());*/

    }

    @Test
    void examQuestionsTest() {

        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        //when(questionRepository.findQuestionsByExamId(3L)).thenReturn(Data.QUESTIONS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        Exam exam = service.findExamByNameWithQuestions("Programming with Javascript");
        assertEquals(3, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 1"));

    }

    @Test
    void examQuestionsVerifyTest() {

        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        Exam exam = service.findExamByNameWithQuestions("Programming with Javascript");
        assertEquals(3, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 1"));

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());

    }

    @Test
    @Disabled
    void noExistsExamVerifyTest() {
        //GIVEN
        when(examRepository.findAll()).thenReturn(Collections.emptyList());
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        //WHEN
        Exam exam = service.findExamByNameWithQuestions("Programming with Javascript");

        //THEN
        assertNull(exam);
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(3L);

    }

    @Test
    void saveExamTest() {
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS); //Para que pase el 2do verify
        when(examRepository.save(any(Exam.class))).thenReturn(Data.EXAM);
        Exam exam = service.save(newExam);
        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Reactive programming", exam.getName());

        verify(examRepository).save(any(Exam.class));
        verify(questionRepository).saveManyQuestions(anyList());

    }

    @Test
    void saveExamIncrementalIdTest() {

        //GIVEN
        Exam newExam = Data.EXAM_INCREMENTAL_ID;
        newExam.setQuestions(Data.QUESTIONS);

        //Para modificar el id del examen
        when(examRepository.save(any(Exam.class))).then(new Answer<Exam>() {

            Long sequence = 8L;

            @Override
            public Exam answer(InvocationOnMock invocationOnMock) throws Throwable {
                Exam exam = invocationOnMock.getArgument(0);
                exam.setId(sequence++);
                return exam;
            }
        });

        //WHEN
        Exam exam = service.save(newExam);

        //THEN
        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Redisson", exam.getName());

        verify(examRepository).save(any(Exam.class));
        verify(questionRepository).saveManyQuestions(anyList());

    }

    @Test
    void handleExceptionTest() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS_ID_NULL);
        when(questionRepository.findQuestionsByExamId(isNull())).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamByNameWithQuestions("OOP with Java");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(isNull());
    }

    @Test
    void argumentMatchersTest() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("OOP with Java");

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(arg -> arg!=null && arg.equals(1L)));
        verify(questionRepository).findQuestionsByExamId(eq(1L));

    }

    @Test
    void argumentMatchersTest2() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        //Usar esta línea para que falle
        //when(examRepository.findAll()).thenReturn(Data.EXAMS_ID_NEGATIVE);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("OOP with Java");

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(new MyArgsMatchers()));

        /* La desventaja de hacerlo de esta forma es que no podemos enviar el mensaje personalizado
        porque solo estamos usando el matches() */
        verify(questionRepository).findQuestionsByExamId(argThat( (argument) -> argument!=null && argument>0 ));

    }

    public static class MyArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument!=null && argument>0;
        }

        @Override
        public String toString() {
            return "Custom message of Mockito in case of error. El argument es " + argument;
        }
    }

    @Test
    void testArgumentCaptor() {

        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        //when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Programming with Python");

        //Se agregó anotación al comienzo
        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(questionRepository).findQuestionsByExamId(captor.capture());
        assertEquals(2L, captor.getValue());

    }

    @Test
    void doThrowTest() {
        Exam exam = Data.EXAM;
        exam.setQuestions(Data.QUESTIONS);

        doThrow(IllegalArgumentException.class).when(questionRepository).saveManyQuestions(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.save(exam);
        });
    }

    @Test
    void doAnswerTest() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 2L ? Data.QUESTIONS : Collections.emptyList();
        }).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = service.findExamByNameWithQuestions("Programming with Python");
        assertEquals(3, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Question 2"));
        assertEquals(2L, exam.getId());
        assertEquals("Programming with Python", exam.getName());

        verify(questionRepository).findQuestionsByExamId(anyLong());

    }

    @Test
    void saveExamIncrementalIdWithDoAnswerTest() {

        //GIVEN
        Exam newExam = Data.EXAM_INCREMENTAL_ID;
        newExam.setQuestions(Data.QUESTIONS);

        //Para modificar el id del examen
        doAnswer(new Answer<Exam>() {

            Long sequence = 8L;

            @Override
            public Exam answer(InvocationOnMock invocationOnMock) throws Throwable {
                Exam exam = invocationOnMock.getArgument(0);
                exam.setId(sequence++);
                return exam;
            }
        }).when(examRepository).save(any(Exam.class));

        //WHEN
        Exam exam = service.save(newExam);

        //THEN
        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Redisson", exam.getName());

        verify(examRepository).save(any(Exam.class));
        verify(questionRepository).saveManyQuestions(anyList());

    }

    @Test
    void doCallRealMethodTest() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        //Simulando el método:
        //when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        //Invocando al método real:
        doCallRealMethod().when(questionRepository).findQuestionsByExamId(anyLong());


        Exam exam = service.findExamByNameWithQuestions("OOP with Java");
        assertEquals(1L, exam.getId());
        assertEquals("OOP with Java", exam.getName());

    }

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

    @Test
    void invocationOrderTest() {

        when(examRepository.findAll()).thenReturn(Data.EXAMS);

        service.findExamByNameWithQuestions("Programming with Python");
        service.findExamByNameWithQuestions("Programming with Javascript");

        InOrder inOrder = inOrder(questionRepository);
        inOrder.verify(questionRepository).findQuestionsByExamId(2L);
        inOrder.verify(questionRepository).findQuestionsByExamId(3L);

    }

    @Test
    void invocationOrderTest2() {

        when(examRepository.findAll()).thenReturn(Data.EXAMS);

        service.findExamByNameWithQuestions("Programming with Python");
        service.findExamByNameWithQuestions("Programming with Javascript");

        InOrder inOrder = inOrder(examRepository, questionRepository);
        inOrder.verify(examRepository).findAll();
        inOrder.verify(questionRepository).findQuestionsByExamId(2L);

        inOrder.verify(examRepository).findAll();
        inOrder.verify(questionRepository).findQuestionsByExamId(3L);

    }

    @Test
    void numberOfInvocationsTest() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        //Para probar con never()
        //when(examRepository.findAll()).thenReturn(Collections.emptyList());
        service.findExamByNameWithQuestions("Programming with Javascript");

        //verify(questionRepository).findQuestionsByExamId(3L); //falla: Por defecto cuenta 1
        verify(questionRepository, times(2)).findQuestionsByExamId(3L);
        verify(questionRepository, atLeast(1)).findQuestionsByExamId(3L);
        verify(questionRepository, atLeastOnce()).findQuestionsByExamId(3L);
        verify(questionRepository, atMost(4)).findQuestionsByExamId(3L);
        //verify(questionRepository, atMostOnce()).findQuestionsByExamId(3L); Falla
        //verify(questionRepository, never()).findQuestionsByExamId(3L); Falla
        //verifyNoInteractions(questionRepository); Falla



    }
}