package net.uzlezz.crypman.network.schemas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class Optimization implements Serializable {
    private boolean optimizeForTarget;
    private double targetReturn;
    private int daysPeriod;
    private List<String> cryptocurrencies;
}
