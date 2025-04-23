package io.muzoo.ssc.project.backend.Tickets;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flows")
public class TicketFlowController {

    @Autowired
    private TicketFlowService ticketFlowService;

    // Create a new ticket flow
    @PostMapping("/create")
    public ResponseEntity<?> createFlow(
            @RequestParam Long teamId,
            @RequestParam String name,
            @RequestParam String description) {
        try {
            TicketFlow flow = ticketFlowService.createFlow(teamId, name, description);
            return ResponseEntity.ok(flow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Update a ticket flow
    @PutMapping("/{flowId}")
    public ResponseEntity<?> updateFlow(
            @PathVariable Long flowId,
            @RequestParam String name,
            @RequestParam String description) {
        try {
            TicketFlow flow = ticketFlowService.updateFlow(flowId, name, description);
            return ResponseEntity.ok(flow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Delete a ticket flow
    @DeleteMapping("/{flowId}")
    public ResponseEntity<?> deleteFlow(@PathVariable Long flowId) {
        try {
            ticketFlowService.deleteFlow(flowId);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true)
                    .message("Flow deleted successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Get all flows for a team
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getTeamFlows(@PathVariable Long teamId) {
        try {
            return ResponseEntity.ok(ticketFlowService.getTeamFlows(teamId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Get a specific flow
    @GetMapping("/{flowId}")
    public ResponseEntity<?> getFlow(@PathVariable Long flowId) {
        try {
            return ResponseEntity.ok(ticketFlowService.getFlow(flowId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
} 