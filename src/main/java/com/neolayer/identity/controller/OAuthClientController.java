package com.neolayer.identity.controller;

import com.neolayer.identity.dto.CreateOAuthClientRequest;
import com.neolayer.identity.dto.OAuthClientResponse;
import com.neolayer.identity.service.OAuthClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "OAuth Clients", description = "OAuth Client ID and Secret management for applications")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
public class OAuthClientController {

    private final OAuthClientService oauthClientService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create OAuth Client",
            description = "Generate a new OAuth client with unique ID and secret for your application. " +
                    "Project details are required to identify the application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OAuth client created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
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

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "List OAuth Clients",
            description = "Get all OAuth clients created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of OAuth clients"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<OAuthClientResponse>> getOAuthClients(Authentication authentication) {
        log.info("Fetching OAuth clients for user");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<OAuthClientResponse> clients = oauthClientService.getOAuthClientsByUserId(userId);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get OAuth Client Details",
            description = "Retrieve detailed information for a specific OAuth client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OAuth client details"),
            @ApiResponse(responseCode = "404", description = "OAuth client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<OAuthClientResponse> getOAuthClient(
            @PathVariable @Parameter(description = "OAuth Client ID") Long id,
            Authentication authentication) {

        log.info("Fetching OAuth client with id: {}", id);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        OAuthClientResponse response = oauthClientService.getOAuthClientById(id, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update OAuth Client",
            description = "Update project details for an existing OAuth client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OAuth client updated successfully"),
            @ApiResponse(responseCode = "404", description = "OAuth client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<OAuthClientResponse> updateOAuthClient(
            @PathVariable @Parameter(description = "OAuth Client ID") Long id,
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

    @PostMapping("/{id}/regenerate-secret")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Regenerate Client Secret",
            description = "Generate a new client secret. WARNING: This will invalidate the old secret immediately!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client secret regenerated successfully"),
            @ApiResponse(responseCode = "404", description = "OAuth client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<OAuthClientResponse> regenerateClientSecret(
            @PathVariable @Parameter(description = "OAuth Client ID") Long id,
            Authentication authentication) {

        log.info("Regenerating client secret for client id: {}", id);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        OAuthClientResponse response = oauthClientService.regenerateClientSecret(id, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Delete OAuth Client",
            description = "Remove an OAuth client. Applications using this client will no longer be authorized.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OAuth client deleted successfully"),
            @ApiResponse(responseCode = "404", description = "OAuth client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteOAuthClient(
            @PathVariable @Parameter(description = "OAuth Client ID") Long id,
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
