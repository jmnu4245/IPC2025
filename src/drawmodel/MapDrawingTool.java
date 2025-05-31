package drawmodel;

import controller.SelectionMenuManager;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;

public interface MapDrawingTool {
    // Called when the tool is selected from the toolbar
    void activate();
    // Called when the tool is deselected or another tool is chosen
    void deactivate();

    // Event handlers for drawing actions
    void onMouseClick(MouseEvent event, Point2D mapCoords);
    void onMousePressed(MouseEvent event, Point2D mapCoords); // For potential drag-to-draw tools
    void onMouseDragged(MouseEvent event, Point2D mapCoords);
    void onMouseReleased(MouseEvent event, Point2D mapCoords);

    // Method to pass references needed by tools
    void setDependencies(Group mapZoomGroup,SelectionMenuManager menuManager,ScrollPane mapScrollPane);
}