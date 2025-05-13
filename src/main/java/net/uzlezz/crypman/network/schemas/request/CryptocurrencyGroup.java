package net.uzlezz.crypman.network.schemas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
public class CryptocurrencyGroup implements Serializable {
    private int daysPeriod;
    private Map<String, Double> weights;
}
