package com.baron.SocialMediaAPI.util;

import com.baron.SocialMediaAPI.dto.PostDto;
import com.baron.SocialMediaAPI.model.Post;



public class PostUtil {


    public static PostDto mapPostToPostDto(Post post){
        return PostDto.builder().label(post.getLabel()).text(post.getText()).image(post.getImage())
                        .username(post.getUser().getUsername()).id(post.getId()).date(post.getDate())
                        .
                build();
    }

}
