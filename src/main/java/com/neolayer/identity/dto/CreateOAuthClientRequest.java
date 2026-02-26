package com.neolayer.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOAuthClientRequest {

    private Long userId;

    @NotBlank(message = "Project name is required")
    private String projectName;

    private String projectDescription;

    @NotBlank(message = "Redirect URI is required")
    private String redirectUri;

    private String allowedScopes;

}
