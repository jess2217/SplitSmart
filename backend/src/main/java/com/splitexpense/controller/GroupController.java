package com.splitexpense.controller;

import com.splitexpense.model.Student;
import com.splitexpense.model.Group;
import com.splitexpense.model.User;
import com.splitexpense.repository.GroupRepository;
import com.splitexpense.repository.StudentRepository;
import com.splitexpense.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public GroupController(
            GroupRepository groupRepository,
            UserRepository userRepository,
            StudentRepository studentRepository) {

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    // =========================
    // CREATE GROUP
    // =========================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Group createGroup(
            @RequestParam int userId,
            @RequestBody CreateGroupRequest request) {

        // -------------------------
        // VALIDATE REQUEST
        // -------------------------

        if (request == null ||
                request.name() == null ||
                request.name().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Group name is required."
            );
        }

        // -------------------------
        // FIND OWNER
        // -------------------------

        User owner =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found."
                                )
                        );

        // -------------------------
        // CREATE GROUP
        // -------------------------

        Group group =
                new Group(
                        request.name().trim()
                );

        group.setOwner(owner);

        // -------------------------
        // ENSURE OWNER HAS STUDENT
        // -------------------------

        Student ownerStudent =
                getOrCreateStudent(owner);

        group.addMember(ownerStudent);

        // -------------------------
        // SAVE GROUP
        // -------------------------

        return groupRepository.save(group);
    }

    // =========================
    // GET USER'S GROUPS
    // =========================

    @GetMapping
    public List<Group> getGroups(
            @RequestParam int userId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found."
                                )
                        );

        // -------------------------
        // ENSURE USER HAS STUDENT
        // -------------------------

        Student student =
                getOrCreateStudent(user);

        // -------------------------
        // FIND USER'S GROUPS
        // -------------------------

        return groupRepository.findByOwnerOrMembers(
                user,
                student
        );
    }

    // =========================
    // GET ONE GROUP
    // =========================

    @GetMapping("/{id}")
    public Group getGroup(
            @PathVariable int id,
            @RequestParam int userId) {

        // -------------------------
        // FIND USER
        // -------------------------

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found."
                                )
                        );

        // -------------------------
        // FIND GROUP
        // -------------------------

        Group group =
                groupRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Group not found."
                                )
                        );

        // -------------------------
        // CHECK OWNER
        // -------------------------

        boolean isOwner =
                group.getOwner() != null &&
                group.getOwner().getId() == user.getId();

        // -------------------------
        // CHECK MEMBER
        // -------------------------

        boolean isMember =
                user.getStudent() != null &&
                group.getMembers()
                        .contains(user.getStudent());

        // -------------------------
        // CHECK ACCESS
        // -------------------------

        if (!isOwner && !isMember) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this group."
            );
        }

        return group;
    }

    // =========================
    // DELETE GROUP
    // =========================

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(
            @PathVariable int groupId,
            @RequestParam int userId) {

        // -------------------------
        // FIND USER
        // -------------------------

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found."
                                )
                        );

        // -------------------------
        // FIND GROUP
        // -------------------------

        Group group =
                groupRepository.findById(groupId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Group not found."
                                )
                        );

        // -------------------------
        // ONLY OWNER CAN DELETE
        // -------------------------

        if (group.getOwner() == null ||
                group.getOwner().getId() != user.getId()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot delete this group."
            );
        }

        groupRepository.delete(group);
    }

    // =========================
    // GET OR CREATE STUDENT
    // =========================

    private Student getOrCreateStudent(
            User user) {

        // -------------------------
        // USER ALREADY HAS STUDENT
        // -------------------------

        if (user.getStudent() != null) {

            return user.getStudent();
        }

        // -------------------------
        // NORMALIZE EMAIL
        // -------------------------

        String email =
                user.getEmail()
                        .trim()
                        .toLowerCase();

        // -------------------------
        // FIND EXISTING STUDENT
        // -------------------------

        Student student =
                studentRepository
                        .findByEmail(email)
                        .orElse(null);

        // -------------------------
        // CREATE ONLY IF NEEDED
        // -------------------------

        if (student == null) {

            student =
                    new Student(
                            user.getName()
                                    .trim(),
                            email,
                            ""
                    );

            student =
                    studentRepository.save(student);
        }

        // -------------------------
        // LINK STUDENT TO USER
        // -------------------------

        user.setStudent(student);

        userRepository.save(user);

        return student;
    }

    // =========================
    // REQUEST DTO
    // =========================

    public record CreateGroupRequest(
            String name
    ) {
    }
}