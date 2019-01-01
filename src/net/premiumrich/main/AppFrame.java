package net.premiumrich.main;

import java.awt.*;

import javax.swing.*;

import net.premiumrich.io.IOController;
import net.premiumrich.ui.*;

/**
 * The AppFrame constructs and manages the main frame
 * @author premiumrich
 */
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
		
		menubar = new Menubar(this);
		this.setJMenuBar(menubar);
	}
	
	public void setOpenedFileName(String fileName) {
		this.setTitle(fileName + " - " + appName);
	}
	
	// Getters
	public IOController getIOCon() {
		return ioCon;
	}
	public CanvasPanel getCanvasPanel() {
		return canvasPanel;
	}
	public PickerPanel getPickerPanel() {
		return pickerPanel;
	}

}
