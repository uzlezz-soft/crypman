package net.uzlezz.crypman.network.schemas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NewPriceRecord implements Serializable {
    private String cryptoName;
    private LocalDateTime time;
    private BigDecimal value;
}
