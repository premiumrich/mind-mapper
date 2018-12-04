package net.premiumrich.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;

import com.google.gson.*;

import net.premiumrich.io.*;
import net.premiumrich.shapes.*;

public class CanvasPanel extends JPanel {
	
	private static final long serialVersionUID = 0;
	static CanvasPanel canvasInstance;
	
	private ContextMenu contextMenu;
	private boolean isContextTrigger = false;
	private MouseEvent contextTriggerEvent;

	private static long lastFrameTime = 0;
	private static final int MAX_FPS = 60;
	private Timer fpsCounterUpdater;
	private Timer debugLabelsUpdater;
	
	private Point centerPoint;
	
	// Zoom and pan variables
	private double zoomFactor = 1;
	private double prevZoomFactor = 1;
	private boolean zooming;
	private double xOffset = 0;
	private double yOffset = 0;
	private boolean panning;
	private boolean released;
	private int xDiff;
	private int yDiff;
	private Point startPoint;
	
	public List<MapShape> shapes;
	
	public CanvasPanel() {
		canvasInstance = this;
		
		shapes = new ArrayList<MapShape>();
		centerPoint = new Point();
		
		initListeners();
		initTimers();
		contextMenu = new ContextMenu();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform at = new AffineTransform();
		
		if (zooming) {
			double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
			double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
			
			double zoomDiv = zoomFactor / prevZoomFactor;
			
			xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
			yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;
			
			at.translate(xOffset, yOffset);
			at.scale(zoomFactor, zoomFactor);
			g2d.transform(at);
			
			prevZoomFactor = zoomFactor;
			zooming = false;
        }
		
		if (panning) {
            at.translate(xOffset + xDiff, yOffset + yDiff);
            at.scale(zoomFactor, zoomFactor);
            g2d.transform(at);

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
    			xDiff = 0;
        		yDiff = 0;
                panning = false;
            }
        }
		
