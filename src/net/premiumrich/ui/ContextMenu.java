package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import java.awt.Color;

public class ContextMenu extends JPopupMenu {

	private static final long serialVersionUID = 0;
	
	private JMenu addMenu;
	private JMenuItem addEllipseMenuItem;
	private JMenuItem addRectangleMenuItem;
	
	private JMenu editMenu;
	private JMenuItem removeElementMenuItem;
	
	private JMenu changeBorderColourMenu;
	
	private JMenu changeFontMenu;
	private JMenuItem selectFontSerif;
	private JMenuItem selectFontSansSerif;
	private JMenuItem selectFontMonospaced;
	
	private JMenu changeFontSizeMenu;
	private final int fontSizes[] = {10, 11, 12, 14, 16, 18, 24};
	
	private JMenu changeFontColourMenu;
	
	// Limitation of using ActionCommands to communicate across classes:
	// only Strings can be used. Thus, a "colour-name lookup table" must be used
	public static final HashMap<String,Color> colours;
	static {
		colours = new HashMap<String,Color>();
		colours.put("Black", Color.black);
		colours.put("Red", Color.red);
		colours.put("Green", Color.green);
		colours.put("Blue", Color.blue);
		colours.put("Yellow", Color.yellow);
		colours.put("Orange", Color.orange);
		colours.put("Magenta", Color.magenta);
		colours.put("Pink", Color.pink);
	}
	
	public ContextMenu() {
		initAddMenu();
		initEditMenu();
	}
	
	private void initAddMenu() {
		addMenu = new JMenu("Add ...");
		this.add(addMenu);
		
		addEllipseMenuItem = new JMenuItem("Ellipse shape");
		addEllipseMenuItem.setActionCommand("Ellipse");
		addEllipseMenuItem.addActionListener(new AddShapeListener());
		addMenu.add(addEllipseMenuItem);
		
		addRectangleMenuItem = new JMenuItem("Rectangle shape");
		addRectangleMenuItem.setActionCommand("Rectangle");
		addRectangleMenuItem.addActionListener(new AddShapeListener());
		addMenu.add(addRectangleMenuItem);
	}
	
	private void initEditMenu() {
		editMenu = new JMenu("Edit");
		this.add(editMenu);
		editMenu.setVisible(false);
		
		removeElementMenuItem = new JMenuItem("Remove this");
		editMenu.add(removeElementMenuItem);
		removeElementMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CanvasPanel.canvasInstance.removeShapeUnderCursor();
			}
		});
		
		changeBorderColourMenu = new JMenu("Border colour");
		editMenu.add(changeBorderColourMenu);
		for (String colourName : colours.keySet()) {
			JMenuItem selectBorderColour = new JMenuItem(colourName);
			selectBorderColour.setActionCommand(colourName);
			selectBorderColour.addActionListener(new ChangeBorderColourListener());
			changeBorderColourMenu.add(selectBorderColour);
		}
		
		changeFontMenu = new JMenu("Font");
		editMenu.add(changeFontMenu);
		
		selectFontSerif = new JMenuItem("Times New Roman");
		selectFontSerif.setActionCommand("Serif");
		selectFontSerif.addActionListener(new ChangeFontListener());
		changeFontMenu.add(selectFontSerif);
		
		selectFontSansSerif = new JMenuItem("Helvetica");
		selectFontSansSerif.setActionCommand("SansSerif");
		selectFontSansSerif.addActionListener(new ChangeFontListener());
		changeFontMenu.add(selectFontSansSerif);
		
		selectFontMonospaced = new JMenuItem("Courier");
		selectFontMonospaced.setActionCommand("Monospaced");
		selectFontMonospaced.addActionListener(new ChangeFontListener());
		changeFontMenu.add(selectFontMonospaced);
		
		changeFontSizeMenu = new JMenu("Font size");
		editMenu.add(changeFontSizeMenu);
		for (int i : fontSizes) {
			JMenuItem selectFontSize = new JMenuItem(Integer.toString(i));
			selectFontSize.setActionCommand(Integer.toString(i));
			selectFontSize.addActionListener(new ChangeFontSizeListener());
			changeFontSizeMenu.add(selectFontSize);
		}
		
		changeFontColourMenu = new JMenu("Font colour");
		editMenu.add(changeFontColourMenu);
		for (String colourName : colours.keySet()) {
			JMenuItem selectFontColour = new JMenuItem(colourName);
			selectFontColour.setActionCommand(colourName);
			selectFontColour.addActionListener(new ChangeFontColourListener());
			changeFontColourMenu.add(selectFontColour);
		}
	}
	
	public JMenu getAddMenu() {
		return addMenu;
	}
	
	public JMenu getEditMenu() {
		return editMenu;
	}
	
	class AddShapeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CanvasPanel.canvasInstance.addShape(e);
			CanvasPanel.canvasInstance.isContextTrigger = false;
		}
	}
	
	class ChangeBorderColourListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CanvasPanel.canvasInstance.changeBorderColour(e);
			CanvasPanel.canvasInstance.isContextTrigger = false;
		}
	}
	
	class ChangeFontListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CanvasPanel.canvasInstance.changeFont(e);
			CanvasPanel.canvasInstance.isContextTrigger = false;
		}
	}
	
	class ChangeFontSizeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CanvasPanel.canvasInstance.changeFontSize(e);
			CanvasPanel.canvasInstance.isContextTrigger = false;
		}
	}
	
	class ChangeFontColourListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CanvasPanel.canvasInstance.changeFontColour(e);
			CanvasPanel.canvasInstance.isContextTrigger = false;
		}
	}
	
}
