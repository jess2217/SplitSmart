package com.splitexpense.controller;

import com.splitexpense.model.Group;
import com.splitexpense.model.Student;
import com.splitexpense.repository.GroupRepository;
import com.splitexpense.repository.StudentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
@CrossOrigin(origins = "http://localhost:5173")
public class MemberController {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;

    public MemberController(
            GroupRepository groupRepository,
            StudentRepository studentRepository) {

        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
    }
@DeleteMapping("/{studentId}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteMember(
        @PathVariable int groupId,
        @PathVariable int studentId) {

    Group group = getGroup(groupId);

    Student student =
            group.getMembers()
                    .stream()
                    .filter(member ->
                            member.getId() == studentId)
                    .findFirst()
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Student not found in this group."
                            )
                    );

    boolean removed =
            group.removeMember(student);

    if (!removed) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Could not remove member."
        );
    }

    groupRepository.save(group);
}

    @GetMapping
    public List<Student> getMembers(
            @PathVariable int groupId) {

        Group group = getGroup(groupId);

        return group.getMembers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student addMember(
            @PathVariable int groupId,
            @RequestBody CreateStudentRequest request) {

        Group group = getGroup(groupId);

        if (request == null ||
                request.name() == null ||
                request.name().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Student name is required."
            );
        }

        Student student = new Student(
                request.name().trim(),
                request.email(),
                request.college()
        );

        student = studentRepository.save(student);

        if (!group.addMember(student)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Student is already in the group."
            );
        }

        groupRepository.save(group);

        return student;
    }

    private Group getGroup(int groupId) {

        return groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Group not found."
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