package com.scrs.repository;

import com.scrs.model.Student;
import java.util.List;

public interface StudentRepository {
    void save(Student student);
    Student findById(String studentId);
    List<Student> findAll();
    void delete(String studentId);
}
