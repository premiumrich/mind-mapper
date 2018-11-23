package net.premiumrich.ui;

import java.awt.Color;

import javax.swing.*;

public class PickerPanel extends JPanel {

	private static final long serialVersionUID = 0;
	
	private JLabel tempPickerLbl;
	
	public PickerPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBackground(Color.gray);
		
		initComponents();
	}
	
	private void initComponents() {
		tempPickerLbl = new JLabel("Picker");
		this.add(tempPickerLbl);
	}
	
}
