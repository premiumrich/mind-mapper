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

	private static final int MAX_FPS = 60;
	private long lastFrameTime = 0;
	// Zoom and pan variables
	private boolean zooming;
	public double zoomFactor = 1, prevZoomFactor = 1;
	private boolean released;
	private boolean panning;
	public int xOffset = 0, yOffset = 0;
	protected Point panStartPoint;
	protected int panXDiff, panYDiff;
	
	private Timer fpsCounterUpdater;
	private Timer debugLabelsUpdater;
	
	private CanvasPanel canvasPanel;
	
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

		// Iterate and print all lines before shapes
		for (MapLine connection : canvasPanel.getShapesController().getConnections()) {
			connection.updateConnection();
			g2d.setColor(Color.black);
			g2d.setStroke(new BasicStroke(2));
			g2d.draw(connection.getLine());
		}
		for (MapShape mapShape : canvasPanel.getShapesController().getShapes()) {
			// Fill shape background with white to hide lines within the shape
			g2d.setColor(Color.white);
			g2d.fill(mapShape.getShape());
			// Draw border around shape
			if (mapShape.isHighlighted) g2d.setColor(Color.cyan);
			else g2d.setColor(mapShape.getBorderColour());
			g2d.setStroke(new BasicStroke(mapShape.getBorderWidth()));
			g2d.draw(mapShape.getShape());
			// Draw text
			drawShapeText(g, mapShape);
		}
	}

	private void drawShapeText(Graphics g, MapShape mapShape) {
		/**
		 * Draw text from textfield only, if not being edited, or position the textfield correctly if being edited
		 */
		if (! mapShape.equals(canvasPanel.getShapesController().getEditingShape())) {
			// Offset the location of text fields to center of shape
			mapShape.getTextField().setBounds(mapShape.getX() + mapShape.getShape().getBounds().width/2 - 100 + xOffset, 
												mapShape.getY() + mapShape.getShape().getBounds().height/2 - 50 + yOffset,
												200, 100);
			Graphics2D textGraphics = (Graphics2D) g.create(mapShape.getTextField().getBounds().x - xOffset, 
												mapShape.getTextField().getBounds().y - yOffset, 
												mapShape.getTextField().getBounds().width, 
												mapShape.getTextField().getBounds().height);
			mapShape.getTextField().paint(textGraphics);
		} else {
			mapShape.getTextField().setBounds(mapShape.getX() + mapShape.getShape().getBounds().width/2 - 100, 
												mapShape.getY() + mapShape.getShape().getBounds().height/2 - 50,
												200, 100);
		}
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
		zooming = true;
		zoomFactor *= 1.1;
		handleRepaint();
	}
	public void zoomOut() {
		zooming = true;
		zoomFactor /= 1.1;
		handleRepaint();
	}
	public void centerView() {
		// Calculate average center
		int numShapes = canvasPanel.getShapesController().getShapes().size();
		int totalX = 0, totalY = 0;
		for (MapShape shape : canvasPanel.getShapesController().getShapes()) {
			totalX += shape.getX() + shape.getShape().getBounds().getWidth()/2;
			totalY += shape.getY() + shape.getShape().getBounds().getHeight()/2;
		}
		xOffset = -(totalX/numShapes - canvasPanel.getWidth()/2);
		yOffset = -(totalY/numShapes - canvasPanel.getHeight()/2);
		
		panning = true;
		canvasPanel.repaint();
	}
	public void reset() {
		xOffset = 0; yOffset = 0;
		zoomFactor = 1.0; prevZoomFactor = 1.0;
		panning = true;
		released = true;
		zooming = true;
		canvasPanel.repaint();
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
	public void setMouseReleased(boolean state) {
		released = state;
	}
	public JsonObject getViewportData() {
		JsonObject viewportData = new JsonObject();
		viewportData.addProperty("Zoom", zoomFactor);
		viewportData.addProperty("xOffset", xOffset);
		viewportData.addProperty("yOffset", yOffset);
		return viewportData;
	}
	public void setViewportData(JsonObject viewportData) {
		reset();
		zoomFactor = viewportData.get("Zoom").getAsDouble();
		prevZoomFactor = canvasPanel.getViewport().zoomFactor;
		xOffset = viewportData.get("xOffset").getAsInt();
		yOffset = viewportData.get("yOffset").getAsInt();
		panning = true;
		released = true;
	}
	
}
