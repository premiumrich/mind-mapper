package net.premiumrich.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.premiumrich.ui.CanvasPanel;
import net.premiumrich.ui.ContextMenu;
import net.premiumrich.ui.Viewport;

public class ShapesController {

	private CanvasPanel canvasInstance;
	private Viewport viewport;
	
	private List<MapShape> shapes;
	private MapShape highlightedShape;
	private List<MapLine> lines;
	
	public ShapesController(CanvasPanel canvasInstance, Viewport viewport) {
		this.canvasInstance = canvasInstance;
		this.viewport = viewport;
		shapes = new ArrayList<MapShape>();
		lines = new ArrayList<MapLine>();
	}
	
	public void addShape(ActionEvent e) {
		int xGen;		// X-coordinate on screen to create the shape
		int yGen;		// Y-coordinate on screen to create the shape
		if (canvasInstance.isContextTrigger) {
			xGen = canvasInstance.contextTriggerEvent.getX() - viewport.xOffset;
			yGen = canvasInstance.contextTriggerEvent.getY() - viewport.yOffset;
		} else {
			Random rand = new Random();
			xGen = rand.nextInt(canvasInstance.getWidth());
			yGen = rand.nextInt(canvasInstance.getHeight());
		}
		switch (e.getActionCommand()) {
		case "Ellipse":
			shapes.add(new EllipseShape(xGen, yGen, 200, 100));
			break;
		case "Rectangle":
			shapes.add(new RectangleShape(xGen, yGen, 200, 100));
			break;
		}
		
		viewport.panning = true;
		canvasInstance.repaint();
	}
	
	public boolean isShapeUnderCursor(Point cursor) {
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(cursor))
				return true;
		}
		return false;
	}
	
	public void removeShapeUnderCursor() {
		// Offset cursor to be consistent with shape location
		Point offsetCursor = canvasInstance.contextTriggerEvent.getPoint();
		offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds()
					.contains(offsetCursor.getX(), offsetCursor.getY())) {
				shapes.remove(shape);
				break;
			}
		}
		canvasInstance.repaint();
	}
	
	public void changeBorderWidth(ChangeEvent e) {
		// Offset cursor to be consistent with shape location
		Point offsetCursor = canvasInstance.contextTriggerEvent.getPoint();
		offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(offsetCursor)) {
				shape.setBorderWidth(((JSlider)e.getSource()).getValue());
			}
		}
		canvasInstance.repaint();
	}
	
	public void changeBorderColour(ActionEvent e) {
		// Offset cursor to be consistent with shape location
		Point offsetCursor = canvasInstance.contextTriggerEvent.getPoint();
		offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(offsetCursor)) {
				shape.setBorderColour(ContextMenu.colours.get(e.getActionCommand()));
			}
		}
		canvasInstance.repaint();
	}
	
	public void changeFont(ActionEvent e) {
		// Offset cursor to be consistent with shape location
		Point offsetCursor = canvasInstance.contextTriggerEvent.getPoint();
		offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(offsetCursor)) {
				shape.setTextFont(
						new Font(e.getActionCommand(), shape.getTextFont().getStyle(), shape.getTextFont().getSize()));
			}
		}
		canvasInstance.repaint();
	}
	
	public void changeFontStyle(ActionEvent e) {
		// Offset cursor to be consistent with shape location
		Point offsetCursor = canvasInstance.contextTriggerEvent.getPoint();
		offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(offsetCursor)) {
				shape.setTextFont(new Font(shape.getTextFont().getFontName(), 
									Integer.parseInt(e.getActionCommand()), 
									shape.getTextFont().getSize()));
			}
		}
		canvasInstance.repaint();
	}
	
	public void changeFontSize(int newFontSize) {
		// Offset cursor to be consistent with shape location
		Point offsetCursor = canvasInstance.contextTriggerEvent.getPoint();
		offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(offsetCursor)) {
				shape.setTextFont(new Font(shape.getTextFont().getFontName(), 
									shape.getTextFont().getStyle(), 
									newFontSize));
			}
		}
		canvasInstance.repaint();
	}
	
	public void changeFontColour(ActionEvent e) {
		// Offset cursor to be consistent with shape location
		Point offsetCursor = canvasInstance.contextTriggerEvent.getPoint();
		offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(offsetCursor)) {
				shape.setFontColour(ContextMenu.colours.get(e.getActionCommand()));
			}
		}
		canvasInstance.repaint();
	}
	
	public void newConnection(MapShape origin, MapShape termination) {
		lines.add(new MapLine(origin, termination));
	}
	
	// Getters and setters
	public List<MapShape> getShapes() {
		return shapes;
	}
	public JsonArray getShapesAsJson() {
		JsonArray shapesData = new JsonArray();
		for (MapShape shape : canvasInstance.getShapesController().getShapes()) {
			shapesData.add(shape.getAsJsonObj());
		}
		return shapesData;
	}
	public void replaceShapes(List<MapShape> shapes) {
		this.shapes = shapes;
	}
	public void replaceShapesFromJson(JsonArray shapesData) {
		shapes = new ArrayList<MapShape>();
		for (JsonElement shape : shapesData) {
			JsonObject thisShape = shape.getAsJsonObject();
			try {
				// Create new MapShape from Json string using Java reflection
				Class<?> newMapShapeClass = Class.forName(thisShape.get("Type").getAsString());
				Constructor<?> newMapShapeConstructor = newMapShapeClass.getConstructor(
						new Class<?>[] {int.class, int.class, int.class, int.class});
				Object[] newMapShapeParameters = {thisShape.get("X").getAsInt(), thisShape.get("Y").getAsInt(), 
										thisShape.get("Width").getAsInt(), thisShape.get("Height").getAsInt()};
				MapShape newMapShape = (MapShape)newMapShapeConstructor.newInstance(newMapShapeParameters);
				
				newMapShape.setBorderColour(Color.decode(thisShape.get("Border colour").getAsString()));
				newMapShape.setText(thisShape.get("Text").getAsString());
				newMapShape.setTextFont(new Font(thisShape.get("Text font name").getAsString(), 
													thisShape.get("Text font style").getAsInt(), 
													thisShape.get("Text font size").getAsInt()));
				newMapShape.setFontColour(Color.decode(thisShape.get("Font colour").getAsString()));
				
				shapes.add(newMapShape);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | 
					NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	public MapShape getHighlightedShape() {
		return highlightedShape;
	}
	public void setHighlightedShape(MapShape highlightedShape) {
		this.highlightedShape = highlightedShape;
	}
	public List<MapLine> getLines() {
		return lines;
	}
	
}
