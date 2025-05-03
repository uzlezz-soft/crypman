package net.uzlezz.crypman.network.schemas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CryptocurrencyEdit implements Serializable {
    private String originalName;
    private String newName;
    private String newFriendlyName;
    private String newIcon;
}
