package net.uzlezz.crypman;

import lombok.Getter;
import lombok.Setter;
import net.uzlezz.crypman.network.schemas.response.Cryptocurrency;

import java.util.List;

public class MarketState {
    private static MarketState instance;
    private List<Cryptocurrency> cryptocurrencies;

    private MarketState() {}

    private static MarketState getInstance() {
        if (instance == null) {
            instance = new MarketState();
        }
        return instance;
    }

    public static Cryptocurrency getCryptocurrency(String name) {
        for (var crypto : getInstance().cryptocurrencies) {
            if (crypto.getName().equals(name)) return crypto;
        }
        return null;
    }

    public static void setCryptocurrencyList(List<Cryptocurrency> cryptocurrencies) {
        getInstance().cryptocurrencies = cryptocurrencies;
    }
}
