package org.sdamc.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {

    private String token;

    private int id;

    public LoginResponse(String token, int id) {
        this.token = token;
        this.id = id;
    }

}
