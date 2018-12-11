package net.premiumrich.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.Timer;

import com.google.gson.JsonObject;

import net.premiumrich.shapes.MapLine;
import net.premiumrich.shapes.MapShape;

public class Viewport {

	private CanvasPanel canvasInstance;

	long lastFrameTime = 0;
	private static final int MAX_FPS = 60;
	// Zoom and pan variables
	public boolean zooming;
	public double zoomFactor = 1;
	public double prevZoomFactor = 1;
	public boolean panning;
	public boolean released;
	public int xOffset = 0;
	public int yOffset = 0;
	public Point startPoint;
	public int xDiff;
	public int yDiff;
	
	private Timer fpsCounterUpdater;
	private Timer debugLabelsUpdater;
	
	public Viewport(CanvasPanel canvasInstance) {
		this.canvasInstance = canvasInstance;
		initTimers();
	}
	
	public void drawAll(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (zooming) {
			AffineTransform at = new AffineTransform();
			double xRel = MouseInfo.getPointerInfo().getLocation().getX() - canvasInstance.getX();
			double yRel = MouseInfo.getPointerInfo().getLocation().getY() - canvasInstance.getY();
			
			double zoomDiv = zoomFactor / prevZoomFactor;
			
			xOffset = (int) ((zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel);
			yOffset = (int) ((zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel);
			
			at.translate(xOffset, yOffset);
			at.scale(zoomFactor, zoomFactor);
			g2d.transform(at);
			
			prevZoomFactor = zoomFactor;
			zooming = false;
        }
		
		if (panning) {
			AffineTransform at = new AffineTransform();
            at.translate(xOffset + xDiff, yOffset + yDiff);
            at.scale(zoomFactor, zoomFactor);
            g2d.transform(at);

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
    			xDiff = 0;
        		yDiff = 0;
            }
        }
		
		// Iterate and print all shapes and lines
		for (MapShape mapShape : canvasInstance.getShapesController().getShapes()) {
			if (mapShape.isHighlighted) g2d.setColor(Color.cyan);
			else g2d.setColor(mapShape.getBorderColour());
			g2d.setStroke(new BasicStroke(mapShape.getBorderWidth()));
			g2d.draw(mapShape.getShape());
			drawShapeText(g2d, mapShape);
		}
		for (MapLine connection : canvasInstance.getShapesController().getConnections()) {
			connection.updateConnection();
			g2d.draw(connection.getLine());
		}
	}

	private void drawShapeText(Graphics2D g2d, MapShape mapShape) {
		g2d.setColor(mapShape.getFontColour());
		FontMetrics metrics = g2d.getFontMetrics(mapShape.getTextFont());
		int textX = mapShape.getX() + (mapShape.getShape().getBounds().width - metrics.stringWidth(mapShape.getText()))/2;
	    int textY = mapShape.getY() + ((mapShape.getShape().getBounds().height - metrics.getHeight())/2) 
	    			+ metrics.getAscent();
		g2d.setFont(mapShape.getTextFont());
		g2d.drawString(mapShape.getText(), textX, textY);
	}
	
	void handleRepaint() {		// A handler to limit framerate and CPU usage
		// Calculate frame time and only repaint at the specified framerate
		if (System.currentTimeMillis() - lastFrameTime >= (1000/MAX_FPS)) {
			canvasInstance.repaint();
			lastFrameTime = System.currentTimeMillis();
		}
	}
	
	private void initTimers() {
		fpsCounterUpdater = new Timer(250, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (System.currentTimeMillis() - lastFrameTime != 0)
					PickerPanel.fpsLbl.setText("FPS: " + 1000/(System.currentTimeMillis()-lastFrameTime));
			}
		});
		fpsCounterUpdater.start();
		
		debugLabelsUpdater = new Timer(150, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PickerPanel.zoomLbl.setText("Zoom: " + Double.toString(Math.round(zoomFactor*100)/100.0));
				PickerPanel.xOffsetLbl.setText("xOffset: " + xOffset);
				PickerPanel.yOffsetLbl.setText("yOffset: " + yOffset);
			}
		});
		debugLabelsUpdater.start();
	}
	
	// Getters and setters
	public JsonObject getViewportData() {
		JsonObject viewportData = new JsonObject();
		viewportData.addProperty("Zoom", zoomFactor);
		viewportData.addProperty("xOffset", xOffset);
		viewportData.addProperty("yOffset", yOffset);
		return viewportData;
	}
	public void setViewportData(JsonObject viewportData) {
		zoomFactor = viewportData.get("Zoom").getAsDouble();
		prevZoomFactor = canvasInstance.getViewport().zoomFactor;
		xOffset = viewportData.get("xOffset").getAsInt();
		yOffset = viewportData.get("yOffset").getAsInt();
		panning = true;
		released = true;
	}
	
}
