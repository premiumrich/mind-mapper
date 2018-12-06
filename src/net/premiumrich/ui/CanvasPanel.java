package net.premiumrich.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;

import net.premiumrich.shapes.*;

public class CanvasPanel extends JPanel {
	
	private static final long serialVersionUID = 0;
	public static CanvasPanel canvasInstance;
	
	private ContextMenu contextMenu;
	public boolean isContextTrigger = false;
	private MouseEvent contextTriggerEvent;

	private static long lastFrameTime = 0;
	private static final int MAX_FPS = 60;
	private Timer fpsCounterUpdater;
	private Timer debugLabelsUpdater;
	
	// Zoom and pan variables
	public double zoomFactor = 1;
	public double prevZoomFactor = 1;
	private boolean zooming;
	public int xOffset = 0;
	public int yOffset = 0;
	public boolean panning;
	public boolean released;
	private int xDiff;
	private int yDiff;
	private Point startPoint;
	
	public List<MapShape> shapes;
	private MapShape highlightedShape;
	
	public CanvasPanel() {
		canvasInstance = this;
		this.setLayout(new OverlayLayout(this));
		
		shapes = new ArrayList<MapShape>();
		
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
			
			xOffset = (int) ((zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel);
			yOffset = (int) ((zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel);
			
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
            }
        }
		
		for (MapShape mapShape : shapes) {		// Iterate and print all shapes
			if (mapShape.isHighlighted) g2d.setColor(Color.cyan);
			else g2d.setColor(mapShape.getBorderColour());
			g2d.draw(mapShape.getShape());
			drawText(g2d, mapShape, mapShape.getText(), mapShape.getTextFont());
		}
	}

	private void drawText(Graphics2D g2d, MapShape mapShape, String text, Font font) {
		g2d.setColor(mapShape.getFontColour());
		FontMetrics metrics = g2d.getFontMetrics(font);
		int textX = mapShape.getX() + (mapShape.getShape().getBounds().width - metrics.stringWidth(text))/2 
					- xOffset;
	    int textY = mapShape.getY() + ((mapShape.getShape().getBounds().height - metrics.getHeight())/2) 
	    			+ metrics.getAscent() - yOffset;
		g2d.setFont(font);
		g2d.drawString(mapShape.getText(), textX, textY);
	}	
	
	private void handleRepaint() {		// A handler to limit framerate and CPU usage
		// Calculate frame time and only repaint at the specified framerate
		if (System.currentTimeMillis() - lastFrameTime >= (1000/MAX_FPS)) {
			repaint();
			lastFrameTime = System.currentTimeMillis();
		}
	}
	
	public void addShape(ActionEvent e) {
		int xGen;
		int yGen;
		if (isContextTrigger) {
			xGen = contextTriggerEvent.getX() - xOffset;
			yGen = contextTriggerEvent.getY() - yOffset;
		} else {
			Random rand = new Random();
			xGen = rand.nextInt(this.getWidth() - 200) + 200;
			yGen = rand.nextInt(this.getHeight() - 100) + 100;
		}
		switch (e.getActionCommand()) {
		case "Ellipse":
			shapes.add(new EllipseShape(xGen, yGen, 200, 100));
			break;
		case "Rectangle":
			shapes.add(new RectangleShape(xGen, yGen, 200, 100));
			break;
		}
		
		panning = true;
		repaint();
	}
	
