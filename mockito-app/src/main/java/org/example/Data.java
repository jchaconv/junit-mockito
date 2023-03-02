package org.example;

import org.example.models.Exam;

import java.util.Arrays;
import java.util.List;

public class Data {

    public final static List<Exam> EXAMS = Arrays.asList(
            new Exam(1L, "OOP with Java"),
            new Exam(2L, "Programming with Python"),
            new Exam(3L, "Programming with Javascript")
    );

    public final static List<Exam> EXAMS_ID_NEGATIVE = Arrays.asList(
            new Exam(-1L, "OOP with Java"),
            new Exam(null, "Programming with Python")
    );

    public final static List<Exam> EXAMS_ID_NULL = Arrays.asList(
            new Exam(null, "OOP with Java"),
            new Exam(null, "Programming with Python"),
            new Exam(null, "Programming with Javascript")
    );

    public final static List<String> QUESTIONS = Arrays.asList(
            "Question 1",
            "Question 2",
            "Question 3"
    );

    public final static Exam EXAM = new Exam(8L, "Reactive programming");

    public final static Exam EXAM_INCREMENTAL_ID = new Exam(null, "Redisson");


}
