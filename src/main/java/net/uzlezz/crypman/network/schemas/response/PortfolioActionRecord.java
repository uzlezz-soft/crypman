package net.uzlezz.crypman.network.schemas.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PortfolioActionRecord implements Serializable {
    private String action;
    private String cryptocurrencyName;
    private BigDecimal balanceDelta;
    private BigDecimal cryptocurrencyDelta;
    private LocalDateTime dateTime;
}