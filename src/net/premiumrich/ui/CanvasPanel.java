package net.premiumrich.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.premiumrich.main.AppFrame;
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
	private MapController mapCon;
	
	private AppFrame appFrame;

	public CanvasPanel(AppFrame appFrame) {
		this.appFrame = appFrame;
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
		if (mapCon.getShapesUnderCursor(e.getPoint()).size() > 0) {
			contextMenu.updateMenuValues(mapCon.getSelectedShape());
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
		mapCon = new MapController(this, viewport);
		MapListener mapListener = new MapListener(this, viewport);
		this.addMouseListener(mapListener);
		this.addMouseMotionListener(mapListener);
		this.addMouseWheelListener(mapListener);
		this.setBackground(Color.white);
		appFrame.setAppTitle(null);
		repaint();
	}
	
	// Getters
	public Viewport getViewport() {
		return viewport;
	}
	public MapController getMapController() {
		return mapCon;
	}
	public ContextMenu getContextMenu() {
		return contextMenu;
	}
	
}
