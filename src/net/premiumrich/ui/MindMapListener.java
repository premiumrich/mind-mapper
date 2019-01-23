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
		viewport.setMouseReleased(false);
		
		selectShapeUnderCursor(evt);
		triggerContext(evt);
	}
	public void mouseReleased(MouseEvent evt) {
		viewport.setMouseReleased(true);
		canvasPanel.repaint();		// Bypass FPS limiter and force repaint to lock in position

		selectShapeUnderCursor(evt);
		triggerContext(evt);
	}
	public void mouseDragged(MouseEvent evt) {
		if (shapeCon.getSelectedShape() == null && !canvasPanel.isContextTrigger) {		// Pan the canvas
			viewport.pan(evt.getLocationOnScreen());
		} else if (shapeCon.getSelectedShape() != null) {								// Drag the selected shape
			Point curPoint = evt.getLocationOnScreen();
			if (curPoint.x  != shapeCon.dragStartPoint.x || curPoint.y != shapeCon.dragStartPoint.y) {
				shapeCon.getSelectedShape().setNewCoordinates(
						shapeCon.getSelectedShape().getX() + (int)((curPoint.x - shapeCon.dragStartPoint.x)/viewport.zoomFactor), 
						shapeCon.getSelectedShape().getY() + (int)((curPoint.y - shapeCon.dragStartPoint.y)/viewport.zoomFactor));
				shapeCon.dragStartPoint = evt.getLocationOnScreen();			// Update drag diff reference
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
		}
	}
	public void mouseClicked(MouseEvent evt) {
		// Handle double-click
		if (evt.getClickCount() == 2 && shapeCon.getSelectedShape() != null) {
			canvasPanel.add(shapeCon.getSelectedShape().getTextField());
			shapeCon.setEditingShape(shapeCon.getSelectedShape());
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
	
	private void selectShapeUnderCursor(MouseEvent evt) {
		// Select the shape that is clicked on
		if (shapeCon.getShapesUnderCursor(evt.getPoint()).size() > 0) {
			shapeCon.dragStartPoint = evt.getLocationOnScreen();			// Update drag diff reference
			if (shapeCon.shapeSelectionIndex > shapeCon.getShapesUnderCursor(evt.getPoint()).size() - 1)
				shapeCon.shapeSelectionIndex = 0;		// Prevent index overflow by restarting cycle
			shapeCon.setSelectedShape(shapeCon.getShapesUnderCursor(evt.getPoint()).get(shapeCon.shapeSelectionIndex));
			shapeCon.shapeSelectionIndex++;				// Increment index to select overlapped shapes
		} else {	// Remove the selection
			for (Component com : canvasPanel.getComponents())
				if (com instanceof JTextField) canvasPanel.remove(com);		// Remove all text fields
			shapeCon.setEditingShape(null);
			shapeCon.setSelectedShape(null);
		}
	}

	private void triggerContext(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			canvasPanel.isContextTrigger = true;
			canvasPanel.contextTriggerEvent = evt;
			canvasPanel.popup(evt);
		} else {
			canvasPanel.isContextTrigger = false;
		}
	}
}
