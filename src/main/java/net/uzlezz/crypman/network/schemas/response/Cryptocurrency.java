package net.uzlezz.crypman.network.schemas.response;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Cryptocurrency implements Serializable {
    public String name;
    public String friendlyName;
    public BigDecimal price;
    public String icon;
}