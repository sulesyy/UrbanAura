package com.example.urbanaura;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserService {
    private static final int HASH_ITERATIONS = 120_000;
    private static final int HASH_KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ConcurrentMap<String, AuthUser> users = new ConcurrentHashMap<>();
    private final Path usersFile;
    private final UserRepository userRepository;

    public UserService(@Value("${urbanaura.users-file:data/users.properties}") String usersFile) {
        this.usersFile = Path.of(usersFile).toAbsolutePath().normalize();
        this.userRepository = null;
        loadUsers();
    }

    @Autowired
    public UserService(UserRepository userRepository,
                       @Value("${urbanaura.users-file:data/users.properties}") String usersFile) {
        this.usersFile = Path.of(usersFile).toAbsolutePath().normalize();
        this.userRepository = userRepository;
        loadUsers();
        migrateFileUsersToDatabase();
    }

    public RegisterResult register(String fullName, String email, String password, String style, boolean termsAccepted) {
        String normalizedEmail = normalizeEmail(email);
        String cleanFullName = clean(fullName);
        String cleanStyle = clean(style);

        if (cleanFullName.length() < 2) {
            return RegisterResult.invalid("Ad soyad en az 2 karakter olmalı.");
        }

        if (!normalizedEmail.contains("@") || !normalizedEmail.contains(".")) {
            return RegisterResult.invalid("Geçerli bir e-posta adresi gir.");
        }

        if (password == null || password.length() < 6) {
            return RegisterResult.invalid("Şifre en az 6 karakter olmalı.");
        }

        if (cleanStyle.isBlank()) {
            return RegisterResult.invalid("Bir yaşam tarzı seçmelisin.");
        }

        if (!termsAccepted) {
            return RegisterResult.invalid("Kullanım koşullarını kabul etmelisin.");
        }

        AuthUser user = new AuthUser(cleanFullName, normalizedEmail, hashPassword(password), cleanStyle, Set.of(), "");
        if (databaseBacked()) {
            if (userRepository.existsById(normalizedEmail)) {
                return RegisterResult.invalid("Bu e-posta ile zaten bir hesap var.");
            }
            try {
                userRepository.save(toEntity(user));
            } catch (RuntimeException exception) {
                return RegisterResult.invalid("Hesap kaydedilemedi. Lütfen tekrar dene.");
            }
            return RegisterResult.success(user);
        }

        AuthUser existingUser = users.putIfAbsent(normalizedEmail, user);
        if (existingUser != null) {
            return RegisterResult.invalid("Bu e-posta ile zaten bir hesap var.");
        }

        try {
            saveUsers();
        } catch (IOException exception) {
            users.remove(normalizedEmail, user);
            return RegisterResult.invalid("Hesap kaydedilemedi. Lütfen tekrar dene.");
        }

        return RegisterResult.success(user);
    }

    public Optional<AuthUser> login(String email, String password) {
        AuthUser user = findUser(normalizeEmail(email)).orElse(null);
        if (user == null || password == null || !verifyPassword(password, user.passwordHash())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public synchronized Optional<AuthUser> toggleFavorite(String email, String countrySlug) {
        String normalizedEmail = normalizeEmail(email);
        if (databaseBacked()) {
            return userRepository.findById(normalizedEmail).map(user -> {
                String cleanSlug = clean(countrySlug);
                Optional<FavoriteCountryEntity> existingFavorite = favoriteBySlug(user, cleanSlug);
                if (existingFavorite.isPresent()) {
                    user.getFavorites().remove(existingFavorite.get());
                } else if (!cleanSlug.isBlank()) {
                    user.addFavorite(new FavoriteCountryEntity(cleanSlug, "", "dream"));
                }
                return toAuthUser(userRepository.save(user));
            });
        }

        AuthUser user = findUser(normalizedEmail).orElse(null);
        if (user == null) {
            return Optional.empty();
        }

        Set<String> favorites = new LinkedHashSet<>(user.favoriteCountries());
        if (!favorites.add(countrySlug)) {
            favorites.remove(countrySlug);
        }

        AuthUser updatedUser = new AuthUser(user.fullName(), user.email(), user.passwordHash(), user.style(),
                favorites, user.recommendedCountry(), user.favoriteNotes(), user.favoriteCollections());
        return saveUpdatedUser(normalizedEmail, user, updatedUser);
    }

    public synchronized Optional<AuthUser> saveRecommendation(String email, String countrySlug) {
        String normalizedEmail = normalizeEmail(email);
        if (databaseBacked()) {
            return userRepository.findById(normalizedEmail).map(user -> {
                user.setRecommendedCountry(clean(countrySlug));
                return toAuthUser(userRepository.save(user));
            });
        }

        AuthUser user = findUser(normalizedEmail).orElse(null);
        if (user == null) {
            return Optional.empty();
        }

        AuthUser updatedUser = new AuthUser(user.fullName(), user.email(), user.passwordHash(), user.style(),
                user.favoriteCountries(), clean(countrySlug), user.favoriteNotes(), user.favoriteCollections());
        return saveUpdatedUser(normalizedEmail, user, updatedUser);
    }

    public synchronized Optional<AuthUser> updateProfile(String email, String fullName, String style) {
        String normalizedEmail = normalizeEmail(email);
        String cleanFullName = clean(fullName);
        String cleanStyle = clean(style);
        if (databaseBacked()) {
            return userRepository.findById(normalizedEmail)
                    .filter(user -> cleanFullName.length() >= 2 && !cleanStyle.isBlank())
                    .map(user -> {
                        user.setFullName(cleanFullName);
                        user.setStyle(cleanStyle);
                        return toAuthUser(userRepository.save(user));
                    });
        }

        AuthUser user = findUser(normalizedEmail).orElse(null);
        if (user == null || cleanFullName.length() < 2 || cleanStyle.isBlank()) {
            return Optional.empty();
        }

        AuthUser updatedUser = new AuthUser(cleanFullName, user.email(), user.passwordHash(), cleanStyle,
                user.favoriteCountries(), user.recommendedCountry(), user.favoriteNotes(), user.favoriteCollections());
        return saveUpdatedUser(normalizedEmail, user, updatedUser);
    }

    public synchronized Optional<AuthUser> updateFavoriteMeta(String email, String countrySlug, String note, String collection) {
        String normalizedEmail = normalizeEmail(email);
        String cleanSlug = clean(countrySlug);
        if (databaseBacked()) {
            return userRepository.findById(normalizedEmail).flatMap(user -> favoriteBySlug(user, cleanSlug).map(favorite -> {
                favorite.setNote(clean(note));
                favorite.setCollection(clean(collection).isBlank() ? "dream" : clean(collection));
                return toAuthUser(userRepository.save(user));
            }));
        }

        AuthUser user = findUser(normalizedEmail).orElse(null);
        if (user == null || cleanSlug.isBlank() || !user.favoriteCountries().contains(cleanSlug)) {
            return Optional.empty();
        }

        Map<String, String> notes = new LinkedHashMap<>(user.favoriteNotes());
        Map<String, String> collections = new LinkedHashMap<>(user.favoriteCollections());
        String cleanNote = clean(note);
        String cleanCollection = clean(collection).isBlank() ? "dream" : clean(collection);

        if (cleanNote.isBlank()) {
            notes.remove(cleanSlug);
        } else {
            notes.put(cleanSlug, cleanNote);
        }
        collections.put(cleanSlug, cleanCollection);

        AuthUser updatedUser = new AuthUser(user.fullName(), user.email(), user.passwordHash(), user.style(),
                user.favoriteCountries(), user.recommendedCountry(), notes, collections);
        return saveUpdatedUser(normalizedEmail, user, updatedUser);
    }

    public synchronized RegisterResult resetPassword(String email, String password, String confirmPassword) {
        String normalizedEmail = normalizeEmail(email);
        if (databaseBacked()) {
            Optional<UserEntity> user = userRepository.findById(normalizedEmail);
            if (user.isEmpty()) {
                return RegisterResult.invalid("Bu e-posta ile kayıtlı hesap bulunamadı.");
            }

            if (password == null || password.length() < 6) {
                return RegisterResult.invalid("Yeni şifre en az 6 karakter olmalı.");
            }

            if (!password.equals(confirmPassword)) {
                return RegisterResult.invalid("Şifreler eşleşmiyor.");
            }

            UserEntity updatedUser = user.get();
            updatedUser.setPasswordHash(hashPassword(password));
            return RegisterResult.success(toAuthUser(userRepository.save(updatedUser)));
        }

        AuthUser user = findUser(normalizedEmail).orElse(null);
        if (user == null) {
            return RegisterResult.invalid("Bu e-posta ile kayıtlı hesap bulunamadı.");
        }

        if (password == null || password.length() < 6) {
            return RegisterResult.invalid("Yeni şifre en az 6 karakter olmalı.");
        }

        if (!password.equals(confirmPassword)) {
            return RegisterResult.invalid("Şifreler eşleşmiyor.");
        }

        AuthUser updatedUser = new AuthUser(user.fullName(), user.email(), hashPassword(password), user.style(),
                user.favoriteCountries(), user.recommendedCountry(), user.favoriteNotes(), user.favoriteCollections());
        Optional<AuthUser> savedUser = saveUpdatedUser(normalizedEmail, user, updatedUser);
        if (savedUser.isEmpty()) {
            return RegisterResult.invalid("Şifre güncellenemedi. Lütfen tekrar dene.");
        }
        return RegisterResult.success(savedUser.get());
    }

    private Optional<AuthUser> saveUpdatedUser(String email, AuthUser previousUser, AuthUser updatedUser) {
        if (databaseBacked()) {
            try {
                userRepository.save(toEntity(updatedUser));
                return Optional.of(updatedUser);
            } catch (RuntimeException exception) {
                return Optional.empty();
            }
        }

        users.put(email, updatedUser);
        try {
            saveUsers();
            return Optional.of(updatedUser);
        } catch (IOException exception) {
            users.put(email, previousUser);
            return Optional.empty();
        }
    }

    private boolean databaseBacked() {
        return userRepository != null;
    }

    private Optional<AuthUser> findUser(String email) {
        if (databaseBacked()) {
            return userRepository.findById(email).map(UserService::toAuthUser);
        }
        return Optional.ofNullable(users.get(email));
    }

    private void migrateFileUsersToDatabase() {
        if (!databaseBacked() || users.isEmpty()) {
            return;
        }
        users.values().forEach(user -> {
            if (!userRepository.existsById(user.email())) {
                userRepository.save(toEntity(user));
            }
        });
    }

    private static AuthUser toAuthUser(UserEntity entity) {
        Set<String> favorites = new LinkedHashSet<>();
        Map<String, String> notes = new LinkedHashMap<>();
        Map<String, String> collections = new LinkedHashMap<>();
        entity.getFavorites().forEach(favorite -> {
            favorites.add(favorite.getCountrySlug());
            if (favorite.getNote() != null && !favorite.getNote().isBlank()) {
                notes.put(favorite.getCountrySlug(), favorite.getNote());
            }
            collections.put(favorite.getCountrySlug(),
                    favorite.getCollection() == null || favorite.getCollection().isBlank()
                            ? "dream"
                            : favorite.getCollection());
        });
        return new AuthUser(entity.getFullName(), entity.getEmail(), entity.getPasswordHash(), entity.getStyle(),
                favorites, entity.getRecommendedCountry(), notes, collections);
    }

    private static UserEntity toEntity(AuthUser user) {
        UserEntity entity = new UserEntity(user.email(), user.fullName(), user.passwordHash(), user.style(), user.recommendedCountry());
        Set<FavoriteCountryEntity> favorites = new LinkedHashSet<>();
        user.favoriteCountries().forEach(slug -> favorites.add(new FavoriteCountryEntity(
                slug,
                user.favoriteNotes().getOrDefault(slug, ""),
                user.favoriteCollections().getOrDefault(slug, "dream")
        )));
        entity.replaceFavorites(favorites);
        return entity;
    }

    private static Optional<FavoriteCountryEntity> favoriteBySlug(UserEntity user, String countrySlug) {
        return user.getFavorites().stream()
                .filter(favorite -> favorite.getCountrySlug().equals(countrySlug))
                .findFirst();
    }

    private static String normalizeEmail(String email) {
        return clean(email).toLowerCase(Locale.ROOT);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private void loadUsers() {
        if (!Files.exists(usersFile)) {
            return;
        }

        Properties storedUsers = new Properties();
        try (InputStream inputStream = Files.newInputStream(usersFile)) {
            storedUsers.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Kullanıcı verisi okunamadı.", exception);
        }

        storedUsers.forEach((email, storedValue) -> {
            String[] values = storedValue.toString().split(":", -1);
            if (values.length < 3) {
                return;
            }

            try {
                Set<String> favorites = new LinkedHashSet<>();
                if (values.length > 3 && !values[3].isBlank()) {
                    String decodedFavorites = decode(values[3]);
                    for (String favorite : decodedFavorites.split(",")) {
                        if (!favorite.isBlank()) {
                            favorites.add(favorite);
                        }
                    }
                }
                AuthUser user = new AuthUser(
                        decode(values[0]),
                        email.toString(),
                        decode(values[1]),
                        decode(values[2]),
                        favorites,
                        values.length > 4 ? decode(values[4]) : "",
                        values.length > 5 ? deserializeMap(decode(values[5])) : Map.of(),
                        values.length > 6 ? deserializeMap(decode(values[6])) : Map.of()
                );
                users.put(user.email(), user);
            } catch (IllegalArgumentException ignored) {
                // Skip malformed entries so one damaged account does not prevent startup.
            }
        });
    }

    private synchronized void saveUsers() throws IOException {
        Path parentDirectory = usersFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        Properties storedUsers = new Properties();
        users.values().forEach(user -> storedUsers.setProperty(
                user.email(),
                encode(user.fullName()) + ":" + encode(user.passwordHash()) + ":" + encode(user.style()) + ":"
                        + encode(String.join(",", user.favoriteCountries())) + ":" + encode(user.recommendedCountry())
                        + ":" + encode(serializeMap(user.favoriteNotes())) + ":" + encode(serializeMap(user.favoriteCollections()))
        ));

        Path temporaryFile = Files.createTempFile(parentDirectory, "users-", ".tmp");
        try (OutputStream outputStream = Files.newOutputStream(temporaryFile)) {
            storedUsers.store(outputStream, "UrbanAura registered users");
        }

        try {
            Files.move(temporaryFile, usersFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, usersFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        byte[] hash = derivePassword(password, salt);
        return HASH_ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    private static boolean verifyPassword(String password, String storedHash) {
        String[] fields = storedHash.split("\\$", -1);
        if (fields.length != 3) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(fields[0]);
            byte[] salt = Base64.getDecoder().decode(fields[1]);
            byte[] expectedHash = Base64.getDecoder().decode(fields[2]);
            byte[] actualHash = derivePassword(password, salt, iterations, expectedHash.length * 8);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static byte[] derivePassword(String password, byte[] salt) {
        return derivePassword(password, salt, HASH_ITERATIONS, HASH_KEY_LENGTH);
    }

    private static byte[] derivePassword(String password, byte[] salt, int iterations, int keyLength) {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec).getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Şifre doğrulama hazırlanamadı.", exception);
        } finally {
            keySpec.clearPassword();
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static String serializeMap(Map<String, String> values) {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + Base64.getUrlEncoder().withoutPadding()
                        .encodeToString(entry.getValue().getBytes(StandardCharsets.UTF_8)))
                .reduce((first, second) -> first + "," + second)
                .orElse("");
    }

    private static Map<String, String> deserializeMap(String value) {
        Map<String, String> values = new LinkedHashMap<>();
        if (value == null || value.isBlank()) {
            return values;
        }
        for (String entry : value.split(",")) {
            String[] parts = entry.split("=", 2);
            if (parts.length == 2 && !parts[0].isBlank()) {
                values.put(parts[0], new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8));
            }
        }
        return values;
    }

    public record RegisterResult(boolean success, String message, AuthUser user) {
        static RegisterResult success(AuthUser user) {
            return new RegisterResult(true, "", user);
        }

        static RegisterResult invalid(String message) {
            return new RegisterResult(false, message, null);
        }
    }
}
