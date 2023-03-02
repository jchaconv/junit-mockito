package org.example.repositories;

import org.example.Data;
import org.example.models.Exam;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamRepositoryImpl implements ExamRepository {

    @Override
    public List<Exam> findAll() {
        //return Collections.emptyList(); --Con esto falla el test
        /*return Arrays.asList(
                new Exam(1L, "OOP with Java"),
                new Exam(2L, "Programming with Python"),
                new Exam(3L, "Programming with Javascript")
        );*/
        try {
            System.out.println("ExamRepositoryImpl.findAll");
            TimeUnit.SECONDS.sleep(3);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return Data.EXAMS;
    }

    @Override
    public Exam save(Exam exam) {
        System.out.println("ExamRepositoryImpl.save");
        return Data.EXAM;
    }
}
