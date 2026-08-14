package com.splitexpense.controller;

import com.splitexpense.model.Group;
import com.splitexpense.model.Student;
import com.splitexpense.repository.GroupRepository;
import com.splitexpense.service.BalanceService;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups/{groupId}/balances")
@CrossOrigin(origins = "http://localhost:5173")
public class BalanceController {

    private final GroupRepository groupRepository;
    private final BalanceService balanceService;

    public BalanceController(
            GroupRepository groupRepository,
            BalanceService balanceService) {

        this.groupRepository = groupRepository;
        this.balanceService = balanceService;
    }

    @GetMapping
    public List<BalanceResponse> getBalances(
            @PathVariable int groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Group not found: " + groupId
                        )
                );

        Map<Student, Double> balances =
                balanceService.calculateBalances(group);

        List<BalanceResponse> response =
                new ArrayList<>();

        for (Map.Entry<Student, Double> entry :
                balances.entrySet()) {

            Student student = entry.getKey();

            response.add(
                    new BalanceResponse(
                            student.getId(),
                            student.getName(),
                            entry.getValue()
                    )
            );
        }

        return response;
    }

    public record BalanceResponse(
            int studentId,
            String studentName,
            double balance
    ) {
    }
}