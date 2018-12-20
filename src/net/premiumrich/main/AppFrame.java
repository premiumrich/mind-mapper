package net.premiumrich.main;

import java.awt.*;

import javax.swing.*;

import net.premiumrich.io.IOController;
import net.premiumrich.ui.*;

public class AppFrame extends JFrame {

	private static final long serialVersionUID = 0;
	
	private static Menubar menubar;
	private static PickerPanel pickerPanel;
	private static CanvasPanel canvasPanel;
	private static IOController ioCon;
	
	public static final String appName = "Mind Mapper";
	
	public AppFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Untitled - Mind Mapper");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(50, 50, screenSize.width - 100, screenSize.height - 100);
		this.setLayout(new BorderLayout());
		
		initComponents();
	}
	
	private void initComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pickerPanel = new PickerPanel();
		this.add(pickerPanel, BorderLayout.WEST);
		
		canvasPanel = new CanvasPanel();
		this.add(canvasPanel, BorderLayout.CENTER);
		
		ioCon = new IOController(this, canvasPanel);
		
		menubar = new Menubar(canvasPanel, ioCon);
		this.setJMenuBar(menubar);
	}
	
	public void setOpenedFileName(String fileName) {
		this.setTitle(fileName + " - " + appName);
	}
	
	public CanvasPanel getCanvasPanel() {
		return canvasPanel;
	}

}
