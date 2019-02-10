package gvf121.main;

import java.awt.*;

import javax.swing.*;

import gvf121.io.IOController;
import gvf121.ui.*;

/**
 * The AppFrame constructs and manages the main frame
 * @author premiumrich
 */
public class AppFrame extends JFrame {

	private static final long serialVersionUID = 0;
	
	private static Menubar menubar;
	private static PickerPanel pickerPanel;
	private static CanvasPanel canvasPanel;
	private static DebugPanel debugPanel;
	private static IOController ioCon;
	
	public static final String appName = "Mind Mapper";
	
	public AppFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAppTitle(null);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(50, 50, screenSize.width - 100, screenSize.height - 100);
		this.setLayout(new BorderLayout());
		
		initComponents();
	}
	
	private void initComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("OptionPane.background", Color.white);
			UIManager.put("Panel.background", Color.white);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		canvasPanel = new CanvasPanel(this);
		this.add(canvasPanel, BorderLayout.CENTER);
		
		pickerPanel = new PickerPanel(canvasPanel);
		this.add(pickerPanel, BorderLayout.WEST);
		
		debugPanel = new DebugPanel();
		this.add(debugPanel, BorderLayout.EAST);

		ioCon = new IOController(this, canvasPanel);
		
		menubar = new Menubar(this);
		this.setJMenuBar(menubar);
	}
	
	public void setAppTitle(String fileName) {
		if (fileName == null) this.setTitle(appName);
		else this.setTitle(fileName + " - " + appName);
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
	public DebugPanel getDebugPanel() {
		return debugPanel;
	}

}
