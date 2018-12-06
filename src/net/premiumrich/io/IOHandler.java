package net.premiumrich.io;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.premiumrich.shapes.EllipseShape;
import net.premiumrich.shapes.MapShape;
import net.premiumrich.shapes.RectangleShape;
import net.premiumrich.ui.CanvasPanel;

public class IOHandler {

	public static void handleOpen() {
		MindMapReader reader = new MindMapReader(new File("test.json"));
		
		CanvasPanel.canvasInstance.zoomFactor = reader.getViewportData().get("Zoom").getAsDouble();
		CanvasPanel.canvasInstance.prevZoomFactor = CanvasPanel.canvasInstance.zoomFactor;
		CanvasPanel.canvasInstance.xOffset = reader.getViewportData().get("xOffset").getAsInt();
		CanvasPanel.canvasInstance.yOffset = reader.getViewportData().get("yOffset").getAsInt();
		CanvasPanel.canvasInstance.panning = true;
		CanvasPanel.canvasInstance.released = true;
		
		CanvasPanel.canvasInstance.shapes = new ArrayList<MapShape>();
		for (JsonElement shape : reader.getShapesData()) {
			JsonObject thisShape = shape.getAsJsonObject();
			switch (thisShape.get("Type").getAsString()) {
			case "net.premiumrich.shapes.RectangleShape":
				CanvasPanel.canvasInstance.shapes.add(new RectangleShape(thisShape.get("X").getAsInt(), 
										thisShape.get("Y").getAsInt(), 
										thisShape.get("Width").getAsInt(), 
										thisShape.get("Height").getAsInt()));
				break;
			case "net.premiumrich.shapes.EllipseShape":
				CanvasPanel.canvasInstance.shapes.add(new EllipseShape(thisShape.get("X").getAsInt(), 
										thisShape.get("Y").getAsInt(), 
										thisShape.get("Width").getAsInt(), 
										thisShape.get("Height").getAsInt()));
				break;
			}
		}
		
		CanvasPanel.canvasInstance.repaint();
	}
	
	public static void handleSave() {
		MindMapWriter writer = new MindMapWriter(new File("test.json"));
		
		JsonObject viewportData = new JsonObject();
		viewportData.addProperty("Zoom", CanvasPanel.canvasInstance.zoomFactor);
		viewportData.addProperty("xOffset", 0.0);
		viewportData.addProperty("yOffset", 0.0);
		
		writer.add("Viewport", viewportData);
		
		JsonArray shapesData = new JsonArray();
		for (MapShape shape : CanvasPanel.canvasInstance.shapes) {
			shapesData.add(shape.getAsJsonObj());
		}
		writer.add("Shapes", shapesData);
		
		writer.save();
	}
	
}
