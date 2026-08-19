package com.splitexpense.controller;

import com.splitexpense.model.Student;
import com.splitexpense.model.Group;
import com.splitexpense.model.User;
import com.splitexpense.repository.GroupRepository;
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

    public GroupController(
            GroupRepository groupRepository,
            UserRepository userRepository) {

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE GROUP
    // =========================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Group createGroup(
            @RequestParam int userId,
            @RequestBody CreateGroupRequest request) {

        if (request == null ||
                request.name() == null ||
                request.name().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Group name is required."
            );
        }

        User owner =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found."
                                )
                        );
Group group =
        new Group(
                request.name().trim()
        );

group.setOwner(owner);

if (owner.getStudent() == null) {

    Student student =
            new Student(
                    owner.getName(),
                    owner.getEmail(),
                    ""
            );

    owner.setStudent(student);

    owner =
            userRepository.save(owner);
}

group.addMember(owner.getStudent());

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

        /*
         * Existing users may have been created
         * before the User -> Student relationship
         * was added.
         *
         * Create the missing Student automatically.
         */
        if (user.getStudent() == null) {

            Student student =
                    new Student(
                            user.getName(),
                            user.getEmail(),
                            ""
                    );

            user.setStudent(student);

            user =
                    userRepository.save(user);
        }

        /*
         * Return groups where the user is either:
         * 1. The owner
         * 2. A member
         */
        return groupRepository.findByOwnerOrMembers(
                user,
                user.getStudent()
        );
    }

    // =========================
    // GET ONE GROUP
    // =========================

    @GetMapping("/{id}")
    public Group getGroup(
            @PathVariable int id,
            @RequestParam int userId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found."
                                )
                        );

        Group group =
                groupRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Group not found."
                                )
                        );

        /*
         * Allow access if the user is:
         * 1. The owner
         * OR
         * 2. A member of the group
         */
        boolean isOwner =
                group.getOwner() != null &&
                group.getOwner().getId() == user.getId();

        boolean isMember =
                user.getStudent() != null &&
                group.getMembers().contains(user.getStudent());

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

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User not found."
                                )
                        );

        Group group =
                groupRepository.findById(groupId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Group not found."
                                )
                        );

        /*
         * Only the owner can delete the group.
         */
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
    // REQUEST DTO
    // =========================

    public record CreateGroupRequest(
            String name
    ) {
    }
}