package com.MeetingWeb.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {
    private String pwEmail;
    private String pw;
    private String pwCheck;

    // Getters and Setters
}