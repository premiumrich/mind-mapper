package net.premiumrich.io;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.premiumrich.main.AppFrame;
import net.premiumrich.ui.CanvasPanel;

public class IOController {
	
	private AppFrame appFrame;
	private CanvasPanel canvasPanel;
	
	private File currentFile;
	
	public IOController(AppFrame appFrame, CanvasPanel canvasPanel) {
		this.appFrame = appFrame;
		this.canvasPanel = canvasPanel;
	}
	
	public void handleSave(File file) {
		MindMapWriter writer = new MindMapWriter(file);
		writer.add("Viewport", canvasPanel.getViewport().getViewportData());
		writer.add("Shapes", canvasPanel.getShapesController().getShapesAsJson());
		writer.save();
		setCurrentFile(file);
	}
	
	public void handleOpen(File file) {
		MindMapReader reader = new MindMapReader(file);
		canvasPanel.getViewport().setViewportData(reader.getViewportData());
		canvasPanel.getShapesController().replaceShapesFromJson(reader.getShapesData());
		canvasPanel.repaint();
		setCurrentFile(file);
	}
	
	public void handleExport(File file, String imgType) {
		System.out.print("Exporting to " + file.getAbsolutePath() + " ... ");
		// Upscale exported image 4x to increase quality and enable transparency if exporting to PNG
		BufferedImage image = new BufferedImage(canvasPanel.getWidth() * 4, canvasPanel.getHeight() * 4, 
												imgType == "png" ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		// Increase quality of exported image
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.scale(4, 4);		// Upscale 4x
		canvasPanel.printAll(g2d);
		try {
		    ImageIO.write(image, imgType, file);
		    System.out.println("Success!");
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public File getCurrentFile() {
		return currentFile;
	}
	public void setCurrentFile(File file) {
		currentFile = file;
		appFrame.setOpenedFileName(file.getName().substring(0, file.getName().length() - 5));	// Truncate ".json"
	}
	
}
