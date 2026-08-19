package com.splitexpense.controller;

import com.splitexpense.model.Group;
import com.splitexpense.model.Student;
import com.splitexpense.model.User;
import com.splitexpense.repository.GroupRepository;
import com.splitexpense.repository.StudentRepository;
import com.splitexpense.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
public class MemberController {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public MemberController(
            GroupRepository groupRepository,
            StudentRepository studentRepository,
            UserRepository userRepository) {

        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // DELETE MEMBER
    // =========================

    @DeleteMapping("/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(
            @PathVariable int groupId,
            @PathVariable int studentId,
         @RequestParam int userId) {

        Group group = getGroup(groupId);
User user =
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found."
                        )
                );

// Only the group owner can remove members
if (group.getOwner() == null ||
        group.getOwner().getId() != user.getId()) {

    throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "Only the group owner can remove members."
    );
}
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

    // =========================
    // GET MEMBERS
    // =========================

    @GetMapping
    public List<Student> getMembers(
            @PathVariable int groupId) {

        Group group = getGroup(groupId);

        return group.getMembers();
    }

    // =========================
    // ADD MEMBER
    // =========================
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Student addMember(
        @PathVariable int groupId,
        @RequestBody CreateStudentRequest request) {

    Group group = getGroup(groupId);

    // -------------------------
    // VALIDATE REQUEST
    // -------------------------

    if (request == null ||
            request.name() == null ||
            request.name().isBlank()) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Student name is required."
        );
    }

    if (request.email() == null ||
            request.email().isBlank()) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Student email is required."
        );
    }

    String email =
            request.email()
                    .trim()
                    .toLowerCase();

    // -------------------------
    // FIND REGISTERED USER
    // -------------------------

    User user =
            userRepository.findByEmail(email)
                    .orElse(null);

    Student student;

    if (user != null) {

        /*
         * Registered user:
         * ALWAYS use the Student connected
         * to that User account.
         */
        student = user.getStudent();

        /*
         * Older accounts may not have a Student.
         * Create one and attach it to the User.
         */
        if (student == null) {

            student =
                    new Student(
                            user.getName(),
                            user.getEmail(),
                            request.college() == null
                                    ? ""
                                    : request.college()
                    );

            user.setStudent(student);

            userRepository.save(user);
        }

    } else {

        /*
         * No registered account exists.
         * Look for an existing Student.
         */
        student =
                studentRepository
                        .findByEmail(email)
                        .orElse(null);

        /*
         * Create a Student only if one doesn't exist.
         */
        if (student == null) {

            student =
                    new Student(
                            request.name().trim(),
                            email,
                            request.college() == null
                                    ? ""
                                    : request.college()
                    );

            student =
                    studentRepository.save(student);
        }
    }

    // -------------------------
    // ADD MEMBER TO GROUP
    // -------------------------

    if (!group.addMember(student)) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Student is already in the group."
        );
    }

    groupRepository.save(group);

    return student;
}

    // =========================
    // FIND GROUP
    // =========================

    private Group getGroup(int groupId) {

        return groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Group not found."
                        )
                );
    }

    // =========================
    // REQUEST DTO
    // =========================

    public record CreateStudentRequest(
            String name,
            String email,
            String college
    ) {
    }
}