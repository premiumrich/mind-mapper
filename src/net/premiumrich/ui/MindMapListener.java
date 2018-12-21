package net.premiumrich.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JTextField;

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
	public void mousePressed(MouseEvent evt) {
		viewport.panStartPoint = evt.getLocationOnScreen();
		triggerContext(evt);
		if (!canvasPanel.getContextMenu().isVisible()) canvasPanel.isContextTrigger = false;
		viewport.released = false;
		
		// Select the shape that is clicked on
		if (shapeCon.getShapesUnderCursor(evt.getPoint()).size() > 0) {
			shapeCon.dragStartPoint = evt.getLocationOnScreen();			// Update drag diff reference
			if (shapeCon.shapeSelectionIndex > shapeCon.getShapesUnderCursor(evt.getPoint()).size() - 1)
				shapeCon.shapeSelectionIndex = 0;		// Prevent index overflow by restarting cycle
			shapeCon.prevSelectedShape = shapeCon.selectedShape;
			if (shapeCon.prevSelectedShape != null) shapeCon.prevSelectedShape.isHighlighted = false;
			shapeCon.setSelectedShape(shapeCon.getShapesUnderCursor(evt.getPoint()).get(shapeCon.shapeSelectionIndex));
			shapeCon.shapeSelectionIndex++;				// Increment index to select overlapped shapes
		} else {	// Remove the selection
			for (Component con : canvasPanel.getComponents()) {
				if (con instanceof JTextField) canvasPanel.remove(con);		// Remove all text fields
			}
			shapeCon.setSelectedShape(null);
		}
	}
	public void mouseReleased(MouseEvent evt) {
		triggerContext(evt);
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
	public void mouseWheelMoved(MouseWheelEvent evt) {
		if (!canvasPanel.isContextTrigger) {
			if (evt.getWheelRotation() < 0) {				// Mouse wheel rolls forward
				viewport.zoomIn();
			} else if (evt.getWheelRotation() > 0) {		// Mouse wheel rolls backwards
				viewport.zoomOut();
			}
			viewport.zooming = true;
			viewport.handleRepaint();
		}
	}
	public void mouseClicked(MouseEvent evt) {
		// Handle double-click
		if (evt.getClickCount() == 2 && shapeCon.getSelectedShape() != null) {
			canvasPanel.add(shapeCon.getSelectedShape().getTextField());
			shapeCon.getSelectedShape().getTextField().requestFocusInWindow();
			shapeCon.getSelectedShape().getTextField().selectAll();
		}
	}
	public void mouseMoved(MouseEvent evt) {
	}
	public void mouseEntered(MouseEvent evt) {
	}
	public void mouseExited(MouseEvent evt) {
	}
	
	// Popup context menu on right click
	private void triggerContext(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			canvasPanel.isContextTrigger = true;
			canvasPanel.contextTriggerEvent = evt;
			canvasPanel.popup(evt);
		}
	}
	
}
