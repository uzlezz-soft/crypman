package net.uzlezz.crypman.network.schemas.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Asset implements Serializable {
    private String name;
    private BigDecimal amount;
}
