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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.premiumrich.ui.CanvasPanel;
import net.premiumrich.ui.Viewport;

public class ShapesController {

	private CanvasPanel canvasPanel;
	private Viewport viewport;
	
	public Point dragStartPoint;
	
	private List<MapShape> shapes;
	public MapShape selectedShape;
	public MapShape prevSelectedShape;
	public int shapeSelectionIndex = 0;
	
	public boolean isConnecting;
	private List<MapLine> connections;
	public MapShape connectionOrigin;
	public MapShape connectionDestination;
	
	public ShapesController(CanvasPanel canvasPanel, Viewport viewport) {
		this.canvasPanel = canvasPanel;
		this.viewport = viewport;
		shapes = new ArrayList<MapShape>();
		connections = new ArrayList<MapLine>();
	}
	
	public void addShape(ActionEvent e) {
		int xGen;		// X-coordinate on screen to create the shape
		int yGen;		// Y-coordinate on screen to create the shape
		if (canvasPanel.isContextTrigger) {
			xGen = canvasPanel.contextTriggerEvent.getX() - viewport.xOffset;
			yGen = canvasPanel.contextTriggerEvent.getY() - viewport.yOffset;
		} else {
			Random rand = new Random();
			xGen = rand.nextInt(canvasPanel.getWidth());
			yGen = rand.nextInt(canvasPanel.getHeight());
		}
		switch (e.getActionCommand()) {
		case "Ellipse":
			shapes.add(new EllipseShape(xGen, yGen, 200, 100));
			break;
		case "Rectangle":
			shapes.add(new RectangleShape(xGen, yGen, 200, 100));
			break;
		}
		
		// Select the newly added shape
		setSelectedShape(null);
		setSelectedShape(shapes.get(shapes.size()-1));
		
		viewport.panning = true;
		canvasPanel.repaint();
	}
	
	public List<MapShape> getShapesUnderCursor(Point cursor) {
		// Offset cursor to be consistent with shape location
		cursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		
		List<MapShape> shapesUnderCursor = new ArrayList<MapShape>();
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(cursor))
				shapesUnderCursor.add(shape);
		}
		return shapesUnderCursor;
	}
	
	public void removeSelectedShape() {
		shapes.remove(selectedShape);
		canvasPanel.repaint();
	}
	
	public void changeBorderWidth(int borderWidth) {
		selectedShape.setBorderWidth(borderWidth);
		canvasPanel.repaint();
	}
	
	public void changeBorderColour(Color borderColour) {
		selectedShape.setBorderColour(borderColour);
		canvasPanel.repaint();
	}
	
	public void changeFont(String fontName) {
		selectedShape.setTextFont(new Font(fontName, 
									selectedShape.getTextFont().getStyle(), 
									selectedShape.getTextFont().getSize()));
		canvasPanel.repaint();
	}
	
	public void changeFontStyle(int fontStyle) {
		selectedShape.setTextFont(new Font(selectedShape.getTextFont().getFamily(), 
									fontStyle,
									selectedShape.getTextFont().getSize()));
		canvasPanel.repaint();
	}
	
	public void changeFontSize(int fontSize) {
		selectedShape.setTextFont(new Font(selectedShape.getTextFont().getFamily(), 
									selectedShape.getTextFont().getStyle(), 
									fontSize));
		canvasPanel.repaint();
	}
	
	public void changeFontColour(Color fontColour) {
		selectedShape.setFontColour(fontColour);
		canvasPanel.repaint();
	}
	
	public void newConnection(MapShape origin, MapShape termination) {
		connections.add(new MapLine(origin, termination));
	}
	
	
	// Getters and setters
	public List<MapShape> getShapes() {
		return shapes;
	}
	public MapShape getSelectedShape() {
		return selectedShape;
	}
	public void setSelectedShape(MapShape selectedShape) {
		this.selectedShape = selectedShape;
		if (prevSelectedShape != null) prevSelectedShape.isHighlighted = false;
		if (selectedShape != null)
			selectedShape.isHighlighted = true;
		else
			for (MapShape shape : shapes) shape.isHighlighted = false;
	}
	public List<MapLine> getConnections() {
		return connections;
	}
	public JsonArray getShapesAsJson() {
		JsonArray shapesData = new JsonArray();
		for (MapShape shape : canvasPanel.getShapesController().getShapes()) {
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
	
}
