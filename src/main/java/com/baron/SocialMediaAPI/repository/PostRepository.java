package com.baron.SocialMediaAPI.repository;

import com.baron.SocialMediaAPI.model.Post;
import com.baron.SocialMediaAPI.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    Page<Post> findAllByUserUsername(String username, Pageable pageable);

}
