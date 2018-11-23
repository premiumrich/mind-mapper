package net.premiumrich.ui;

import javax.swing.*;

public class CanvasPanel extends JPanel {
	
	private static final long serialVersionUID = 0;
	
	private JLabel tempCanvasLbl;
	
	public CanvasPanel() {
		initComponents();
	}
	
	private void initComponents() {
		tempCanvasLbl = new JLabel("Canvas");
		this.add(tempCanvasLbl);
	}
	
}
