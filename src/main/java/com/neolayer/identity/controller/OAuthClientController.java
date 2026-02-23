package com.neolayer.identity.controller;

import com.neolayer.identity.dto.CreateOAuthClientRequest;
import com.neolayer.identity.dto.OAuthClientResponse;
import com.neolayer.identity.service.OAuthClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.neolayer.identity.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/oauth-clients")
@RequiredArgsConstructor
@Slf4j
public class OAuthClientController {

    private final OAuthClientService oauthClientService;
    private final UserRepository userRepository;

    /**
     * Create a new OAuth client
     * Requires: authenticated user with project details
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OAuthClientResponse> createOAuthClient(
            @Valid @RequestBody CreateOAuthClientRequest request,
            Authentication authentication) {

        log.info("Creating OAuth client for project: {}", request.getProjectName());

        // Get current user ID from authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        OAuthClientResponse response = oauthClientService.createOAuthClient(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all OAuth clients for the authenticated user
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OAuthClientResponse>> getOAuthClients(Authentication authentication) {
        log.info("Fetching OAuth clients for user");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<OAuthClientResponse> clients = oauthClientService.getOAuthClientsByUserId(userId);
        return ResponseEntity.ok(clients);
    }

    /**
     * Get specific OAuth client by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OAuthClientResponse> getOAuthClient(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("Fetching OAuth client with id: {}", id);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        OAuthClientResponse response = oauthClientService.getOAuthClientById(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update OAuth client details
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OAuthClientResponse> updateOAuthClient(
            @PathVariable Long id,
            @Valid @RequestBody CreateOAuthClientRequest request,
            Authentication authentication) {

        log.info("Updating OAuth client with id: {}", id);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        OAuthClientResponse response = oauthClientService.updateOAuthClient(id, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Regenerate client secret
     * WARNING: This will invalidate the old secret
     */
    @PostMapping("/{id}/regenerate-secret")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OAuthClientResponse> regenerateClientSecret(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("Regenerating client secret for client id: {}", id);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        OAuthClientResponse response = oauthClientService.regenerateClientSecret(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete OAuth client
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteOAuthClient(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("Deleting OAuth client with id: {}", id);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        oauthClientService.deleteOAuthClient(id, userId);
        return ResponseEntity.noContent().build();
    }

}