		for (MapShape mapShape : shapes) {		// Iterate and print all shapes
			g2d.draw(mapShape.getShape());
		}
	}

	public void updateCenter() {				// Find center of canvas
		centerPoint.setLocation(this.getWidth()/2, this.getHeight()/2);
	}
	
	private void initListeners() {
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (!isContextTrigger) {
					panning = true;
					
					Point curPoint = e.getLocationOnScreen();
					xDiff = curPoint.x - startPoint.x;
					yDiff = curPoint.y - startPoint.y;
					
					handleRepaint();
				}
			}
		});
		
		this.addMouseWheelListener(new MouseAdapter() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (!isContextTrigger) {
					zooming = true;
					if (e.getWheelRotation() < 0) {		// Zoom in
					    zoomFactor *= 1.1;
					    handleRepaint();
					}
					if (e.getWheelRotation() > 0) {		// Zoom out
					    zoomFactor /= 1.1;
					    handleRepaint();
					}
				}
			}
		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				popup(e);
				if (!isContextTrigger) {
					released = false;
					startPoint = MouseInfo.getPointerInfo().getLocation();
				}
			}

			public void mouseReleased(MouseEvent e) {
				popup(e);
				if (!isContextTrigger) {
					released = true;
					repaint();		// Bypass FPS limiter and force repaint to lock in position
	
					for (MapShape mapShape : shapes) {
						mapShape.setX( (mapShape.getX() + xDiff) );
						mapShape.setY( (mapShape.getY() + yDiff) );
					}
				}
			}
			private void popup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					isContextTrigger = true;
					contextTriggerEvent = e;
					if (findShapeUnderCursor(e.getX(), e.getY()) != null)
						contextMenu.editMenu.setVisible(true);
					else
						contextMenu.editMenu.setVisible(false);
					contextMenu.show(e.getComponent(), e.getX(), e.getY());
				}
				repaint();
			}
		});
	}
	
	private void handleRepaint() {		// A handler to limit framerate and CPU usage
		// Calculate frame time and only repaint at the specified framerate
		if (System.currentTimeMillis() - lastFrameTime >= (1000/MAX_FPS)) {
			repaint();
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
				PickerPanel.dDragXLbl.setText("Δ Drag X: " + xDiff);
				PickerPanel.dDragYLbl.setText("Δ Drag Y: " + yDiff);
				PickerPanel.tempXLbl.setText(shapes.size() != 0 ? "X: " + shapes.get(0).getX() : "X: null");
				PickerPanel.tempYLbl.setText(shapes.size() != 0 ? "Y: " + shapes.get(0).getY() : "Y: null");
			}
		});
		debugLabelsUpdater.start();
	}
	
	public void addShape(ActionEvent e) {
		int xGen;
		int yGen;
		if (isContextTrigger) {
			xGen = contextTriggerEvent.getX();
			yGen = contextTriggerEvent.getY();
			isContextTrigger = false;
		} else {
			Random rand = new Random();
			xGen = rand.nextInt(this.getWidth() - 200) + 200;
			yGen = rand.nextInt(this.getHeight() - 100) + 100;
		}
		switch (e.getActionCommand()) {
		case "+ellipse":
			shapes.add(new EllipseShape(xGen, yGen, 200, 100));
			break;
		case "+rectangle":
			shapes.add(new RectangleShape(xGen, yGen, 200, 100));
			break;
		}
		
		panning = true;
		repaint();
	}
	
	private MapShape findShapeUnderCursor(int mouseX, int mouseY) {
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(mouseX, mouseY))
				return shape;
		}
		return null;
	}
	
	public void removeShapeUnderCursor() {
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds()
					.contains(contextTriggerEvent.getX(), contextTriggerEvent.getY())) {
				shapes.remove(shape);
				break;
			}
		}
		repaint();
	}
	
	public static void handleOpen() {
		MindMapReader reader = new MindMapReader(new File("test.json"));
		
		canvasInstance.zoomFactor = reader.getViewportData().get("Zoom").getAsDouble();
		canvasInstance.prevZoomFactor = canvasInstance.zoomFactor;
		canvasInstance.xOffset = reader.getViewportData().get("xOffset").getAsDouble();
		canvasInstance.yOffset = reader.getViewportData().get("yOffset").getAsDouble();
		canvasInstance.panning = true;
		canvasInstance.released = true;
		
		canvasInstance.shapes = new ArrayList<MapShape>();
		for (JsonElement shape : reader.getShapesData()) {
			JsonObject thisShape = shape.getAsJsonObject();
			switch (thisShape.get("Type").getAsString()) {
			case "net.premiumrich.shapes.RectangleShape":
				canvasInstance.shapes.add(new RectangleShape(thisShape.get("X").getAsDouble(), 
										thisShape.get("Y").getAsDouble(), 
										thisShape.get("Width").getAsInt(), 
										thisShape.get("Height").getAsInt()));
				break;
			case "net.premiumrich.shapes.EllipseShape":
				canvasInstance.shapes.add(new EllipseShape(thisShape.get("X").getAsDouble(), 
										thisShape.get("Y").getAsDouble(), 
										thisShape.get("Width").getAsInt(), 
										thisShape.get("Height").getAsInt()));
				break;
			}
		}
		
		canvasInstance.repaint();
	}
	
	public static void handleSave() {
		MindMapWriter writer = new MindMapWriter(new File("test.json"));
		
		JsonObject viewportData = new JsonObject();
		viewportData.addProperty("Zoom", canvasInstance.zoomFactor);
		viewportData.addProperty("xOffset", 0.0);
		viewportData.addProperty("yOffset", 0.0);
		
		writer.add("Viewport", viewportData);
		
		JsonArray shapesData = new JsonArray();
		for (MapShape shape : canvasInstance.shapes) {
			shapesData.add(shape.getAsJsonObj());
		}
		writer.add("Shapes", shapesData);
		
		writer.save();
	}
	
}
