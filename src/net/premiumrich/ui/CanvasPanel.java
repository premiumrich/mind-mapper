package net.premiumrich.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.premiumrich.shapes.*;

/**
 * The CanvasPanel controls all mind map elements and listeners
 * @author premiumrich
 */
public class CanvasPanel extends JPanel {
	
	private static final long serialVersionUID = 0;
	
	private ContextMenu contextMenu;
	public boolean isContextTrigger = false;
	public MouseEvent contextTriggerEvent;
	
	private Viewport viewport;
	private ShapesController shapesCon;
	
	public CanvasPanel() {
		contextMenu = new ContextMenu(this);
		reset();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		viewport.drawAll(g);		// Draw all shapes, text, and lines
	}
	
	// Handle displaying context menu
	public void popup(MouseEvent e) {
		if (shapesCon.getShapesUnderCursor(e.getPoint()).size() > 0) {
			contextMenu.updateMenuValues(shapesCon.getSelectedShape());
			contextMenu.getEditMenu().setEnabled(true);
			contextMenu.getOrderMenu().setEnabled(true);
			contextMenu.getConnectionsMenu().setEnabled(true);
		} else {
			contextMenu.getEditMenu().setEnabled(false);
			contextMenu.getOrderMenu().setEnabled(false);
			contextMenu.getConnectionsMenu().setEnabled(false);
		}
		contextMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	public void reset() {
		viewport = new Viewport(this);
		shapesCon = new ShapesController(this, viewport);
		MindMapListener mapListener = new MindMapListener(this, viewport);
		this.addMouseListener(mapListener);
		this.addMouseMotionListener(mapListener);
		this.addMouseWheelListener(mapListener);
		this.setBackground(Color.white);
		repaint();
	}
	
	// Getters
	public Viewport getViewport() {
		return viewport;
	}
	public ShapesController getShapesController() {
		return shapesCon;
	}
	public ContextMenu getContextMenu() {
		return contextMenu;
	}
	
}
