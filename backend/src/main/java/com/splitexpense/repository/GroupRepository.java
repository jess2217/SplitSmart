package com.splitexpense.repository;

import com.splitexpense.model.Group;
import com.splitexpense.model.Student;
import com.splitexpense.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository
        extends JpaRepository<Group, Integer> {

    // Groups created by the user
    List<Group> findByOwner(User owner);

    // Groups where the user's Student record is a member
    List<Group> findByMembers(Student student);

    // Groups owned by OR containing the user's Student record
    List<Group> findByOwnerOrMembers(
            User owner,
            Student student
    );
}