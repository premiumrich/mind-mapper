package net.premiumrich.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

public class PickerPanel extends JPanel {

	private static final long serialVersionUID = 0;
	
	static JLabel fpsLbl;
	static JLabel mouseXLbl;
	static JLabel mouseYLbl;
	static JLabel zoomLbl;
	static JLabel dDragXLbl;
	static JLabel dDragYLbl;
	static JLabel tempXLbl;
	static JLabel tempYLbl;
	
	public PickerPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setPreferredSize(new Dimension(100, 0));
		this.setBackground(Color.gray);
		
		initComponents();
	}
	
	private void initComponents() {
		fpsLbl = new JLabel();
		this.add(fpsLbl);
		
		mouseXLbl = new JLabel();
		this.add(mouseXLbl);
		mouseYLbl = new JLabel();
		this.add(mouseYLbl);
		
		zoomLbl = new JLabel();
		this.add(zoomLbl);
		
		dDragXLbl = new JLabel();
		this.add(dDragXLbl);
		dDragYLbl = new JLabel();
		this.add(dDragYLbl);
		
		tempXLbl = new JLabel();
		this.add(tempXLbl);
		tempYLbl = new JLabel();
		this.add(tempYLbl);
	}
	
}
