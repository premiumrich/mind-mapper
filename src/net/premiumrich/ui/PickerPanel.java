package net.premiumrich.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import net.premiumrich.shapes.*;

public class PickerPanel extends JPanel {

	private static final long serialVersionUID = 0;

	private static final int MAX_FPS = 60;
	private long lastFrameTime = 0;
	
	private static JLabel title;

	private static List<MapShape> shapes;

	public PickerPanel(CanvasPanel canvasPanel) {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setPreferredSize(new Dimension(200, 0));
		this.setBackground(Color.LIGHT_GRAY);
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		
		title = new JLabel("Add");
		title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		title.setBorder(new EmptyBorder(25, 0, 0, 0));
		title.setFont(new Font("Arial", Font.BOLD, 20));
		this.add(title);

		initShapes();

		// Mouse activity listeners
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
			}
			public void mouseReleased(MouseEvent evt) {
			}
			public void mouseClicked(MouseEvent evt) {
				// Handle double-click
				if (evt.getClickCount() == 2 && getShapeUnderCursor(evt) != null) {
					canvasPanel.getMapController().addShape(getShapeUnderCursor(evt).getClass().getName());
				}
			}
		});
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				// if (shapeCon.getSelectedShape() != null) {								// Drag the selected shape
				// 	Point curPoint = evt.getLocationOnScreen();
				// 	if (curPoint.x  != shapeCon.dragStartPoint.x || curPoint.y != shapeCon.dragStartPoint.y) {
				// 		shapeCon.getSelectedShape().setNewCoordinates(
				// 				shapeCon.getSelectedShape().getX() + (int)((curPoint.x - shapeCon.dragStartPoint.x)/viewport.zoomFactor), 
				// 				shapeCon.getSelectedShape().getY() + (int)((curPoint.y - shapeCon.dragStartPoint.y)/viewport.zoomFactor));
				// 		shapeCon.dragStartPoint = evt.getLocationOnScreen();			// Update drag diff reference
				// 	}
				// 	viewport.handleRepaint();
				// }
			}
			public void mouseMoved(MouseEvent evt) {
				for (MapShape shape : shapes) {
					if (shape.getShape().getBounds().contains(evt.getPoint())) shape.isHighlighted = true;
					else shape.isHighlighted = false;
				}
				handleRepaint();
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (MapShape mapShape : shapes) {
			// Fill shape background with white
			g2d.setColor(Color.white);
			g2d.fill(mapShape.getShape());
			// Draw border around shape
			if (mapShape.isHighlighted) g2d.setColor(Color.cyan);
			else g2d.setColor(mapShape.getBorderColour());
			g2d.setStroke(mapShape.getBorderStroke());
			g2d.draw(mapShape.getShape());
		}
	}

	private void handleRepaint() {		// A handler to limit framerate and CPU usage
		// Calculate frame time and only repaint at the specified framerate
		if (System.currentTimeMillis() - lastFrameTime >= (1000/MAX_FPS)) {
			repaint();
			lastFrameTime = System.currentTimeMillis();
		}
	}

	private void initShapes() {
		shapes = new ArrayList<MapShape>();
		
		shapes.add(new RectangleShape(40, 100, 120, 60));
		shapes.add(new EllipseShape(40, 200, 120, 60));
		shapes.add(new TriangleShape(40, 300, 120, 60));
	}

	private MapShape getShapeUnderCursor(MouseEvent evt) {
		for (MapShape shape : shapes)
			if (shape.getShape().getBounds().contains(evt.getPoint())) return shape;
		return null;
	}
}
