package com.splitexpense.controller;

import com.splitexpense.model.Student;
import com.splitexpense.repository.StudentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(
            @RequestBody CreateStudentRequest request) {

        if (request == null ||
                request.name() == null ||
                request.name().isBlank()) {

            throw new IllegalArgumentException(
                    "Student name is required."
            );
        }

        Student student = new Student(
                request.name().trim(),
                request.email(),
                request.college()
        );

        return studentRepository.save(student);
    }

    @GetMapping
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student getStudent(
            @PathVariable int id) {

        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found: " + id
                        )
                );
    }

    public record CreateStudentRequest(
            String name,
            String email,
            String college
    ) {
    }
}