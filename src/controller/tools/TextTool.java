package controller.tools;

import drawmodel.MapDrawingTool;
import drawmodel.MapStateManager;
import controller.SelectionMenuManager;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Optional;
import javafx.scene.control.ScrollPane;

public class TextTool implements MapDrawingTool {
    private Group mapZoomGroup;
    private SelectionMenuManager menuManager;
    private Bounds contentBounds;
    private ScrollPane mapScrollPane;


    @Override
    public void setDependencies( Group mapZoomGroup, SelectionMenuManager menuManager,ScrollPane mapScrollPane) {
        this.mapZoomGroup = mapZoomGroup;
        this.menuManager = menuManager;
        this.mapScrollPane=mapScrollPane;
    }


    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
        // Nothing specific to clean up
    }

public void onMouseClick(MouseEvent event, Point2D mapCoords) {
        // Mostrar diálogo de entrada de texto
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Insertar Texto");
        dialog.setHeaderText("Introduce el texto a colocar en el mapa:");
        dialog.setContentText("Texto:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(textContent -> {
            if (!textContent.trim().isEmpty()) {
                // Crear el nodo de texto
                Text newText = new Text(mapCoords.getX(), mapCoords.getY(), textContent);
                newText.setFill(menuManager.getColorPickerValue());

                String fontFamily = menuManager.getFontFamily();
                double fontSize = menuManager.getFontSize();
                newText.setFont(javafx.scene.text.Font.font(fontFamily, fontSize));

                mapZoomGroup.getChildren().add(newText);

                       }
        });
    }
    @Override public void onMousePressed(MouseEvent event, Point2D mapCoords) {}
    @Override public void onMouseDragged(MouseEvent event, Point2D mapCoords) {}
    @Override public void onMouseReleased(MouseEvent event, Point2D mapCoords) {}
}