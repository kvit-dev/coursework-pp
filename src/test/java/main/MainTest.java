package main;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        new Main().start(stage);
    }

    @Test
    void testMainWindowLoadsSuccessfully() {
        assertNotNull(stage);
        assertTrue(stage.isShowing());

        assertEquals("Управління банківськими вкладами", stage.getTitle());

        assertTrue(stage.isMaximized());

        Scene scene = stage.getScene();
        assertNotNull(scene);
        assertFalse(scene.getStylesheets().isEmpty(), "Список стилів має бути непорожнім");
    }
}
