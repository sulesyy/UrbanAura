package com.example.urbanaura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "favorite_countries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_email", "country_slug"}))
public class FavoriteCountryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_slug", nullable = false)
    private String countrySlug;

    @Column(length = 1000)
    private String note = "";

    @Column(nullable = false)
    private String collection = "dream";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", nullable = false)
    private UserEntity user;

    protected FavoriteCountryEntity() {
    }

    public FavoriteCountryEntity(String countrySlug, String note, String collection) {
        this.countrySlug = countrySlug;
        this.note = note == null ? "" : note;
        this.collection = collection == null || collection.isBlank() ? "dream" : collection;
    }

    public String getCountrySlug() {
        return countrySlug;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? "" : note;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection == null || collection.isBlank() ? "dream" : collection;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
