/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 *
 * @author Juan Manuel
 */
public class utils {
    private utils(){}
    
    public static double calculateAngle(Point2D center, Point2D p) {
        double angle = Math.toDegrees(Math.atan2(-center.getY() + p.getY(), center.getX() - p.getX()));
        return angle;
    }
    
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
    public static boolean isNodeAt(Node node, Point2D point) {
        Bounds bounds = node.getBoundsInLocal();
        if (node instanceof Line) {
            Line line = (Line) node;
            double lineLength = Math.sqrt(Math.pow(line.getEndX() - line.getStartX(), 2) +
                                         Math.pow(line.getEndY() - line.getStartY(), 2));
            double distance = Math.abs((line.getEndX() - line.getStartX()) * (line.getStartY() - point.getY()) -
                                      (line.getStartX() - point.getX()) * (line.getEndY() - line.getStartY())) / lineLength;
            return distance <= line.getStrokeWidth() + 5;
        } else if (node instanceof Text) {
            return bounds.contains(point);
        } else if (node instanceof Shape) {
            return bounds.contains(point);
        }

        return false;
    }
    
    

    public static boolean isMapBackground(Node element) {
        // Check if element is the map background image
        return element.getId() != null && element.getId().equals("mapImageView");
    }
}
