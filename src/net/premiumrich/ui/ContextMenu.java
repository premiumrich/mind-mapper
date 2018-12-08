package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.premiumrich.main.AppFrame;

import java.awt.Color;

public class ContextMenu extends JPopupMenu {

	private static final long serialVersionUID = 0;
	
	private JMenu addMenu;
	private JMenuItem addEllipseMenuItem;
	private JMenuItem addRectangleMenuItem;
	
	private JMenu editMenu;
	private JMenuItem removeElementMenuItem;
	
	private JMenu changeBorderColourMenu;
	private JMenu changeFontColourMenu;
	// The limitation of using ActionCommands to communicate actions across classes is that
	// only Strings can be used. Thus, a lookup table must be used to discern the action.
	public static final HashMap<String,Color> colours = new HashMap<String,Color>();
	static {
		colours.put("Black", Color.black);
		colours.put("Red", Color.red);
		colours.put("Green", Color.green);
		colours.put("Blue", Color.blue);
		colours.put("Yellow", Color.yellow);
		colours.put("Orange", Color.orange);
		colours.put("Magenta", Color.magenta);
		colours.put("Pink", Color.pink);
	}
	
	private JMenu changeFontMenu;
	public static final HashMap<String,String> fonts = new HashMap<String,String>();
	static {
		fonts.put("Times New Roman", "Serif");
		fonts.put("Helvetica", "SansSerif");
		fonts.put("Courier", "Monospaced");
	}
	
	private JMenu changeFontSizeMenu;
	private static final int fontSizes[] = {10, 11, 12, 14, 16, 18, 24};
	
	public ContextMenu() {
		initAddMenu();
		initEditMenu();
	}
	
	private void initAddMenu() {
		addMenu = new JMenu("Add ...");
		this.add(addMenu);
		
		class AddShapeListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				AppFrame.canvasPanel.getShapesController().addShape(e);
				AppFrame.canvasPanel.isContextTrigger = false;
			}
		}
		
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
				AppFrame.canvasPanel.getShapesController().removeShapeUnderCursor();
			}
		});
		
		changeBorderColourMenu = new JMenu("Border colour");
		editMenu.add(changeBorderColourMenu);
		// Iterate through available colours and create a new menu item for each
		for (String colourName : colours.keySet()) {
			JMenuItem selectBorderColour = new JMenuItem(colourName);
			selectBorderColour.setActionCommand(colourName);
			selectBorderColour.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AppFrame.canvasPanel.getShapesController().changeBorderColour(e);
					AppFrame.canvasPanel.isContextTrigger = false;
				}
			});
			changeBorderColourMenu.add(selectBorderColour);
		}
		
		changeFontMenu = new JMenu("Font");
		editMenu.add(changeFontMenu);
		// Iterate through available fonts and create a new menu item for each
		for (String fontName : fonts.keySet()) {
			JMenuItem selectFont = new JMenuItem(fontName);
			selectFont.setActionCommand(fonts.get(fontName));
			selectFont.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AppFrame.canvasPanel.getShapesController().changeFont(e);
					AppFrame.canvasPanel.isContextTrigger = false;
				}
			});
			changeFontMenu.add(selectFont);
		}
		
		changeFontSizeMenu = new JMenu("Font size");
		editMenu.add(changeFontSizeMenu);
		// Iterate through available font sizes and create a new menu item for each
		for (int i : fontSizes) {
			JMenuItem selectFontSize = new JMenuItem(Integer.toString(i));
			selectFontSize.setActionCommand(Integer.toString(i));
			selectFontSize.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AppFrame.canvasPanel.getShapesController().changeFontSize(e);
					AppFrame.canvasPanel.isContextTrigger = false;
				}
			});
			changeFontSizeMenu.add(selectFontSize);
		}
		
		changeFontColourMenu = new JMenu("Font colour");
		editMenu.add(changeFontColourMenu);
		// Iterate through available colours and create a new menu item for each
		for (String colourName : colours.keySet()) {
			JMenuItem selectFontColour = new JMenuItem(colourName);
			selectFontColour.setActionCommand(colourName);
			selectFontColour.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AppFrame.canvasPanel.getShapesController().changeFontColour(e);
					AppFrame.canvasPanel.isContextTrigger = false;
				}
			});
			changeFontColourMenu.add(selectFontColour);
		}
	}
	
	public JMenu getAddMenu() {
		return addMenu;
	}
	public JMenu getEditMenu() {
		return editMenu;
	}

	
}
