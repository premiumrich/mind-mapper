package net.premiumrich.ui;

import java.awt.BasicStroke;
import java.awt.Color;
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

/**
 * The Viewport manages and handles drawing mind map elements
 * @author premiumrich
 */
public class Viewport {

	private CanvasPanel canvasPanel;

	private static final int MAX_FPS = 60;
	private long lastFrameTime = 0;
	// Zoom and pan variables
	protected boolean zooming;
	public double zoomFactor = 1, prevZoomFactor = 1;
	protected boolean released;
	public boolean panning;
	public int xOffset = 0, yOffset = 0;
	protected Point panStartPoint;
	protected int panXDiff, panYDiff;
	
	private Timer fpsCounterUpdater;
	private Timer debugLabelsUpdater;
	
	public Viewport(CanvasPanel canvasPanel) {
		this.canvasPanel = canvasPanel;
		initTimers();
	}
	
	public void drawAll(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (zooming) {
			AffineTransform at = new AffineTransform();
			
			// Zoom relative to cursor
			double xRel = MouseInfo.getPointerInfo().getLocation().getX() - canvasPanel.getX();
			double yRel = MouseInfo.getPointerInfo().getLocation().getY() - canvasPanel.getY();
			double zoomDiv = zoomFactor / prevZoomFactor;
			xOffset = (int)((zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel);
			yOffset = (int)((zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel);
			
			at.translate(xOffset, yOffset);
			at.scale(zoomFactor, zoomFactor);
			g2d.transform(at);
			
			prevZoomFactor = zoomFactor;
			zooming = false;
        } else if (panning) {
			AffineTransform at = new AffineTransform();
			
            at.translate(xOffset + panXDiff, yOffset + panYDiff);
            at.scale(zoomFactor, zoomFactor);
            g2d.transform(at);

            if (released) {
                xOffset += panXDiff;
                yOffset += panYDiff;
    			panXDiff = 0;
        		panYDiff = 0;
            }
        }
		
		// Iterate and print all shapes and lines
		for (MapShape mapShape : canvasPanel.getShapesController().getShapes()) {
			if (mapShape.isHighlighted) g2d.setColor(Color.cyan);
			else g2d.setColor(mapShape.getBorderColour());
			g2d.setStroke(new BasicStroke(mapShape.getBorderWidth()));
			g2d.draw(mapShape.getShape());
			drawShapeText(g, mapShape);
		}
		for (MapLine connection : canvasPanel.getShapesController().getConnections()) {
			connection.updateConnection();
			g2d.draw(connection.getLine());
		}
	}

	private void drawShapeText(Graphics g, MapShape mapShape) {
		// Offset the location of text fields
		mapShape.getTextField().setBounds(mapShape.getX() + mapShape.getShape().getBounds().width/2 - 100 + xOffset, 
											mapShape.getY() + mapShape.getShape().getBounds().height/2 - 50 + yOffset,
											200, 100);
		Graphics2D textGraphics = (Graphics2D) g.create(mapShape.getTextField().getBounds().x - xOffset, 
											mapShape.getTextField().getBounds().y - yOffset, 
											mapShape.getTextField().getBounds().width, 
											mapShape.getTextField().getBounds().height);
		mapShape.getTextField().paint(textGraphics);
	}
	
	protected void handleRepaint() {		// A handler to limit framerate and CPU usage
		// Calculate frame time and only repaint at the specified framerate
		if (System.currentTimeMillis() - lastFrameTime >= (1000/MAX_FPS)) {
			canvasPanel.repaint();
			lastFrameTime = System.currentTimeMillis();
		}
	}
	
	// Viewport controls
	public void pan(Point curPoint) {
		panning = true;
		panXDiff = curPoint.x - panStartPoint.x;
		panYDiff = curPoint.y - panStartPoint.y;
		handleRepaint();
	}
	public void zoomIn() {
		zoomFactor *= 1.1;
	}
	public void zoomOut() {
		zoomFactor /= 1.1;
	}
	public void centerView() {
		xOffset = 0;
		yOffset = 0;
	}
	public void resetZoom() {
		zoomFactor = 0;
		prevZoomFactor = 0;
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
		prevZoomFactor = canvasPanel.getViewport().zoomFactor;
		xOffset = viewportData.get("xOffset").getAsInt();
		yOffset = viewportData.get("yOffset").getAsInt();
		panning = true;
		released = true;
	}
	
}
