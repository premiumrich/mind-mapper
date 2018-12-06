package net.premiumrich.main;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import net.premiumrich.ui.*;

public class AppFrame extends JFrame {

	private static final long serialVersionUID = 0;
	
	private static MainMenubar menubar;

	private static PickerPanel pickerPanel;
	private static CanvasPanel canvasPanel;
	
	public AppFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Mind Mapper");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(50, 50, screenSize.width - 100, screenSize.height - 100);
		this.setLayout(new BorderLayout());
		
		initComponents();
	}
	
	private void initComponents() {
		pickerPanel = new PickerPanel();
		this.add(pickerPanel, BorderLayout.WEST);
		
		canvasPanel = new CanvasPanel();
		this.add(canvasPanel, BorderLayout.CENTER);
		
		menubar = new MainMenubar();
		this.add(menubar, BorderLayout.NORTH);
	}

}
