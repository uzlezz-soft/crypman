package net.uzlezz.crypman.network;

import lombok.Data;

import java.io.Serializable;

@Data
public final class Response implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;

    private ResponseStatus status;
    private Object payload;
    private String errorMessage;
}