package net.uzlezz.crypman.network.schemas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CryptocurrencyStats implements Serializable {
    public String name;
    public int periodDays;
}
