package net.premiumrich.ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import net.premiumrich.shapes.ShapesController;

/**
 * The MindMapListener handles clicking, dragging and scrolling in the CanvasPanel
 * @author premiumrich
 */
public class MindMapListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	private CanvasPanel canvasPanel;
	private Viewport viewport;
	private ShapesController shapeCon;
	
	public MindMapListener(CanvasPanel canvasPanel, Viewport viewport) {
		this.canvasPanel = canvasPanel;
		this.viewport = viewport;
		this.shapeCon = canvasPanel.getShapesController();
	}
	
	// Mouse activity listeners
	public void mousePressed(MouseEvent e) {
		viewport.panStartPoint = e.getLocationOnScreen();
		triggerContext(e);
		if (!canvasPanel.getContextMenu().isVisible()) canvasPanel.isContextTrigger = false;
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
		triggerContext(e);
		viewport.released = true;
		canvasPanel.repaint();		// Bypass FPS limiter and force repaint to lock in position
	}
	public void mouseDragged(MouseEvent e) {
		if (shapeCon.getSelectedShape() == null && !canvasPanel.isContextTrigger) {		// Pan the canvas
			viewport.pan(e.getLocationOnScreen());
		} else {															// Drag the selected shape
			Point curPoint = e.getLocationOnScreen();
			if (curPoint.x  != shapeCon.dragStartPoint.x || curPoint.y != shapeCon.dragStartPoint.y) {
				shapeCon.selectedShape.setNewCoordinates(
						shapeCon.selectedShape.getX() + (int)((curPoint.x - shapeCon.dragStartPoint.x)/viewport.zoomFactor), 
						shapeCon.selectedShape.getY() + (int)((curPoint.y - shapeCon.dragStartPoint.y)/viewport.zoomFactor));
				shapeCon.dragStartPoint = e.getLocationOnScreen();			// Update drag diff reference
			}
			viewport.handleRepaint();
		}
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!canvasPanel.isContextTrigger) {
			if (e.getWheelRotation() < 0) {				// Mouse wheel rolls forward
				viewport.zoomIn();
			} else if (e.getWheelRotation() > 0) {		// Mouse wheel rolls backwards
				viewport.zoomOut();
			}
			viewport.zooming = true;
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
	
	// Popup context menu on right click
	private void triggerContext(MouseEvent e) {
		if (e.isPopupTrigger()) {
			canvasPanel.isContextTrigger = true;
			canvasPanel.contextTriggerEvent = e;
			canvasPanel.popup(e);
		}
	}
	
}
