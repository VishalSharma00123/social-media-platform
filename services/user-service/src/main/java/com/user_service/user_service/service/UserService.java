package com.user_service.user_service.service;

import com.user_service.user_service.client.NotificationClient;
import com.user_service.user_service.dto.*;
import com.user_service.user_service.event.UserEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.user_service.user_service.Model.User;
import com.user_service.user_service.Respository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FileStorageService fileStorageService;
    private final NotificationClient notificationClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String EVENT_TOPIC = "user-events";

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       FileStorageService fileStorageService, NotificationClient notificationClient, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.fileStorageService = fileStorageService;
        this.notificationClient = notificationClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    // ✅ ADD THESE NEW METHODS FOR MESSAGE SERVICE

    /**
     * Get user by ID - Used by Message Service via Feign Client
     */
        public UserDTO getUserById(String userId) {
        System.out.println("→ Fetching user by ID: " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        System.out.println("✓ User found: " + user.getUsername());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePicture(user.getProfilePicture())
                .bio(user.getBio())
                .followersCount(user.getFollowers() != null ? user.getFollowers().size() : 0)
                .followingCount(user.getFollowing() != null ? user.getFollowing().size() : 0)
                .build();
    }

    /**
     * Get multiple users by IDs - Used for batch operations
     */
    public List<UserDTO> getUsersByIds(List<String> userIds) {
        System.out.println("→ Fetching " + userIds.size() + " users by IDs");

        List<User> users = userRepository.findAllById(userIds);

        System.out.println("✓ Found " + users.size() + " users");

        return users.stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .profilePicture(user.getProfilePicture())
                        .bio(user.getBio())
                        .followersCount(user.getFollowers() != null ? user.getFollowers().size() : 0)
                        .followingCount(user.getFollowing() != null ? user.getFollowing().size() : 0)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Check if user exists by ID
     */
    public boolean userExists(String userId) {
        boolean exists = userRepository.existsById(userId);
        System.out.println("→ User " + userId + " exists: " + exists);
        return exists;
    }

    // ✅ EXISTING METHODS BELOW (unchanged)

    public AuthResponse register(RegisterRequest request) {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║   REGISTRATION SERVICE STARTED     ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("Username: " + request.getUsername());
        System.out.println("Email: " + request.getEmail());

        try {
            System.out.println("→ Checking for existing username...");
            Optional<User> existingUsername = userRepository.findByUsername(request.getUsername());
            if (existingUsername.isPresent()) {
                System.out.println("❌ Username already exists!");
                throw new RuntimeException("Username already exists");
            }
            System.out.println("✓ Username available");

            System.out.println("→ Checking for existing email...");
            Optional<User> existingEmail = userRepository.findByEmail(request.getEmail());
            if (existingEmail.isPresent()) {
                System.out.println("❌ Email already exists!");
                throw new RuntimeException("Email already exists");
            }
            System.out.println("✓ Email available");

            System.out.println("→ Creating new user...");
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            System.out.println("→ Saving to MongoDB...");
            User savedUser = userRepository.save(user);
            System.out.println("✓ User saved with ID: " + savedUser.getId());

            System.out.println("→ Generating JWT token...");
            String token = jwtService.generateToken(savedUser.getId(), savedUser.getUsername());
            System.out.println("✓ Token generated");

            System.out.println("╔════════════════════════════════════╗");
            System.out.println("║   REGISTRATION COMPLETED ✓         ║");
            System.out.println("╚════════════════════════════════════╝\n");


            UserEvent user1 = new UserEvent();

            user1.setType("REGISTRATION");
            user1.setUserId(user.getId());
            user1.setUsername(user.getUsername());

            kafkaTemplate.send(EVENT_TOPIC, user1);


            return new AuthResponse.Builder()
                    .token(token)
                    .userId(savedUser.getId())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .profilePicture(savedUser.getProfilePicture())
                    .build();

        } catch (Exception e) {
            System.err.println("╔════════════════════════════════════╗");
            System.err.println("║   REGISTRATION FAILED ✗            ║");
            System.err.println("╚════════════════════════════════════╝");
            System.err.println("Error: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    public AuthResponse login(LoginRequest request) {
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getUsername());

        // Inside login() method, after user is authenticated:
       UserEvent user1 = new UserEvent();
       user1.setType("LOGIN");
       user1.setUserId(user.getId());
       user1.setUsername(user.getUsername());

       kafkaTemplate.send(EVENT_TOPIC, user1);



        return new AuthResponse.Builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));



        return new UserProfileResponse.Builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .bio(user.getBio())
                .profilePicture(user.getProfilePicture())
                .coverPicture(user.getCoverPicture())
                .followersCount(user.getFollowers().size())
                .followingCount(user.getFollowing().size())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public void followUser(String currentUserId, String targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new RuntimeException("Cannot follow yourself");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        currentUser.getFollowing().add(targetUserId);
        targetUser.getFollowers().add(currentUserId);

        userRepository.save(currentUser);
        userRepository.save(targetUser);

        UserEvent event = new UserEvent();
        event.setType("FOLLOW");
        event.setUserId(currentUserId);
        event.setUsername(currentUser.getUsername());
        event.setTargetUserId(targetUserId);

        kafkaTemplate.send(EVENT_TOPIC, event);

    }

    public void unfollowUser(String currentUserId, String targetUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        currentUser.getFollowing().remove(targetUserId);
        targetUser.getFollowers().remove(currentUserId);

        userRepository.save(currentUser);
        userRepository.save(targetUser);

    }

    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getBio() != null) user.setBio(request.getBio());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);


        return getUserProfile(savedUser.getUsername());
    }

    public String uploadProfilePicture(String userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = fileStorageService.uploadFile(file, "profile-pictures");
        user.setProfilePicture(imageUrl);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        return imageUrl;
    }

    public List<String> getUserFollowing(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        return List.copyOf(userOpt.get().getFollowing());
    }
}