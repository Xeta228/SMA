package com.baron.SocialMediaAPI.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageSendRequest {

    @NotNull(message = "receiver cannot be null")
    @NotBlank(message = "receiver cannot be blank")
    @NotEmpty(message = "receiver cannot be empty")
    private String receiver;
    private String text;

}
