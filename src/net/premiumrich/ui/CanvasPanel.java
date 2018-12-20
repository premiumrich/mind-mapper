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
	private ShapesController shapeCon;
	
	public CanvasPanel() {
		viewport = new Viewport(this);
		shapeCon = new ShapesController(this, viewport);
		contextMenu = new ContextMenu(this);

		this.setBackground(Color.white);
		
		MindMapListener mapListener = new MindMapListener(this, viewport);
		this.addMouseListener(mapListener);
		this.addMouseMotionListener(mapListener);
		this.addMouseWheelListener(mapListener);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		viewport.drawAll(g);		// Invoke drawing
	}
	
	// Handle displaying context menu
	public void popup(MouseEvent e) {
		if (shapeCon.getShapesUnderCursor(e.getPoint()).size() > 0) {
			contextMenu.updateEditMenuValues(shapeCon.selectedShape);
			contextMenu.getEditMenu().setEnabled(true);
		}
		else
			contextMenu.getEditMenu().setEnabled(false);
		contextMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	
	// Getters
	public Viewport getViewport() {
		return viewport;
	}
	public ShapesController getShapesController() {
		return shapeCon;
	}
	public ContextMenu getContextMenu() {
		return contextMenu;
	}
	
}
