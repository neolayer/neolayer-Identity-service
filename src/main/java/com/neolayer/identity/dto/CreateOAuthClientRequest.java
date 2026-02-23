package com.neolayer.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOAuthClientRequest {

    @NotBlank(message = "Project name is required")
    private String projectName;

    private String projectDescription;

    @NotBlank(message = "Redirect URI is required")
    @Pattern(regexp = "^https?://.*", message = "Redirect URI must start with http:// or https://")
    private String redirectUri;

    private String allowedScopes; // comma-separated scopes

}
