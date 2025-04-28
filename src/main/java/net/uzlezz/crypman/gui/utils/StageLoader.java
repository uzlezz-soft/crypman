package net.uzlezz.crypman.gui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.uzlezz.crypman.gui.enums.StagePath;

import java.io.IOException;

public final class StageLoader {
    public static void loadUnchecked(Stage stage, StagePath path) throws IOException {
        var loader = new FXMLLoader(StageLoader.class.getResource(path.getPath()));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
    }

    public static void load(Stage stage, StagePath path) {
        try {
            loadUnchecked(stage, path);
        } catch (IOException exception) {
            exception.printStackTrace();
            AlertUtils.showError("Navigation Error", String.format("Unable to navigate to %s", path.getPath()));
        }
    }

    public static void load(Window window, StagePath path) {
        load((Stage)window, path);
    }

    public static void load(Scene scene, StagePath path) {
        load(scene.getWindow(), path);
    }
}
