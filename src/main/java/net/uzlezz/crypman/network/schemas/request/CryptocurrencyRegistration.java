package net.uzlezz.crypman.network.schemas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CryptocurrencyRegistration implements Serializable {
    private String name;
    private String friendlyName;
    private String icon;
}