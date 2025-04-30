package net.uzlezz.crypman.network.schemas.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PriceRecord implements Serializable {
    private LocalDateTime time;
    private BigDecimal value;
}

