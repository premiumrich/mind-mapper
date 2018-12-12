package net.premiumrich.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.premiumrich.shapes.*;

public class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	
	private static final long serialVersionUID = 0;
	
	private ContextMenu contextMenu;
	public boolean isContextTrigger = false;
	public MouseEvent contextTriggerEvent;
	
	private Viewport viewport;
	private ShapesController shapeCon;
	
	public CanvasPanel() {
		viewport = new Viewport(this);
		shapeCon = new ShapesController(this, viewport);
		contextMenu = new ContextMenu(this);

		this.setBackground(Color.white);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		viewport.drawAll(g);		// Invoke drawing
	}
	
	
	// Mouse activity listeners
	public void mousePressed(MouseEvent e) {
		popup(e);
		if (!contextMenu.isVisible()) isContextTrigger = false;
		viewport.panStartPoint = e.getLocationOnScreen();
		viewport.panning = true;
		viewport.released = false;
		
		// Select the shape that is clicked on
		if (shapeCon.getShapesUnderCursor(e.getPoint()).size() > 0) {
			shapeCon.dragStartPoint = e.getLocationOnScreen();			// Update drag diff reference
			if (shapeCon.shapeSelectionIndex > shapeCon.getShapesUnderCursor(e.getPoint()).size() - 1)
				shapeCon.shapeSelectionIndex = 0;		// Prevent index overflow by restarting cycle
			shapeCon.prevSelectedShape = shapeCon.selectedShape;
			if (shapeCon.prevSelectedShape != null) shapeCon.prevSelectedShape.isHighlighted = false;
			shapeCon.setSelectedShape(shapeCon.getShapesUnderCursor(e.getPoint()).get(shapeCon.shapeSelectionIndex));
			shapeCon.shapeSelectionIndex++;				// Increment index to select overlapped shapes
		} else {	// Remove the selection
			shapeCon.shapeSelectionIndex = 0;
			shapeCon.setSelectedShape(null);
		}
	}
	public void mouseReleased(MouseEvent e) {
		popup(e);
		viewport.released = true;
		repaint();		// Bypass FPS limiter and force repaint to lock in position
	}
	public void mouseDragged(MouseEvent e) {
		if (shapeCon.getSelectedShape() == null && !isContextTrigger) {		// Pan the canvas
			viewport.panning = true;
			
			Point curPoint = e.getLocationOnScreen();
			viewport.panXDiff = curPoint.x - viewport.panStartPoint.x;
			viewport.panYDiff = curPoint.y - viewport.panStartPoint.y;
			
			viewport.handleRepaint();
		} else {						// Drag the selected shape
			Point curPoint = e.getLocationOnScreen();
			if (curPoint.x  != shapeCon.dragStartPoint.x || curPoint.y != shapeCon.dragStartPoint.y) {
				shapeCon.selectedShape.setNewCoordinates(
							shapeCon.selectedShape.getX() + (curPoint.x - shapeCon.dragStartPoint.x), 
							shapeCon.selectedShape.getY() + (curPoint.y - shapeCon.dragStartPoint.y));
				shapeCon.dragStartPoint = e.getLocationOnScreen();			// Update drag diff reference
			}
			viewport.handleRepaint();
		}
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!isContextTrigger) {
			viewport.zooming = true;
			if (e.getWheelRotation() < 0) {		// Zoom in
				viewport.zoomFactor *= 1.1;
			}
			if (e.getWheelRotation() > 0) {		// Zoom out
				viewport.zoomFactor /= 1.1;
			}
			viewport.handleRepaint();
		}
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	
	// Handle displaying context menu
	private void popup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			isContextTrigger = true;
			contextTriggerEvent = e;
			if (shapeCon.getShapesUnderCursor(e.getPoint()).size() > 0) {
				contextMenu.updateEditMenuValues(shapeCon.selectedShape);
				contextMenu.getEditMenu().setEnabled(true);
			}
			else
				contextMenu.getEditMenu().setEnabled(false);
			contextMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	
	// Getters
	public Viewport getViewport() {
		return viewport;
	}
	public ShapesController getShapesController() {
		return shapeCon;
	}
	
}
