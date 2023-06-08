package com.baron.SocialMediaAPI.util;

import com.baron.SocialMediaAPI.dto.UserDto;
import com.baron.SocialMediaAPI.model.User;

public class UserUtil {


    public static UserDto mapUserToUserDto(User user){
        return UserDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }


}
