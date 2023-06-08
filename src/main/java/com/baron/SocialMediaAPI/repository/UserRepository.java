package com.baron.SocialMediaAPI.repository;
import com.baron.SocialMediaAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);

    boolean existsByUsername(String username);


    boolean existsByEmail(String email);


}
