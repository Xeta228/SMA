package com.baron.SocialMediaAPI.dto;


import com.baron.SocialMediaAPI.model.User;
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
public class MessageDto {
    private Long id;
    private String sender;

    @NotNull(message = "receiver cannot be null")
    @NotBlank(message = "receiver cannot be blank")
    @NotEmpty(message = "receiver cannot be empty")
    private String receiver;
    private String text;

    public MessageDto(@NotNull(message = "receiver cannot be null") String receiver, String text) {
        this.receiver = receiver;
        this.text = text;
    }
}
