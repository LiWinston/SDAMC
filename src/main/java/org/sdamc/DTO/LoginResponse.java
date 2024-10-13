package org.sdamc.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {

    private String token;

    private int id;

    private String name;

    public LoginResponse(String token, int id, String name) {
        this.token = token;
        this.id = id;
        this.name = name;
    }

}
