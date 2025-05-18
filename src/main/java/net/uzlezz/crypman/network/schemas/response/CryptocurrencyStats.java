package net.uzlezz.crypman.network.schemas.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CryptocurrencyStats implements Serializable {
    private double risk;
    private double returns;
}
