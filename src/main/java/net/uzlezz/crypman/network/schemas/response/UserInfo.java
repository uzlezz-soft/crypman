package net.uzlezz.crypman.network.schemas.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class UserInfo implements Serializable {
    private String userName;
    public List<String> roles;
    public BigDecimal balance;
}
