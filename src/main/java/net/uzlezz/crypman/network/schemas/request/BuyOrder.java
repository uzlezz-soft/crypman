package net.uzlezz.crypman.network.schemas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BuyOrder implements Serializable {
    private String cryptocurrency;
    private BigDecimal amount;
}
