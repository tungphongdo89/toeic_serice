package com.migi.toeic.authen.model;

import lombok.Data;

@Data
public class RequestContact {
    private String email;
    private String context;
    private String name;
}
