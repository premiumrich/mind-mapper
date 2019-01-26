package net.premiumrich.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JTextField;

import net.premiumrich.shapes.MapController;

/**
 * The MindMapListener handles clicking, dragging and scrolling in the CanvasPanel
 * @author premiumrich
 */
public class MindMapListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	private CanvasPanel canvasPanel;
	private Viewport viewport;
	private MapController mapCon;
	
	public MindMapListener(CanvasPanel canvasPanel, Viewport viewport) {
		this.canvasPanel = canvasPanel;
		this.viewport = viewport;
		this.mapCon = canvasPanel.getMapController();
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
		if (mapCon.getSelectedShape() == null && !canvasPanel.isContextTrigger) {			// Pan the canvas
			viewport.pan(evt.getLocationOnScreen());
		} else if (mapCon.getSelectedShape() != null && !canvasPanel.isContextTrigger) {	// Drag the selected shape
			Point curPoint = evt.getLocationOnScreen();
			if (curPoint.x  != mapCon.dragStartPoint.x || curPoint.y != mapCon.dragStartPoint.y) {
				mapCon.getSelectedShape().setNewCoordinates(				// Update coordinates
						mapCon.getSelectedShape().getX() + (int)((curPoint.x - mapCon.dragStartPoint.x)/viewport.zoomFactor), 
						mapCon.getSelectedShape().getY() + (int)((curPoint.y - mapCon.dragStartPoint.y)/viewport.zoomFactor));
				mapCon.dragStartPoint = evt.getLocationOnScreen();			// Update drag diff reference
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
		if (evt.getClickCount() == 2 && mapCon.getSelectedShape() != null) {
			canvasPanel.add(mapCon.getSelectedShape().getTextField());
			mapCon.setEditingShape(mapCon.getSelectedShape());
			mapCon.getSelectedShape().getTextField().requestFocusInWindow();
			mapCon.getSelectedShape().getTextField().selectAll();
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
		if (mapCon.getShapesUnderCursor(evt.getPoint()).size() > 0) {
			mapCon.dragStartPoint = evt.getLocationOnScreen();			// Update drag diff reference
			if (mapCon.shapeSelectionIndex > mapCon.getShapesUnderCursor(evt.getPoint()).size() - 1)
				mapCon.shapeSelectionIndex = 0;		// Prevent index overflow by restarting cycle
			mapCon.setSelectedShape(mapCon.getShapesUnderCursor(evt.getPoint()).get(mapCon.shapeSelectionIndex));
			mapCon.shapeSelectionIndex++;				// Increment index to select overlapped shapes
		} else {	// Remove the selection
			for (Component com : canvasPanel.getComponents())
				if (com instanceof JTextField) canvasPanel.remove(com);		// Remove all text fields
			mapCon.setEditingShape(null);
			mapCon.setSelectedShape(null);
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
