package net.premiumrich.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import net.premiumrich.shapes.*;

public class CanvasPanel extends JPanel {
	
	private static final long serialVersionUID = 0;

	private Point centerPoint;
	
	private double zoomFactor = 1;
	private double prevZoomFactor = 1;
	private boolean zooming;
	private double xOffset = 0;
	private double yOffset = 0;
	private boolean dragging;
	private boolean released;
	private int xDiff;
	private int yDiff;
	private Point startPoint;
	
	private List<MapShape> shapes;
	
	public CanvasPanel() {
		shapes = new ArrayList<MapShape>();
		
		initComponents();
	}
	
	private void initComponents() {
		initListeners();
		
		centerPoint = new Point();
		
		shapes.add(new RectangleShape(50, 50, 100, 200));
		shapes.add(new EllipseShape(200, 200, 100, 100));
		
		initDebugLabels();
	}
	
	private void initDebugLabels() {
		PickerPanel.zoomLbl.setText("Zoom: " + zoomFactor);
		PickerPanel.mouseXLbl.setText("Mouse X: " + MouseInfo.getPointerInfo().getLocation().x);
		PickerPanel.mouseYLbl.setText("Mouse Y: " + MouseInfo.getPointerInfo().getLocation().y);
		PickerPanel.dDragXLbl.setText("Δ Drag X: " + xDiff);
		PickerPanel.dDragYLbl.setText("Δ Drag Y: " + yDiff);
		PickerPanel.tempXLbl.setText("X: " + shapes.get(0).getX());
		PickerPanel.tempYLbl.setText("Y: " + shapes.get(0).getY());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
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
		
		if (dragging) {
            at.translate(xOffset + xDiff, yOffset + yDiff);
            at.scale(zoomFactor, zoomFactor);
            g2d.transform(at);

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
                dragging = false;
        		xDiff = 0;
                yDiff = 0;
            }
            
            PickerPanel.dDragXLbl.setText("Δ Drag X: " + xDiff);
    		PickerPanel.dDragYLbl.setText("Δ Drag Y: " + yDiff);
        }
		
		for (MapShape mapShape : shapes) {		// Iterate and print all shapes
			g2d.fill(mapShape.getShape());
		}
	}

	public void updateCenter() {				// Find center of canvas
		centerPoint.setLocation(this.getWidth()/2, this.getHeight()/2);
	}
	
	private void initListeners() {
		this.addMouseWheelListener(new MouseAdapter() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				zooming = true;
				if (e.getWheelRotation() < 0) {		// Zoom in
				    zoomFactor *= 1.1;
				    repaint();
				}
				if (e.getWheelRotation() > 0) {		// Zoom out
				    zoomFactor /= 1.1;
				    repaint();
				}
				PickerPanel.zoomLbl.setText("Zoom: " + Double.toString(Math.round(zoomFactor*100)/100.0));
			}
		});
		
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				PickerPanel.mouseXLbl.setText("Mouse X: " + MouseInfo.getPointerInfo().getLocation().x);
				PickerPanel.mouseYLbl.setText("Mouse Y: " + MouseInfo.getPointerInfo().getLocation().y);
			}
			
			public void mouseDragged(MouseEvent e) {
				dragging = true;
				
				Point curPoint = e.getLocationOnScreen();
				xDiff = curPoint.x - startPoint.x;
				yDiff = curPoint.y - startPoint.y;
				
				repaint();
				
				PickerPanel.mouseXLbl.setText("Mouse X: " + MouseInfo.getPointerInfo().getLocation().x);
				PickerPanel.mouseYLbl.setText("Mouse Y: " + MouseInfo.getPointerInfo().getLocation().y);
				
				PickerPanel.tempXLbl.setText("X: " + shapes.get(0).getX());
				PickerPanel.tempYLbl.setText("Y: " + shapes.get(0).getY());
			}
		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				released = false;
				startPoint = MouseInfo.getPointerInfo().getLocation();
			}

			public void mouseReleased(MouseEvent e) {
				released = true;
				repaint();
			}
		});
	}
	
}
