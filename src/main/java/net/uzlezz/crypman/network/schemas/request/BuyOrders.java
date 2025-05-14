package net.uzlezz.crypman.network.schemas.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class BuyOrders implements Serializable {
    private List<BuyOrder> list;
}
