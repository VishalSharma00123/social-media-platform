package com.upost_service.post_service.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent {
    private String type;         // e.g., "POST_LIKED", "POST_COMMENTED"
    private String postId;
    private String postOwnerId;  // owner of the post
    private String userId;       // user who liked/commented
    private String username;     // username of the user who liked/commented
    private String comment;      // present only if COMMENT
}

