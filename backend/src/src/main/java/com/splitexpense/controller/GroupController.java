package com.splitexpense.controller;

import com.splitexpense.model.Group;
import com.splitexpense.model.Student;
import com.splitexpense.repository.GroupRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:5173")
public class GroupController {

    private final GroupRepository groupRepository;

    public GroupController(
            GroupRepository groupRepository) {

        this.groupRepository = groupRepository;
    }

    // =========================
    // CREATE GROUP
    // =========================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Group createGroup(
            @RequestBody CreateGroupRequest request) {

        if (request == null ||
                request.name() == null ||
                request.name().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Group name is required."
            );
        }

        Group group =
                new Group(request.name().trim());

        return groupRepository.save(group);
    }

    // =========================
    // DELETE GROUP
    // =========================

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(
            @PathVariable int groupId) {

        Group group =
                groupRepository.findById(groupId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Group not found."
                                )
                        );

        groupRepository.delete(group);
    }

    // =========================
    // DELETE MEMBER FROM GROUP
    // =========================

  
    

   

    // =========================
    // GET ALL GROUPS
    // =========================

    @GetMapping
    public List<Group> getGroups() {

        return groupRepository.findAll();
    }

    // =========================
    // GET ONE GROUP
    // =========================

    @GetMapping("/{id}")
    public Group getGroup(
            @PathVariable int id) {

        return groupRepository.findById(id)
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

    public record CreateGroupRequest(
            String name
    ) {
    }
}