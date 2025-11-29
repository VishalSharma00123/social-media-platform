package com.upost_service.post_service.service;

import com.upost_service.post_service.Event.PostEvent;
import com.upost_service.post_service.client.UserServiceClient;
import com.upost_service.post_service.controller.FileStorageService;
import com.upost_service.post_service.dto.*;
import com.upost_service.post_service.model.Post;
import com.upost_service.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FileStorageService fileStorageService;

    private static final String TOPIC_POST_EVENTS = "post-events";


    public PostResponse createPost(String userId, CreatePostRequest request, List<MultipartFile> images, MultipartFile video) {
        // Get user details from User Service
        UserDTO user = userServiceClient.getUserById(userId);


        // Store images and get URLs
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                String url = fileStorageService.saveImage(image); // Implement this method!
                imageUrls.add(url);
            }
        }

        String videoUrl = null;
        if (video != null && !video.isEmpty()) {
            videoUrl = fileStorageService.saveVideo(video); // Implement this method!
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setUsername(user.getUsername());
        post.setUserProfilePicture(user.getProfilePicture());
        post.setContent(request.getContent());
        post.setImages(imageUrls != null ? imageUrls : List.of());
        post.setVideoUrl(videoUrl);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return mapToPostResponse(savedPost, userId);
    }

    public PostResponse getPost(String postId, String currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.isDeleted()) {
            throw new RuntimeException("Post has been deleted");
        }

        return mapToPostResponse(post, currentUserId);
    }

    public Page<PostResponse> getUserPosts(String userId, String currentUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable);
        return posts.map(post -> mapToPostResponse(post, currentUserId));
    }

    public Page<PostResponse> getFeed(String userId, Pageable pageable) {
        // Get user's following list from User Service
        List<String> following = userServiceClient.getUserFollowing(userId);
        following.add(userId); // Include user's own posts

        Page<Post> posts = postRepository.findByUserIdInAndIsDeletedFalseOrderByCreatedAtDesc(following, pageable);
        return posts.map(post -> mapToPostResponse(post, userId));
    }

    public PostResponse likePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.getLikes().add(userId);
        Post savedPost = postRepository.save(post);

        UserDTO user = userServiceClient.getUserById(userId);
        PostEvent event = new PostEvent();
        event.setType("POST_LIKED");
        event.setPostId(postId);
        event.setPostOwnerId(post.getUserId());
        event.setUserId(userId);
        event.setUsername(user.getUsername());

        kafkaTemplate.send(TOPIC_POST_EVENTS, event);

        return mapToPostResponse(savedPost, userId);
    }

    public PostResponse unlikePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.getLikes().remove(userId);
        Post savedPost = postRepository.save(post);

        return mapToPostResponse(savedPost, userId);
    }

    public PostResponse addComment(String postId, String userId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        UserDTO user = userServiceClient.getUserById(userId);

        Post.Comment comment = new Post.Comment();
        comment.setUserId(userId);
        comment.setUsername(user.getUsername());
        comment.setUserProfilePicture(user.getProfilePicture());
        comment.setContent(request.getContent());

        post.getComments().add(comment);
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        PostEvent event = new PostEvent();
        event.setType("COMMENT");
        event.setPostId(postId);
        event.setPostOwnerId(post.getUserId());
        event.setUserId(userId);
        event.setUsername(user.getUsername());
        event.setComment(request.getContent());

        kafkaTemplate.send(TOPIC_POST_EVENTS, event);

        return mapToPostResponse(savedPost, userId);
    }

    public void deletePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own posts");
        }

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

        private PostResponse mapToPostResponse(Post post, String currentUserId) {
            List<CommentResponse> recentComments = post.getComments().stream()
                    .skip(Math.max(0, post.getComments().size() - 3))
                    .map(comment -> CommentResponse.builder()
                            .id(comment.getId())
                            .userId(comment.getUserId())
                            .username(comment.getUsername())
                            .userProfilePicture(comment.getUserProfilePicture())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .username(post.getUsername())
                .userProfilePicture(post.getUserProfilePicture())
                .content(post.getContent())
                .images(post.getImages())
                .video(post.getVideoUrl())
                .likesCount(post.getLikes().size())
                .commentsCount(post.getComments().size())
                .sharesCount(post.getSharesCount())
                .isLiked(post.getLikes().contains(currentUserId))
                .recentComments(recentComments)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
