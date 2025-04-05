package net.uzlezz.crypman.gui.utils;

import net.uzlezz.crypman.network.Response;
import net.uzlezz.crypman.network.ResponseStatus;

public class ResponseFailureUtils {
    public static void handle(Response response) {
        if (response.getStatus() == ResponseStatus.OK) return;

        String header;
        switch (response.getStatus()) {
            case ERROR -> header = "Ошибка";
            case BAD_PAYLOAD -> header = "Ошибка данных";
            case UNKNOWN_PATH -> header = "Ошибка пути запроса";
            case INTERNAL_ERROR -> header = "Ошибка на сервере";
            default -> header = "[неизвестно]";
        }

        AlertUtils.showError(header, response.getErrorMessage());
        throw new RuntimeException();
    }
}
