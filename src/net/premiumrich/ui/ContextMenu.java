package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.premiumrich.shapes.MapShape;

import java.awt.Color;
import java.awt.Font;

/**
 * The ContextMenu manages and handles actions in the right-click menu
 * @author premiumrich
 */
public class ContextMenu extends JPopupMenu {

	private static final long serialVersionUID = 0;
	
	private CanvasPanel canvasPanel;
	
	private JMenu addMenu;
	private JMenuItem addEllipseMenuItem;
	private JMenuItem addRectangleMenuItem;
	
	private JMenu editMenu;
	private JMenuItem removeElementMenuItem;
	
	private JMenu changeBorderWidthMenu;
	private JSlider changeBorderWidthSlider;
	private JMenu changeBorderColourMenu;
	
	private JMenu changeFontColourMenu;
	private JMenu changeFontMenu;
	private JMenu changeFontStyleMenu;
	private JMenu changeFontSizeMenu;
	private JTextField changeFontSizeField;
	
	// Lookup tables for values and their names
	private static final HashMap<String,Color> colours = new HashMap<String,Color>();
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
	private static final HashMap<String,String> fonts = new HashMap<String,String>();
	static {
		fonts.put("Times New Roman", "Serif");
		fonts.put("Helvetica", "SansSerif");
		fonts.put("Courier", "Monospaced");
	}
	private static final HashMap<String,Integer> fontStyles = new HashMap<String,Integer>();
	static {
		fontStyles.put("Plain", Font.PLAIN);
		fontStyles.put("Bold", Font.BOLD);
		fontStyles.put("Italic", Font.ITALIC);
	}
	
	
	public ContextMenu(CanvasPanel canvasPanel) {
		this.canvasPanel = canvasPanel;
		initAddMenu();
		initEditMenu();
	}
	
	private void initAddMenu() {
		addMenu = new JMenu("Add ...");
		this.add(addMenu);
		
		addEllipseMenuItem = new JMenuItem("Ellipse shape");
		addEllipseMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvasPanel.getShapesController().addShape("net.premiumrich.shapes.EllipseShape");
			}
		});
		addMenu.add(addEllipseMenuItem);
		
		addRectangleMenuItem = new JMenuItem("Rectangle shape");
		addRectangleMenuItem.setActionCommand("Rectangle");
		addRectangleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvasPanel.getShapesController().addShape("net.premiumrich.shapes.RectangleShape");
			}
		});
		addMenu.add(addRectangleMenuItem);
	}
	
	private void initEditMenu() {
		editMenu = new JMenu("Edit");
		this.add(editMenu);
		
		removeElementMenuItem = new JMenuItem("Remove this");
		editMenu.add(removeElementMenuItem);
		removeElementMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvasPanel.getShapesController().removeSelectedShape();
			}
		});
		
		editMenu.addSeparator();
		
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
				canvasPanel.getShapesController().changeBorderWidth(changeBorderWidthSlider.getValue());
			}
		});
		changeBorderWidthMenu.add(changeBorderWidthSlider);
		
		changeBorderColourMenu = new JMenu("Border colour");
		editMenu.add(changeBorderColourMenu);
		// Iterate through available colours and create a new menu item for each
		for (String colourName : colours.keySet()) {
			JMenuItem selectBorderColour = new JMenuItem(colourName);
			selectBorderColour.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					canvasPanel.getShapesController().changeBorderColour(colours.get(colourName));
				}
			});
			changeBorderColourMenu.add(selectBorderColour);
		}
		
		editMenu.addSeparator();
		
		changeFontMenu = new JMenu("Text font");
		editMenu.add(changeFontMenu);
		// Iterate through available fonts and create a new menu item for each
		for (String fontName : fonts.keySet()) {
			JMenuItem selectFont = new JMenuItem(fontName);
			selectFont.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					canvasPanel.getShapesController().changeFont(fontName);
				}
			});
			changeFontMenu.add(selectFont);
		}
		
		changeFontStyleMenu = new JMenu("Text font style");
		editMenu.add(changeFontStyleMenu);
		// Iterate through available font styles and create a new radio button for each
		for (String fontStyle : fontStyles.keySet()) {
			JMenuItem selectFontStyle = new JMenuItem(fontStyle);
			selectFontStyle.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					canvasPanel.getShapesController().changeFontStyle(fontStyles.get(fontStyle));
				}
			});
			changeFontStyleMenu.add(selectFontStyle);
		}
		
		changeFontSizeMenu = new JMenu("Text font size");
		editMenu.add(changeFontSizeMenu);
		// Create a text field for custom font sizes
		changeFontSizeField = new JTextField();
		changeFontSizeField.setColumns(3);
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
				// Parse as int if the text field only contains numbers
				if (changeFontSizeField.getText().matches("^\\d+$")) {
					canvasPanel.getShapesController()
									.changeFontSize(Integer.parseInt(changeFontSizeField.getText()));
				}
			}
		});
		changeFontSizeMenu.add(changeFontSizeField);
		
		changeFontColourMenu = new JMenu("Text font colour");
		editMenu.add(changeFontColourMenu);
		// Iterate through available colours and create a new menu item for each
		for (String colourName : colours.keySet()) {
			JMenuItem selectFontColour = new JMenuItem(colourName);
			selectFontColour.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					canvasPanel.getShapesController().changeFontColour(colours.get(colourName));
				}
			});
			changeFontColourMenu.add(selectFontColour);
		}
	}
	
	public void updateEditMenuValues(MapShape selectedShape) {
		// Update border width and font size values for the selected shape
		changeBorderWidthSlider.setValue(selectedShape.getBorderWidth());
		changeFontSizeField.setText(Integer.toString(selectedShape.getTextFont().getSize()));
	}
	
	public JMenu getAddMenu() {
		return addMenu;
	}
	public JMenu getEditMenu() {
		return editMenu;
	}

	
}