	private boolean isShapeUnderCursor(Point cursor) {
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds().contains(cursor))
				return true;
		}
		return false;
	}
	
	public void removeShapeUnderCursor() {
		// Offset cursor to be consistent with shape location
		Point offsetCursor = contextTriggerEvent.getPoint();
		offsetCursor.translate(-(int)xOffset, -(int)yOffset);
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds()
					.contains(offsetCursor.getX(), offsetCursor.getY())) {
				shapes.remove(shape);
				break;
			}
		}
		repaint();
	}
	
	public void changeBorderColour(ActionEvent e) {
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds()
					.contains(contextTriggerEvent.getX(), contextTriggerEvent.getY())) {
				shape.setBorderColour(ContextMenu.colours.get(e.getActionCommand()));
			}
		}
		repaint();
	}
	
	public void changeFont(ActionEvent e) {
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds()
					.contains(contextTriggerEvent.getX(), contextTriggerEvent.getY())) {
				switch (e.getActionCommand()) {
				case "Serif":
					shape.setTextFont(
							new Font("Serif", shape.getTextFont().getStyle(), shape.getTextFont().getSize()));
					break;
				case "SansSerif":
					shape.setTextFont(
							new Font("SansSerif", shape.getTextFont().getStyle(), shape.getTextFont().getSize()));
					break;
				case "Monospaced":
					shape.setTextFont(
							new Font("Monospaced", shape.getTextFont().getStyle(), shape.getTextFont().getSize()));
					break;
				}
			}
		}
		repaint();
	}
	
	public void changeFontSize(ActionEvent e) {
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds()
					.contains(contextTriggerEvent.getX(), contextTriggerEvent.getY())) {
				shape.setTextFont(new Font(shape.getTextFont().getFontName(), 
									shape.getTextFont().getStyle(), 
									Integer.parseInt(e.getActionCommand())));
			}
		}
		repaint();
	}
	
	public void changeFontColour(ActionEvent e) {
		for (MapShape shape : shapes) {
			if (shape.getShape().getBounds()
					.contains(contextTriggerEvent.getX(), contextTriggerEvent.getY())) {
				shape.setFontColour(ContextMenu.colours.get(e.getActionCommand()));
			}
		}
		repaint();
	}
	
	private void initListeners() {
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point offsetCursor = e.getPoint();	// Offset cursor to be consistent with shape location
				offsetCursor.translate(-(int)xOffset, -(int)yOffset);
				
				boolean found = false;
				for (MapShape shape : shapes) {
					if (shape.getShape().getBounds().contains(offsetCursor)) {
						shape.isHighlighted = true;
						found = true;
						highlightedShape = shape;
					} else {
						shape.isHighlighted = false;
					}
				}
				if (!found) highlightedShape = null;
				
				handleRepaint();
			}
			
			public void mouseDragged(MouseEvent e) {
				if (highlightedShape == null && !isContextTrigger) {
					panning = true;
					
					Point curPoint = e.getLocationOnScreen();
					xDiff = curPoint.x - startPoint.x;
					yDiff = curPoint.y - startPoint.y;
					
					handleRepaint();
				}
			}
		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				popup(e);
				if (!contextMenu.isVisible()) isContextTrigger = false;
				startPoint = MouseInfo.getPointerInfo().getLocation();
				panning = true;
				released = false;
			}
			public void mouseReleased(MouseEvent e) {
				popup(e);
				released = true;
				repaint();		// Bypass FPS limiter and force repaint to lock in position
				
				for (MapShape mapShape : shapes) {
					mapShape.setX( (mapShape.getX() + xDiff) );
					mapShape.setY( (mapShape.getY() + yDiff) );
				}
			}
			private void popup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					isContextTrigger = true;
					contextTriggerEvent = e;
					Point offsetCursor = e.getPoint();	// Offset cursor to be consistent with shape location
					offsetCursor.translate(-(int)xOffset, -(int)yOffset);
					if (isShapeUnderCursor(offsetCursor))
						contextMenu.getEditMenu().setVisible(true);
					else
						contextMenu.getEditMenu().setVisible(false);
					contextMenu.show(e.getComponent(), e.getX(), e.getY());
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
	}
	
	private void initTimers() {
		fpsCounterUpdater = new Timer(250, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (System.currentTimeMillis() - lastFrameTime != 0)
					PickerPanel.fpsLbl.setText("FPS: " + 1000/(System.currentTimeMillis()-lastFrameTime));
			}
		});
		// fpsCounterUpdater.start();
		
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
	
}
