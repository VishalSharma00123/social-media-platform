package com.upost_service.post_service.repository;

import com.upost_service.post_service.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId, Pageable pageable);

    @Query("{ 'userId': { $in: ?0 }, 'isDeleted': false }")
    Page<Post> findByUserIdInAndIsDeletedFalseOrderByCreatedAtDesc(List<String> userIds, Pageable pageable);

    Page<Post> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    long countByUserIdAndIsDeletedFalse(String userId);
}
