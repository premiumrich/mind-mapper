package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.premiumrich.main.AppFrame;

import java.awt.Color;

public class ContextMenu extends JPopupMenu {

	private static final long serialVersionUID = 0;
	
	private JMenu addMenu;
	private JMenuItem addEllipseMenuItem;
	private JMenuItem addRectangleMenuItem;
	
	private JMenu editMenu;
	private JMenuItem removeElementMenuItem;
	
	private JMenu changeBorderWidthMenu;
	private JSlider changeBorderWidthSlider;
	
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
	
	private JMenu changeFontStyleMenu;
	public static final HashMap<String,Integer> fontStyles = new HashMap<String,Integer>();
	static {
		fontStyles.put("Plain", 0);
		fontStyles.put("Bold", 1);
		fontStyles.put("Italic", 2);
	}
	
	private JMenu changeFontSizeMenu;
	private static Hashtable<Integer,JLabel> fontSizes = new Hashtable<Integer,JLabel>();
	static {
		fontSizes.put(6, new JLabel("6"));
		fontSizes.put(8, new JLabel("8"));
		fontSizes.put(10, new JLabel("10"));
		fontSizes.put(11, new JLabel("11"));
		fontSizes.put(12, new JLabel("12"));
	}
	private JTextField changeFontSizeField;
	
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
		
		changeBorderWidthMenu = new JMenu("Border width");
		editMenu.add(changeBorderWidthMenu);
		// Create a slider to set border width
		changeBorderWidthSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 3);
		changeBorderWidthSlider.setMajorTickSpacing(1);
		changeBorderWidthSlider.setSnapToTicks(true);
		changeBorderWidthSlider.setPaintTicks(true);
		changeBorderWidthSlider.setPaintLabels(true);
		changeBorderWidthSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				AppFrame.canvasPanel.getShapesController().changeBorderWidth(e);
				AppFrame.canvasPanel.isContextTrigger = false;
			}
		});
		changeBorderWidthMenu.add(changeBorderWidthSlider);
		
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
		
		changeFontStyleMenu = new JMenu("Font style");
		editMenu.add(changeFontStyleMenu);
		// Iterate through available font styles and create a new menu item for each
		for (String fontStyle : fontStyles.keySet()) {
			JMenuItem selectFontStyle = new JMenuItem(fontStyle);
			selectFontStyle.setActionCommand(fontStyles.get(fontStyle).toString());
			selectFontStyle.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AppFrame.canvasPanel.getShapesController().changeFontStyle(e);
					AppFrame.canvasPanel.isContextTrigger = false;
				}
			});
			changeFontStyleMenu.add(selectFontStyle);
		}
		
		changeFontSizeMenu = new JMenu("Font size");
		editMenu.add(changeFontSizeMenu);
		// Create a text field for custom font sizes
		changeFontSizeField = new JTextField("12");
		changeFontSizeField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				changeFontSize();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				changeFontSize();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
			private void changeFontSize() {
				if (!changeFontSizeField.getText().isEmpty()) {
					AppFrame.canvasPanel.getShapesController().changeFontSize(
							Integer.parseInt(changeFontSizeField.getText()));
					AppFrame.canvasPanel.isContextTrigger = false;
				}
			}
		});
		changeFontSizeMenu.add(changeFontSizeField);
		
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
