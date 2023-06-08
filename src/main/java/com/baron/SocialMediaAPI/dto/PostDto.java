package com.baron.SocialMediaAPI.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {

    private Long id;
    @NotNull
    @NotBlank(message = "label cannot be blank")
    @NotEmpty(message = "label cannot be empty")
    @Size(min = 2, max = 256, message = "label length should be between 2 and 256 characters ")
    private String label;
    @NotNull
    @NotBlank(message = "text of a post cannot be blank")
    @NotEmpty(message = "text of a post cannot be empty")
    private String text;

    private String image;

    private String username;

    private Date date;
}
