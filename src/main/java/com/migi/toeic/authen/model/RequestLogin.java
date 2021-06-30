package com.migi.toeic.authen.model;

import lombok.Data;

@Data
public class RequestLogin {
    private String email;
    private String username;
    private String password;
    private String passwordOld;
    private String passwordConfirm;
    private String role;
}
