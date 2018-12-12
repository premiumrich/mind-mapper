package net.premiumrich.io;

import java.io.File;
import net.premiumrich.ui.CanvasPanel;

public class IOController {
	
	private CanvasPanel canvasInstance;
	
	public boolean fileOpened = false;
	private File currentFile;
	
	public IOController(CanvasPanel canvasInstance) {
		this.canvasInstance = canvasInstance;
	}
	
	public void handleSave(File file) {
		MindMapWriter writer = new MindMapWriter(file);
		writer.add("Viewport", canvasInstance.getViewport().getViewportData());
		writer.add("Shapes", canvasInstance.getShapesController().getShapesAsJson());
		writer.save();
		currentFile = file;
	}
	
	public void handleOpen(File file) {
		MindMapReader reader = new MindMapReader(file);
		canvasInstance.getViewport().setViewportData(reader.getViewportData());
		canvasInstance.getShapesController().replaceShapesFromJson(reader.getShapesData());
		canvasInstance.repaint();
		currentFile = file;
		fileOpened = true;
	}
	
	public File getCurrentFile() {
		return currentFile;
	}
	
}
