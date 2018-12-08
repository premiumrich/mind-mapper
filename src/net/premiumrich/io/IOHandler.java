package net.premiumrich.io;

import java.io.File;
import net.premiumrich.ui.CanvasPanel;

public class IOHandler {
	
	private CanvasPanel canvasInstance;
	
	public IOHandler(CanvasPanel canvasInstance) {
		this.canvasInstance = canvasInstance;
	}
	
	public void handleSave() {
		MindMapWriter writer = new MindMapWriter(new File("test.json"));
		writer.add("Viewport", canvasInstance.getViewport().getViewportData());
		writer.add("Shapes", canvasInstance.getShapesController().getShapesAsJson());
		writer.save();
	}
	
	public void handleOpen() {
		MindMapReader reader = new MindMapReader(new File("test.json"));
		canvasInstance.getViewport().setViewportData(reader.getViewportData());
		canvasInstance.getShapesController().replaceShapesFromJson(reader.getShapesData());
		canvasInstance.repaint();
	}
	
}
