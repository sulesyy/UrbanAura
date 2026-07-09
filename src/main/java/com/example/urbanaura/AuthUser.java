package com.example.urbanaura;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public record AuthUser(String fullName, String email, String passwordHash, String style,
                       Set<String> favoriteCountries, String recommendedCountry,
                       Map<String, String> favoriteNotes, Map<String, String> favoriteCollections) {
    public AuthUser(String fullName, String email, String passwordHash, String style,
                    Set<String> favoriteCountries, String recommendedCountry) {
        this(fullName, email, passwordHash, style, favoriteCountries, recommendedCountry, Map.of(), Map.of());
    }

    public AuthUser {
        favoriteCountries = Set.copyOf(new LinkedHashSet<>(favoriteCountries));
        recommendedCountry = recommendedCountry == null ? "" : recommendedCountry;
        favoriteNotes = Map.copyOf(favoriteNotes);
        favoriteCollections = Map.copyOf(favoriteCollections);
    }
}
