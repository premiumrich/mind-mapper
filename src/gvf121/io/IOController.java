package gvf121.io;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import gvf121.main.AppFrame;
import gvf121.ui.CanvasPanel;

/**
 * The IOController handles the opening, saving and exporting of mind maps 
 * @author premiumrich
 */
public class IOController {
	
	private File currentFile;
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private AppFrame appFrame;
	private CanvasPanel canvasPanel;
	
	public IOController(AppFrame appFrame, CanvasPanel canvasPanel) {
		this.appFrame = appFrame;
		this.canvasPanel = canvasPanel;
	}
	
	public void handleOpen(File inFile) {
		System.out.print("Opening " + inFile.getAbsolutePath() + " ... ");
		try {
			JsonReader reader = new JsonReader(new FileReader(inFile));
			JsonObject data = gson.fromJson(reader, JsonObject.class);
			canvasPanel.getViewport().setViewportData(data.get("Viewport").getAsJsonObject());
			canvasPanel.getMapController().replaceShapesFromJson(data.get("Shapes").getAsJsonArray());
			canvasPanel.getMapController().replaceConnectionsFromJson(data.get("Connections").getAsJsonArray());
			canvasPanel.repaint();
			setCurrentFile(inFile);
			System.out.println("Success");
		} catch (IOException e) {
			System.out.println("File not found! " + e);
		}
	}
	
	public void handleSave(File outFile) {
		System.out.print("Saving to " + outFile.getAbsolutePath() + " ... ");
		try {
			PrintWriter p = new PrintWriter(new FileWriter(outFile));
			JsonObject output = new JsonObject();
			output.add("Viewport", gson.toJsonTree(canvasPanel.getViewport().getViewportData()));
			output.add("Shapes", gson.toJsonTree(canvasPanel.getMapController().getShapesAsJsonArray()));
			output.add("Connections", gson.toJsonTree(canvasPanel.getMapController().getConnectionsAsJsonArray()));
			p.write(gson.toJson(output));
			p.close();
			setCurrentFile(outFile);
			System.out.println("Success");
		} catch (IOException e) {
			System.out.println("File not found! " + e);
		}
	}
	
	public void handleExport(File file, String imgType, int scale) {
		System.out.print("Exporting to " + file.getAbsolutePath() + " ... ");
		// Upscale exported image to increase quality and enable transparency if exporting to PNG
		BufferedImage image = new BufferedImage(canvasPanel.getWidth() * scale, canvasPanel.getHeight() * scale, 
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
		g2d.scale(scale, scale);		// Upscale
		canvasPanel.printAll(g2d);
		try {
		    ImageIO.write(image, imgType, file);
		    System.out.println("Success");
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public File getCurrentFile() {
		return currentFile;
	}
	public void setCurrentFile(File file) {
		currentFile = file;
		appFrame.setAppTitle(file.getName().substring(0, file.getName().length() - 5));	// Truncate ".json"
	}
	
}
