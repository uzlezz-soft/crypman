package net.uzlezz.crypman.network.schemas.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Log implements Serializable {
    private String userName;
    private LocalDateTime dateTime;
    private String info;
}
