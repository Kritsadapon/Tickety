package io.muzoo.ssc.project.backend.Tickets;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // Create a new ticket
    @PostMapping("/create")
    public ResponseEntity<?> createTicket(
            @RequestParam Long teamId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) Long flowId,
            @RequestParam(required = false) Set<Long> dependencyIds) {
        try {
            Ticket ticket = ticketService.createTicket(teamId, title, description, flowId, dependencyIds);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Assign a ticket to a user
    @PostMapping("/{ticketId}/assign")
    public ResponseEntity<?> assignTicket(
            @PathVariable Long ticketId,
            @RequestParam String username) {
        try {
            Ticket ticket = ticketService.assignTicket(ticketId, username);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Update ticket status
    @PostMapping("/{ticketId}/status")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam TicketStatus newStatus) {
        try {
            Ticket ticket = ticketService.updateTicketStatus(ticketId, newStatus);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Get available tickets for a team
    @GetMapping("/team/{teamId}/available")
    public ResponseEntity<?> getAvailableTickets(@PathVariable Long teamId) {
        try {
            return ResponseEntity.ok(ticketService.getAvailableTickets(teamId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Get tickets in a flow
    @GetMapping("/flow/{flowId}")
    public ResponseEntity<?> getTicketsInFlow(@PathVariable Long flowId) {
        try {
            return ResponseEntity.ok(ticketService.getTicketsInFlow(flowId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Add dependency to a ticket
    @PostMapping("/{ticketId}/dependencies/add/{dependencyId}")
    public ResponseEntity<?> addDependency(
            @PathVariable Long ticketId,
            @PathVariable Long dependencyId) {
        try {
            Ticket ticket = ticketService.addDependency(ticketId, dependencyId);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Remove dependency from a ticket
    @PostMapping("/{ticketId}/dependencies/remove/{dependencyId}")
    public ResponseEntity<?> removeDependency(
            @PathVariable Long ticketId,
            @PathVariable Long dependencyId) {
        try {
            Ticket ticket = ticketService.removeDependency(ticketId, dependencyId);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
} 