package com.splitexpense.service;

import com.splitexpense.exception.GroupNotFoundException;
import com.splitexpense.model.Group;
import com.splitexpense.model.Student;
import com.splitexpense.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group createGroup(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Group name is required."
            );
        }

        Group group =
                new Group(name.trim());

        return groupRepository.save(group);
    }

    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    public Group findById(int id) {

        return groupRepository.findById(id)
                .orElseThrow(() ->
                        new GroupNotFoundException(
                                "Group not found: " + id
                        )
                );
    }

    public void addMember(
            int groupId,
            Student student) {

        Group group = findById(groupId);

        if (!group.addMember(student)) {
            throw new IllegalArgumentException(
                    "Student is already in the group."
            );
        }

        groupRepository.save(group);
    }

    public void removeMember(
            int groupId,
            Student student) {

        Group group = findById(groupId);

        if (!group.removeMember(student)) {
            throw new IllegalArgumentException(
                    "Student is not a member of this group."
            );
        }

        groupRepository.save(group);
    }
}