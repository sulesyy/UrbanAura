package com.example.urbanaura;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LifestyleQuizFlowTests {

    @TempDir
    Path temporaryDirectory;

    @SuppressWarnings("unchecked")
    @Test
    void lifestyleQuizRequiresLoginAndReturnsThereAfterAuthentication() throws Exception {
        UserService userService = new UserService(temporaryDirectory.resolve("users.properties").toString());
        userService.register("Quiz User", "quiz@example.com", "Gizli123", "Doğa & Huzur", true);
        PageController controller = new PageController(userService);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
        MockHttpSession session = new MockHttpSession();

        mvc.perform(get("/lifestyles").session(session))
                .andExpect(redirectedUrl("/login"));

        mvc.perform(post("/login")
                        .session(session)
                        .param("email", "quiz@example.com")
                .param("password", "Gizli123"))
                .andExpect(redirectedUrl("/lifestyles"));

        ExtendedModelMap model = new ExtendedModelMap();
        assertEquals("lifestyles", controller.lifestyles(session, model));
        assertTrue(model.containsAttribute("currentUser"));

        mvc.perform(post("/api/favorites/france").session(session))
                .andExpect(status().isOk());
        mvc.perform(post("/api/favorite-meta/france")
                        .session(session)
                        .param("note", "Paris ve kültür rotası")
                        .param("collection", "culture"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/profile/recommendation/france").session(session))
                .andExpect(status().isOk());

        ExtendedModelMap favoritesModel = new ExtendedModelMap();
        assertEquals("favorites", controller.favorites(session, favoritesModel));
        assertEquals(1, favoritesModel.getAttribute("favoriteCount"));
        Map<String, String> favoriteNotes = (Map<String, String>) favoritesModel.getAttribute("favoriteNotes");
        Map<String, String> favoriteCollections = (Map<String, String>) favoritesModel.getAttribute("favoriteCollections");
        assertEquals("Paris ve kültür rotası", favoriteNotes.get("france"));
        assertEquals("culture", favoriteCollections.get("france"));
        PageController.Country recommendation = (PageController.Country) favoritesModel.getAttribute("recommendedCountry");
        assertEquals("Fransa", recommendation.name());
    }
}
