package com.neolayer.identity.repository;

import com.neolayer.identity.entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OAuthClientRepository extends JpaRepository<OAuthClient, Long> {

    Optional<OAuthClient> findByClientId(String clientId);

    List<OAuthClient> findByUserId(Long userId);

    Optional<OAuthClient> findByClientIdAndClientSecret(String clientId, String clientSecret);

    boolean existsByClientId(String clientId);

}
