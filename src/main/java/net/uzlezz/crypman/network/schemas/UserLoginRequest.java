package net.uzlezz.crypman.network.schemas;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserLoginRequest implements Serializable {
    private String username;
    private String password;
}
