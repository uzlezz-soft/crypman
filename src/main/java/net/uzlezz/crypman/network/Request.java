package net.uzlezz.crypman.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Request implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;

    private String path;
    private Object payload;
}