package com.neolayer.identity.service;

import com.neolayer.identity.dto.CreateOAuthClientRequest;
import com.neolayer.identity.dto.OAuthClientResponse;
import com.neolayer.identity.entity.OAuthClient;
import com.neolayer.identity.entity.User;
import com.neolayer.identity.repository.OAuthClientRepository;
import com.neolayer.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthClientService {

    private final OAuthClientRepository oauthClientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Generate a unique client ID
     */
    private String generateClientId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a client secret as a lowercase hex string (32 bytes = 64 hex chars)
     */
    private String generateClientSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Create a new OAuth client
     */
    @Transactional
    public OAuthClientResponse createOAuthClient(Long userId, CreateOAuthClientRequest request) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate client ID and secret
        String clientId = generateClientId();
        String clientSecret = generateClientSecret();

        // Ensure client ID is unique
        while (oauthClientRepository.existsByClientId(clientId)) {
            clientId = generateClientId();
        }

        // Create OAuth client
        OAuthClient oauthClient = OAuthClient.builder()
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(clientSecret))
                .user(user)
                .projectName(request.getProjectName())
                .projectDescription(request.getProjectDescription())
                .redirectUri(request.getRedirectUri())
                .allowedScopes(request.getAllowedScopes())
                .enabled(true)
                .isConfidential(true)
                .build();

        OAuthClient savedClient = oauthClientRepository.save(oauthClient);
        log.info("OAuth client created for user: {} with client_id: {}", user.getEmail(), clientId);

        return convertToResponse(savedClient, clientSecret); // Return plain secret only on creation
    }

    /**
     * Get OAuth client by ID
     */
    public OAuthClientResponse getOAuthClientById(Long id, Long userId) {
        OAuthClient client = oauthClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OAuth client not found"));

        // Verify ownership
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to access this client");
        }

        return convertToResponse(client, null); // Don't return secret on fetch
    }

    /**
     * Get all OAuth clients for a user
     */
    public List<OAuthClientResponse> getOAuthClientsByUserId(Long userId) {
        List<OAuthClient> clients = oauthClientRepository.findByUserId(userId);
        return clients.stream()
                .map(client -> convertToResponse(client, null))
                .collect(Collectors.toList());
    }

    /**
     * Update OAuth client
     */
    @Transactional
    public OAuthClientResponse updateOAuthClient(Long id, Long userId, CreateOAuthClientRequest request) {
        OAuthClient client = oauthClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OAuth client not found"));

        // Verify ownership
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to update this client");
        }

        client.setProjectName(request.getProjectName());
        client.setProjectDescription(request.getProjectDescription());
        client.setRedirectUri(request.getRedirectUri());
        client.setAllowedScopes(request.getAllowedScopes());

        OAuthClient updatedClient = oauthClientRepository.save(client);
        log.info("OAuth client updated: {}", client.getClientId());

        return convertToResponse(updatedClient, null);
    }

    /**
     * Regenerate client secret
     */
    @Transactional
    public OAuthClientResponse regenerateClientSecret(Long id, Long userId) {
        OAuthClient client = oauthClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OAuth client not found"));

        // Verify ownership
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to update this client");
        }

        String newSecret = generateClientSecret();
        client.setClientSecret(passwordEncoder.encode(newSecret));

        OAuthClient updatedClient = oauthClientRepository.save(client);
        log.info("OAuth client secret regenerated: {}", client.getClientId());

        return convertToResponse(updatedClient, newSecret);
    }

    /**
     * Delete OAuth client
     */
    @Transactional
    public void deleteOAuthClient(Long id, Long userId) {
        OAuthClient client = oauthClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OAuth client not found"));

        // Verify ownership
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to delete this client");
        }

        oauthClientRepository.delete(client);
        log.info("OAuth client deleted: {}", client.getClientId());
    }

    /**
     * Validate client credentials
     */
    public boolean validateClientCredentials(String clientId, String clientSecret) {
        OAuthClient client = oauthClientRepository.findByClientId(clientId)
                .orElse(null);

        if (client == null || !client.getEnabled()) {
            return false;
        }

        return passwordEncoder.matches(clientSecret, client.getClientSecret());
    }

    /**
     * Convert entity to response DTO
     */
    private OAuthClientResponse convertToResponse(OAuthClient client, String plainSecret) {
        return OAuthClientResponse.builder()
                .id(client.getId())
                .clientId(client.getClientId())
                .secretCode(plainSecret != null ? plainSecret : "****") // Only show plain secret on creation
                .projectName(client.getProjectName())
                .projectDescription(client.getProjectDescription())
                .redirectUri(client.getRedirectUri())
                .allowedScopes(client.getAllowedScopes())
                .enabled(client.getEnabled())
                .isConfidential(client.getIsConfidential())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .lastUsed(client.getLastUsed())
                .build();
    }

}
