package com.neolayer.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthClientResponse {

    private Long id;
    private String clientId;
    private String clientSecret;
    private String projectName;
    private String projectDescription;
    private String redirectUri;
    private String allowedScopes;
    private Boolean enabled;
    private Boolean isConfidential;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsed;

}
