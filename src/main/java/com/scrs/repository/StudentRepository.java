package com.scrs.repository;

import com.scrs.model.Student;
import java.util.List;

public interface StudentRepository {
    void save(Student student);
    Student findById(String studentId);

    //  allows login by name too
    Student findByName(String name);

    List<Student> findAll();
}
