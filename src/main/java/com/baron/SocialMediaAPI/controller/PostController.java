package com.baron.SocialMediaAPI.controller;

import com.baron.SocialMediaAPI.dto.PostDto;
import com.baron.SocialMediaAPI.exception.ErrorResponse;
import com.baron.SocialMediaAPI.model.Post;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.service.PostService;
import com.baron.SocialMediaAPI.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;


    @GetMapping("/user/{username}")
    @Operation(summary = "View all posts made by user with specified username. Pagination and sorting by date " +
            "(asc, desc) " +
            "is supported.")
    public List<PostDto> getPostsForCertainUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        return postService.getAllPostsForUser(
                username,
                page, size);
    }

    @PostMapping
    @Operation(summary = "Creates new post setting currently logged in user as the author. \"image\" should be" +
            " provided as a multipart file.")
    public void createNewPost(@RequestParam(value = "image", required = false) MultipartFile file,
                              @RequestParam(value = "label") String label,
                              @RequestParam(value = "text") String text){
        postService.createNewPost(file, label, text);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Update post with specified id.")
    public ResponseEntity<String> updatePost(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "image", required = false) MultipartFile file,
            @RequestParam(value = "label") String label,
            @RequestParam(value = "text") String text
    ) {
       User curr = userService.getCurrentUser();
        try {
            Post post = postService.getPostById(postId);

            if (!post.getUser().getUsername().equals(curr.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You don't have permission to update this post");
            }

            postService.updatePost(post, file, label, text);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("Post Not Found", "The requested post does not exist.");

            try {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(errorResponse));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        return ResponseEntity.ok("Post updated successfully.");
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post with specified id.")
    public ResponseEntity<String> deletePost(
            @PathVariable("postId") Long postId
    ) {
        User curr = userService.getCurrentUser();
        try {
            Post post = postService.getPostById(postId);

            if (post == null) {
                ErrorResponse errorResponse =
                        new ErrorResponse("Post Not Found", "The requested post does not exist.");

                // Set the response status to NOT_FOUND (404) and return the error response as JSON
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(errorResponse));
            }

            if (!post.getUser().getUsername().equals(curr.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You don't have permission to delete this post.");
            }

            // Delete the post
            postService.delete(post);

            return ResponseEntity.ok("Post deleted successfully.");
        } catch (NoSuchElementException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the post.");
        }
    }


    @GetMapping
    @Operation(summary = "View activity for the currently logged in user. " +
            "This will show all the posts from the accounts the user is following. " +
            "Pagination and sorting by date (choose asc or desc) is supported.")
    public List<PostDto> retrieveActivity(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "desc") String sort) {
        User currentUser = userService.getCurrentUser();
        return postService.retrieveActivity(currentUser, page, size, sort);
    }

}
