package com.baron.SocialMediaAPI.service;


import com.baron.SocialMediaAPI.dto.PostDto;
import com.baron.SocialMediaAPI.model.Post;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.repository.PostRepository;
import com.baron.SocialMediaAPI.util.PostUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final String IMAGES_UPLOAD_PATH = "/home/vadim/SMA_images";
    private static final List<String> SUPPORTED_FILE_EXTENSIONS = List.of(".jpeg", ".jpg", ".png");

    public List<PostDto> getAllPostsForUser(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByUserUsername(username, pageable)
                .stream()
                .map(PostUtil::mapPostToPostDto)
                .toList();
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public void createNewPost(MultipartFile file, String label, String text) {
        User currentUser = userService.getCurrentUser();
        String userId = String.valueOf(currentUser.getId());

        if (file != null) {
            validateFileExtension(file.getOriginalFilename());

            String randomUUID = UUID.randomUUID().toString();
            String fileExtension = getFileExtension(file.getOriginalFilename());

            String userFolderPath = Paths.get(IMAGES_UPLOAD_PATH, userId).toString();
            try {
                Files.createDirectories(Paths.get(userFolderPath));
                String newFilename = randomUUID + fileExtension;
                Path filePath = Paths.get(userFolderPath, newFilename);
                file.transferTo(filePath.toFile());

                Post post = createPost(label, text, filePath.toString(), currentUser);
                postRepository.save(post);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Post post = createPost(label, text, null, currentUser);
            postRepository.save(post);
        }
    }

    public void updatePost(Post post, MultipartFile file, String label, String text) {
        User currentUser = post.getUser();
        String userId = String.valueOf(currentUser.getId());

        if (file != null && !file.isEmpty()) {
            validateFileExtension(file.getOriginalFilename());

            String randomUUID = UUID.randomUUID().toString();
            String fileExtension = getFileExtension(file.getOriginalFilename());

            String userFolderPath = Paths.get(IMAGES_UPLOAD_PATH, userId).toString();

            try {
                Files.createDirectories(Paths.get(userFolderPath));
                String newFilename = randomUUID + fileExtension;
                Path filePath = Paths.get(userFolderPath, newFilename);
                file.transferTo(filePath.toFile());

                post.setImage(filePath.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            post.setImage(null);
        }

        post.setLabel(label);
        post.setText(text);
        postRepository.save(post);
    }

    public List<PostDto> retrieveActivity(User currentUser, int page, int size, String sort) {
        List<User> receivedPostsUsers = currentUser.getFollowing();
        List<Post> posts = receivedPostsUsers.stream()
                .flatMap(user -> user.getPosts().stream())
                .collect(Collectors.toList());

        if (sort.equalsIgnoreCase("asc")) {
            posts.sort(Comparator.comparing(Post::getDate));
        } else if (sort.equalsIgnoreCase("desc")) {
            posts.sort(Comparator.comparing(Post::getDate).reversed());
        }

        int totalPosts = posts.size();
        int totalPages = (int) Math.ceil((double) totalPosts / size);

        if (page < 0 || page >= totalPages) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalPosts);

        return posts.subList(start, end)
                .stream()
                .map(PostUtil::mapPostToPostDto)
                .toList();
    }

    private void validateFileExtension(String filename) {
        String fileExtension = getFileExtension(filename);
        if (!SUPPORTED_FILE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file format. Only .jpeg, .jpg, and .png formats are allowed.");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("Invalid file name: " + filename);
        }
        return filename.substring(lastDotIndex);
    }

    private Post createPost(String label, String text, String imagePath, User user) {
        return Post.builder()
                .label(label)
                .text(text)
                .image(imagePath)
                .date(new Date())
                .user(user)
                .build();
    }

    public void savePost(Post post){
        postRepository.save(post);
    }
}
