package net.uzlezz.crypman.gui.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DatetimePricePair {
    private LocalDateTime dateTime;
    private BigDecimal price;
}
