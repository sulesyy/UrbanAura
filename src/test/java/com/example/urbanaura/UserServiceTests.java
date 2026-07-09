package com.example.urbanaura;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTests {

    @TempDir
    Path temporaryDirectory;

    @Test
    void registeredUserCanLogInAfterServiceRestarts() {
        Path usersFile = temporaryDirectory.resolve("users.properties");
        UserService initialService = new UserService(usersFile.toString());

        UserService.RegisterResult registration = initialService.register(
                "Sule Yilmaz",
                "sule@example.com",
                "Gizli123",
                "Luks & Konforlu",
                true
        );

        initialService.toggleFavorite("sule@example.com", "france");
        initialService.saveRecommendation("sule@example.com", "france");
        initialService.updateFavoriteMeta("sule@example.com", "france", "Paris ve sakin mahalleler", "culture");
        UserService restartedService = new UserService(usersFile.toString());

        assertTrue(registration.success());
        AuthUser restartedUser = restartedService.login("SULE@example.com", "Gizli123").orElseThrow();
        assertTrue(restartedUser.favoriteCountries().contains("france"));
        assertEquals("france", restartedUser.recommendedCountry());
        assertEquals("Paris ve sakin mahalleler", restartedUser.favoriteNotes().get("france"));
        assertEquals("culture", restartedUser.favoriteCollections().get("france"));
        assertFalse(restartedService.login("sule@example.com", "Yanlis123").isPresent());
    }
}
