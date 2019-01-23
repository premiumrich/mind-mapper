package net.premiumrich.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.premiumrich.ui.CanvasPanel;
import net.premiumrich.ui.Viewport;

/**
 * The MapController manages and controls mind map elements
 * @author premiumrich
 */
public class MapController {
	
	public Point dragStartPoint;
	
	private List<MapShape> shapes;
	private MapShape selectedShape;
	private MapShape prevSelectedShape;
	public int shapeSelectionIndex = 0;
	private MapShape editingShape;
	
	public boolean isConnecting;
	private List<MapLine> connections;
	private MapShape origin;

	private CanvasPanel canvasPanel;
	private Viewport viewport;
	
	public MapController(CanvasPanel canvasPanel, Viewport viewport) {
		this.canvasPanel = canvasPanel;
		this.viewport = viewport;
		reset();
	}
	
	public void reset() {
		shapes = new ArrayList<MapShape>();
		connections = new ArrayList<MapLine>();
	}
	
	public void addShape(String shapeClassName) {
		int shapeWidth = 200, shapeHeight = 100;
		Point p = new Point();				// Point on screen to create the shape
		if (canvasPanel.isContextTrigger) {
			// Offset cursor to be consistent with shape location in viewport
			p.x = (int) ((canvasPanel.contextTriggerEvent.getX() - viewport.xOffset) / viewport.zoomFactor);
			p.y = (int) ((canvasPanel.contextTriggerEvent.getY() - viewport.yOffset) / viewport.zoomFactor);
		} else {
			// Start from center and find vacant location
			p.x = canvasPanel.getWidth()/2;
			p.y = canvasPanel.getHeight()/2;
			p = findVacantPoint(p);
		}
		
		try {
			// Create new MapShape at center of point using Java reflection
			Class<?> newMapShapeClass = Class.forName(shapeClassName);
			Constructor<?> newMapShapeCons = newMapShapeClass.getConstructor(
												new Class<?>[] {int.class, int.class, int.class, int.class});
			Object[] newMapShapeParameters = {p.x-(shapeWidth/2), p.y-(shapeHeight/2), shapeWidth, shapeHeight};
			MapShape newMapShape = (MapShape)newMapShapeCons.newInstance(newMapShapeParameters);
			
			// Continously repaint panel when editing to display changes in the text field 
			newMapShape.getTextField().getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					canvasPanel.repaint();
				}
				public void removeUpdate(DocumentEvent e) {
					canvasPanel.repaint();
				}
				public void changedUpdate(DocumentEvent e) {
				}
			});
			
			shapes.add(newMapShape);
			
			setSelectedShape(null);
			setSelectedShape(newMapShape);		// Select the newly added shape
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		canvasPanel.repaint();
	}
	
	/**
	 * Recursively check if point is already occupied by a shape and offset southeast if true
	 * @param p Starting point
	 * @return Vacant point
	 */
	private Point findVacantPoint(Point p) {
		for (MapShape shape : shapes) {
			Point shapeLocation = shape.getShape().getBounds().getLocation();
			shapeLocation.translate(shape.getShape().getBounds().width/2, shape.getShape().getBounds().height/2);
			if (p.equals(shapeLocation)) {
				p.translate(20, 20);
				return findVacantPoint(p);
			}
		}
		return p;
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
		selectedShape.getTextField().setFont(new Font(fontName, 
									selectedShape.getTextField().getFont().getStyle(), 
									selectedShape.getTextField().getFont().getSize()));
		canvasPanel.repaint();
	}
	
	public void changeFontStyle(int fontStyle) {
		selectedShape.getTextField().setFont(new Font(selectedShape.getTextField().getFont().getFamily(), 
									fontStyle,
									selectedShape.getTextField().getFont().getSize()));
		canvasPanel.repaint();
	}
	
	public void changeFontSize(int fontSize) {
		selectedShape.getTextField().setFont(new Font(selectedShape.getTextField().getFont().getFamily(), 
									selectedShape.getTextField().getFont().getStyle(), 
									fontSize));
		canvasPanel.repaint();
	}
	
	public void changeFontColour(Color fontColour) {
		selectedShape.getTextField().setForeground(fontColour);
		canvasPanel.repaint();
	}
	
	public void bringSelectedShapeToFront() {
		Collections.swap(shapes, shapes.indexOf(selectedShape), shapes.size()-1);
		canvasPanel.repaint();
	}

	public void bringSelectedShapeForwards() {
		// Do nothing if selected shape is already at the front
		if (! (shapes.indexOf(selectedShape) == shapes.size()-1)) {
			Collections.swap(shapes, shapes.indexOf(selectedShape), shapes.indexOf(selectedShape)+1);
			canvasPanel.repaint();
		}
	}

	public void sendSelectedShapeToBack() {
		Collections.swap(shapes, shapes.indexOf(selectedShape), 0);
		canvasPanel.repaint();
	}

	public void sendSelectedShapeBackward() {
		// Do nothing if selected shape is already at the back
		if (! (shapes.indexOf(selectedShape) == 0)) {
			Collections.swap(shapes, shapes.indexOf(selectedShape), shapes.indexOf(selectedShape)-1);
			canvasPanel.repaint();
		}
	}

	public void setSelectedShapeAsOrigin() {
		origin = selectedShape;
	}
	
	public void setSelectedShapeAsTermination() {
		connections.add(new MapLine(origin, selectedShape));
		canvasPanel.repaint();
		origin = null;
	}
	
	public void removeConnectionsFromSelectedShape() {
		origin = null;
		List<MapLine> toBeRemoved = new ArrayList<MapLine>();
		for (MapLine connection : connections) {
			if (connection.getOrigin() == selectedShape || connection.getTermination() == selectedShape) {
				toBeRemoved.add(connection);
			}
		}
		connections.removeAll(toBeRemoved);
		canvasPanel.repaint();
	}
	
	
	// Getters and setters
	public List<MapShape> getShapes() {
		return shapes;
	}
	public List<MapShape> getShapesUnderCursor(Point cursor) {
		// Offset cursor to be consistent with shape location in viewport
		cursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		cursor.x /= viewport.zoomFactor;
		cursor.y /= viewport.zoomFactor;
		
		List<MapShape> shapesUnderCursor = new ArrayList<MapShape>();
		for (MapShape shape : shapes)
			if (shape.getShape().getBounds().contains(cursor)) shapesUnderCursor.add(shape);
		return shapesUnderCursor;
	}
	public MapShape getSelectedShape() {
		return selectedShape;
	}
	public void setSelectedShape(MapShape selectedShape) {
		prevSelectedShape = this.selectedShape;
		this.selectedShape = selectedShape;
		if (prevSelectedShape != null) prevSelectedShape.isHighlighted = false;
		if (selectedShape != null) selectedShape.isHighlighted = true;
		else {		// If null is passed in, disable highlight for all shapes
			for (MapShape shape : shapes) shape.isHighlighted = false;
			shapeSelectionIndex = 0;
		}
	}
	public MapShape getEditingShape() {
		return editingShape;
	}
	public void setEditingShape(MapShape editingShape) {
		this.editingShape = editingShape;
	}
	public List<MapLine> getConnections() {
		return connections;
	}
	public MapShape getConnectionOrigin() {
		return origin;
	}
	public JsonArray getShapesAsJson() {
		JsonArray shapesData = new JsonArray();
		for (MapShape shape : canvasPanel.getMapController().getShapes()) {
			shapesData.add(shape.getAsJsonObj());
		}
		return shapesData;
	}
	public void replaceShapes(List<MapShape> shapes) {
		this.shapes = shapes;
	}
	public void replaceShapesFromJson(JsonArray shapesData) {
		reset();
		for (JsonElement shape : shapesData) {
			JsonObject thisShape = shape.getAsJsonObject();
			try {
				// Create new MapShape from Json string using Java reflection
				Class<?> newMapShapeClass = Class.forName(thisShape.get("Type").getAsString());
				Constructor<?> newMapShapeCons = newMapShapeClass.getConstructor(
													new Class<?>[] {int.class, int.class, int.class, int.class});
				Object[] newMapShapeParameters = {thisShape.get("X").getAsInt(), thisShape.get("Y").getAsInt(), 
										thisShape.get("Width").getAsInt(), thisShape.get("Height").getAsInt()};
				MapShape newMapShape = (MapShape)newMapShapeCons.newInstance(newMapShapeParameters);
				
				newMapShape.setBorderWidth(thisShape.get("Border width").getAsInt());
				newMapShape.setBorderColour(Color.decode(thisShape.get("Border colour").getAsString()));
				newMapShape.getTextField().setText(thisShape.get("Text").getAsString());
				newMapShape.getTextField().setFont(new Font(thisShape.get("Text font name").getAsString(), 
													thisShape.get("Text font style").getAsInt(), 
													thisShape.get("Text font size").getAsInt()));
				newMapShape.getTextField().setForeground(Color.decode(thisShape.get("Font colour").getAsString()));
				
				// Continously repaint panel when editing to display changes in the text field 
				newMapShape.getTextField().getDocument().addDocumentListener(new DocumentListener() {
					public void insertUpdate(DocumentEvent e) {
						canvasPanel.repaint();
					}
					public void removeUpdate(DocumentEvent e) {
						canvasPanel.repaint();
					}
					public void changedUpdate(DocumentEvent e) {
					}
				});

				shapes.add(newMapShape);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
