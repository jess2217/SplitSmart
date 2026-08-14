package com.splitexpense.controller;

import com.splitexpense.model.Group;
import com.splitexpense.repository.GroupRepository;
import com.splitexpense.service.SettlementService;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/settlements")
@CrossOrigin(origins = "http://localhost:5173")
public class SettlementController {

    private final GroupRepository groupRepository;
    private final SettlementService settlementService;

    public SettlementController(
            GroupRepository groupRepository,
            SettlementService settlementService) {

        this.groupRepository = groupRepository;
        this.settlementService = settlementService;
    }

    @GetMapping
    public List<SettlementResponse> getSettlements(
            @PathVariable int groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Group not found: " + groupId
                        )
                );

        List<SettlementService.Settlement> settlements =
                settlementService.calculateSettlements(group);

        List<SettlementResponse> response =
                new ArrayList<>();

        for (SettlementService.Settlement settlement :
                settlements) {

            response.add(
                    new SettlementResponse(
                            settlement.from().getId(),
                            settlement.from().getName(),
                            settlement.to().getId(),
                            settlement.to().getName(),
                            settlement.amount()
                    )
            );
        }

        return response;
    }

    public record SettlementResponse(
            int fromStudentId,
            String fromStudentName,
            int toStudentId,
            String toStudentName,
            double amount
    ) {
    }
}