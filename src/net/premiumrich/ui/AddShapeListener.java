package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddShapeListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) {
		CanvasPanel.canvasInstance.addShape(e);
	}
	
}
