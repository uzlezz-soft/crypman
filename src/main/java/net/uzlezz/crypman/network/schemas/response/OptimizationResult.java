package net.uzlezz.crypman.network.schemas.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
public class OptimizationResult implements Serializable {
    private Map<String, Double> weights;
    private PortfolioStats stats;
}
