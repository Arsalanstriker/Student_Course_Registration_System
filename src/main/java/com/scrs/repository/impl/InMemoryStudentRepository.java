package com.scrs.repository.impl;

import com.scrs.model.Student;
import com.scrs.repository.StudentRepository;
import com.scrs.repository.InMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

public class InMemoryStudentRepository implements StudentRepository {
    private final InMemoryDatabase db = InMemoryDatabase.getInstance();

    @Override
    public void save(Student student) {
        db.getStudents().put(student.getStudentId(), student);
    }

    @Override
    public Student findById(String studentId) {
        return db.getStudents().get(studentId);
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(db.getStudents().values());
    }

    @Override
    public void delete(String studentId) {
        db.getStudents().remove(studentId);
    }
}
