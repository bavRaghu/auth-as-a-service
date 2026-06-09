package com.bavya.authservice.apikey;

import com.bavya.authservice.apikey.*;
import com.bavya.authservice.project.*;
import com.bavya.authservice.user.User;
import com.bavya.authservice.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String generateKey() {

        return "sk_live_" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "");
    }

    public ApiKeyResponse createApiKey(
            Long projectId,
            CreateApiKeyRequest request
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User currentUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        ProjectMember membership =
                projectMemberRepository
                        .findByProjectIdAndUser(
                                projectId,
                                currentUser
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Not a member of project"
                                )
                        );

        if (membership.getRole() != Role.OWNER) {
            throw new RuntimeException(
                    "Only owners can create API keys"
            );
        }

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow();

        String rawKey =
                generateKey();

        ApiKey apiKey =
                new ApiKey();

        apiKey.setName(
                request.name()
        );

        apiKey.setProject(
                project
        );

        apiKey.setKeyHash(
                passwordEncoder.encode(rawKey)
        );

        apiKeyRepository.save(apiKey);

        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getName(),
                rawKey
        );
    }

    public List<ApiKeySummaryResponse> getApiKeys(
            Long projectId
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User currentUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        projectMemberRepository
                .findByProjectIdAndUser(
                        projectId,
                        currentUser
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Not a member of project"
                        )
                );

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow();

        return apiKeyRepository
                .findByProject(project)
                .stream()
                .map(key ->
                        new ApiKeySummaryResponse(
                                key.getId(),
                                key.getName(),
                                key.getCreatedAt(),
                                key.getLastUsedAt()
                        )
                )
                .toList();
    }

    @Transactional
    public void deleteApiKey(
            Long projectId,
            Long apiKeyId
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User currentUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        ProjectMember membership =
                projectMemberRepository
                        .findByProjectIdAndUser(
                                projectId,
                                currentUser
                        )
                        .orElseThrow();

        if (membership.getRole() != Role.OWNER) {

            throw new RuntimeException(
                    "Only owners can delete API keys"
            );
        }

        ApiKey apiKey =
                apiKeyRepository
                        .findById(apiKeyId)
                        .orElseThrow();

        if (
                !apiKey.getProject()
                        .getId()
                        .equals(projectId)
        ) {

            throw new RuntimeException(
                    "API key does not belong to project"
            );
        }

        apiKeyRepository.delete(apiKey);
    }

    public Project validateApiKey(
            String rawKey
    ) {

        List<ApiKey> apiKeys =
                apiKeyRepository.findAll();

        for (ApiKey apiKey : apiKeys) {

            if (
                    passwordEncoder.matches(
                            rawKey,
                            apiKey.getKeyHash()
                    )
            ) {

                apiKey.setLastUsedAt(
                        LocalDateTime.now()
                );

                apiKeyRepository.save(apiKey);

                return apiKey.getProject();
            }
        }

        throw new RuntimeException(
                "Invalid API key"
        );
    }
}