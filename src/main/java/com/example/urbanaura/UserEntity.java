package com.example.urbanaura;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, length = 512)
    private String passwordHash;

    @Column(nullable = false)
    private String style;

    private String recommendedCountry = "";

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<FavoriteCountryEntity> favorites = new LinkedHashSet<>();

    protected UserEntity() {
    }

    public UserEntity(String email, String fullName, String passwordHash, String style, String recommendedCountry) {
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.style = style;
        this.recommendedCountry = recommendedCountry == null ? "" : recommendedCountry;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getRecommendedCountry() {
        return recommendedCountry;
    }

    public void setRecommendedCountry(String recommendedCountry) {
        this.recommendedCountry = recommendedCountry == null ? "" : recommendedCountry;
    }

    public Set<FavoriteCountryEntity> getFavorites() {
        return favorites;
    }

    public void replaceFavorites(Set<FavoriteCountryEntity> favorites) {
        this.favorites.clear();
        favorites.forEach(this::addFavorite);
    }

    public void addFavorite(FavoriteCountryEntity favorite) {
        favorite.setUser(this);
        favorites.add(favorite);
    }
}
