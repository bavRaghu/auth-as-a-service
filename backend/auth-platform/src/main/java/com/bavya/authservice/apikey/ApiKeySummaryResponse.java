package com.bavya.authservice.apikey;

import java.time.LocalDateTime;

public record ApiKeySummaryResponse(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime lastUsedAt
) {
}