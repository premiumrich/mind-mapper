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
	private ShapesController shapesController;
	
	public CanvasPanel() {
		viewport = new Viewport(this);
		shapesController = new ShapesController(this, viewport);
		contextMenu = new ContextMenu();
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
		viewport.startPoint = MouseInfo.getPointerInfo().getLocation();
		viewport.panning = true;
		viewport.released = false;
	}
	public void mouseReleased(MouseEvent e) {
		popup(e);
		viewport.released = true;
		repaint();		// Bypass FPS limiter and force repaint to lock in position
	}
	public void mouseMoved(MouseEvent e) {
		Point offsetCursor = e.getPoint();	// Offset cursor to be consistent with shape location
		offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
		
		boolean found = false;
		for (MapShape shape : shapesController.getShapes()) {
			if (shape.getShape().getBounds().contains(offsetCursor)) {
				shape.isHighlighted = true;
				found = true;
				shapesController.setHighlightedShape(shape);
			} else {
				shape.isHighlighted = false;
			}
		}
		if (!found) shapesController.setHighlightedShape(null);
		
		viewport.handleRepaint();
	}
	public void mouseDragged(MouseEvent e) {
		if (shapesController.getHighlightedShape() == null && !isContextTrigger) {
			viewport.panning = true;
			
			Point curPoint = e.getLocationOnScreen();
			viewport.xDiff = curPoint.x - viewport.startPoint.x;
			viewport.yDiff = curPoint.y - viewport.startPoint.y;
			
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
			Point offsetCursor = e.getPoint();	// Offset cursor to be consistent with shape location
			offsetCursor.translate(-(int)viewport.xOffset, -(int)viewport.yOffset);
			if (shapesController.isShapeUnderCursor(offsetCursor))
				contextMenu.getEditMenu().setVisible(true);
			else
				contextMenu.getEditMenu().setVisible(false);
			contextMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	public Viewport getViewport() {
		return viewport;
	}
	public ShapesController getShapesController() {
		return shapesController;
	}
	
}
