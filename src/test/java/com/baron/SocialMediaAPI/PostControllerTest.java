package com.baron.SocialMediaAPI;

import com.baron.SocialMediaAPI.controller.PostController;
import com.baron.SocialMediaAPI.dto.PostDto;
import com.baron.SocialMediaAPI.exception.ErrorResponse;
import com.baron.SocialMediaAPI.model.Post;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.service.PostService;
import com.baron.SocialMediaAPI.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Mock
    private PostService postService;
    @Mock
    private UserService userService;



    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        postController = new PostController(postService, userService);
    }

    @Test
    void getPostsForCertainUser_shouldReturnListOfPostDtos() {

        String username = "testuser";
        int page = 0;
        int size = 10;
        List<PostDto> expectedPosts = new ArrayList<>();
        when(postService.getAllPostsForUser(username, page, size)).thenReturn(expectedPosts);


        List<PostDto> result = postController.getPostsForCertainUser(username, page, size);


        assertEquals(expectedPosts, result);
    }

    @Test
    void createNewPost_withImage_shouldCallPostServiceCreateNewPost() {

        MultipartFile image =
                new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);
        String label = "Test Label";
        String text = "Test Text";


        postController.createNewPost(image, label, text);


        verify(postService, times(1)).createNewPost(image, label, text);
    }

    @Test
    void updatePost_withValidPostId_shouldReturnOkResponse() throws JsonProcessingException {

        Long postId = 1L;
        MultipartFile image =
                new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);
        String label = "Updated Label";
        String text = "Updated Text";
        User currentUser = new User();
        currentUser.setUsername("testuser");


        Post post = Post.builder()
                .image(image.getName())
                .id(postId)
                .label(label)
                .text(text)
                .user(currentUser)
                .build();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(postService.getPostById(postId)).thenReturn(post);


        ResponseEntity<String> response = postController.updatePost(postId, image, label, text);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Post updated successfully.", response.getBody());
    }

    @Test
    void updatePost_withInvalidPostId_shouldReturnNotFoundResponse() throws JsonProcessingException {

        Long postId = 1L;
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[0]);
        String label = "Updated Label";
        String text = "Updated Text";
        User currentUser = new User();
        currentUser.setUsername("testuser");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(postService.getPostById(postId)).thenThrow(NoSuchElementException.class);


        ResponseEntity<String> response = postController.updatePost(postId, image, label, text);


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        ErrorResponse errorResponse = new ErrorResponse("Post Not Found", "The requested post does not exist.");
        assertEquals(new ObjectMapper().writeValueAsString(errorResponse), response.getBody());
    }

    @Test
    void deletePost_withValidPostId_shouldReturnOkResponse() throws JsonProcessingException {
        Long postId = 1L;
        User currentUser = new User();
        currentUser.setUsername("testuser");

        Post post = new Post();
        post.setUser(currentUser);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(postService.getPostById(postId)).thenReturn(post);

        doNothing().when(postService).delete(any(Post.class));


        ResponseEntity<String> response = postController.deletePost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Post deleted successfully.", response.getBody());
    }

    @Test
    void deletePost_withInvalidPostId_shouldReturnNotFoundResponse() throws JsonProcessingException {

        Long postId = 1L;
        User currentUser = new User();
        currentUser.setUsername("testuser");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(postService.getPostById(postId)).thenReturn(null);

        ResponseEntity<String> response = postController.deletePost(postId);


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        ErrorResponse errorResponse = new ErrorResponse("Post Not Found", "The requested post does not exist.");
        assertEquals(new ObjectMapper().writeValueAsString(errorResponse), response.getBody());
    }

    @Test
    void retrieveActivity_shouldReturnListOfPostDtos() {

        int page = 0;
        int size = 10;
        String sort = "desc";
        User currentUser = new User();
        currentUser.setUsername("testuser");
        List<PostDto> expectedPosts = new ArrayList<>();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(postService.retrieveActivity(currentUser, page, size, sort)).thenReturn(expectedPosts);


        List<PostDto> result = postController.retrieveActivity(page, size, sort);


        assertEquals(expectedPosts, result);
    }
}