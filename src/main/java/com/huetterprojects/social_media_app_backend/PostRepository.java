package com.huetterprojects.social_media_app_backend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface PostRepository extends MongoRepository<Post, String> {

    @Query("{ '$text': { '$search': ?0 } }")
    Page<Post> searchByText(String searchTerm, Pageable pageable);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'likes': 1 } }")
    void incrementLikes(String postId);
}