package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.premiumrich.main.AppFrame;

/**
 * The Menubar manages the menu bar and handles actions, including the open, save and export dialogs
 * @author premiumrich
 */
public class Menubar extends JMenuBar {
	
	private static final long serialVersionUID = 0;
	
	private JMenu fileMenu;
	private JMenuItem newItem;
	private JMenuItem openItem;
	private JMenuItem saveItem;
	private JMenuItem saveAsItem;
	private JMenuItem exportItem;
	private static final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
	static {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
	}
	private static final FileNameExtensionFilter mindMapFilter = new FileNameExtensionFilter("Mind Maps (*.json)", "json");
	private static final FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Image (*.jpg)", "jpg");
	private static final FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
	private JMenuItem exitItem;
	
	private JMenu editMenu;
	
	private JMenu viewMenu;
	private JMenuItem togglePickerPanelItem;
	private JMenuItem zoomInItem;
	private JMenuItem zoomOutItem;
	private JMenuItem zoomFitItem;
	private JMenuItem zoomResetItem;
	private JMenuItem centerCanvasItem;
	
	private JMenu windowMenu;
	private JMenuItem minimizeWindowItem;
	private JMenuItem maximizeWindowItem;
	
	private JMenu helpMenu;
	private JMenuItem quickStartGuideItem;
	
	private AppFrame appFrame;
	
	public Menubar(AppFrame appFrame) {
		this.appFrame = appFrame;
		initFileMenu();
		initEditMenu();
		initViewMenu();
		initWindowMenu();
		initHelpMenu();
	}
	
	private void initFileMenu() {
		fileMenu = new JMenu("File");
		this.add(fileMenu);
		
		newItem = new JMenuItem("New");
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().reset();
			}
		});
		fileMenu.add(newItem);
		
		openItem = new JMenuItem("Open ...");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fileChooser.setDialogTitle("Open Mind Map");
				fileChooser.resetChoosableFileFilters();
				fileChooser.addChoosableFileFilter(mindMapFilter);

				// Change FileFilter selection label
				UIManager.put("FileChooser.filesOfTypeLabelText", "File Format:");
				SwingUtilities.updateComponentTreeUI(fileChooser);
				
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					appFrame.getIOCon().handleOpen(fileChooser.getSelectedFile());
				}
			}
		});
		fileMenu.add(openItem);
		
		saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (appFrame.getIOCon().getCurrentFile() != null) {
					appFrame.getIOCon().handleSave(appFrame.getIOCon().getCurrentFile());
				} else
					saveAsItem.doClick();
			}
		});
		fileMenu.add(saveItem);
		
		saveAsItem = new JMenuItem("Save As ...");
		saveAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fileChooser.setDialogTitle("Save Mind Map");
				fileChooser.resetChoosableFileFilters();
				fileChooser.addChoosableFileFilter(mindMapFilter);
				fileChooser.setSelectedFile(new File("Untitled.json"));
				
				// Change FileFilter selection label
				UIManager.put("FileChooser.filesOfTypeLabelText", "File Format:");
				SwingUtilities.updateComponentTreeUI(fileChooser);
				
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					// Append ".json" extension if missing
					File file = fileChooser.getSelectedFile();
					if (! file.getName().toLowerCase().endsWith(".json"))
					    file = new File(file.getParentFile(), file.getName() + ".json");
					appFrame.getIOCon().handleSave(file);
				}
			}
		});
		fileMenu.add(saveAsItem);
		
		fileMenu.addSeparator();
		
		exportItem = new JMenuItem("Export As ...");
		exportItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fileChooser.setDialogTitle("Export Mind Map");
				fileChooser.resetChoosableFileFilters();
				fileChooser.addChoosableFileFilter(jpgFilter);
				fileChooser.addChoosableFileFilter(pngFilter);
				fileChooser.setSelectedFile(new File("*.*"));
				
				// Change FileFilter selection label
				UIManager.put("FileChooser.filesOfTypeLabelText", "Select Export Format:");
				SwingUtilities.updateComponentTreeUI(fileChooser);
				
				if (fileChooser.showDialog(null, "Export") == JFileChooser.APPROVE_OPTION) {
					if (fileChooser.getFileFilter() == jpgFilter) {
						// Append ".jpg" extension if missing
						File file = fileChooser.getSelectedFile();
						if (! file.getName().toLowerCase().endsWith(".jpg"))
						    file = new File(file.getParentFile(), file.getName() + ".jpg");
						int scale = showExportScalePopup();
						if (scale != 0) appFrame.getIOCon().handleExport(file, "jpg", scale);
					} else if (fileChooser.getFileFilter() == pngFilter) {
						// Append ".png" extension if missing
						File file = fileChooser.getSelectedFile();
						if (! file.getName().toLowerCase().endsWith(".png"))
						    file = new File(file.getParentFile(), file.getName() + ".png");
						int scale = showExportScalePopup();
						if (scale != 0) appFrame.getIOCon().handleExport(file, "png", scale);
					}
				}
			}
		});
		fileMenu.add(exportItem);
		
		fileMenu.addSeparator();
		
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
	}

	private void initEditMenu() {
		editMenu = new JMenu("Edit");
		this.add(editMenu);
		
		editMenu.add(new ContextMenu(appFrame.getCanvasPanel()).getAddMenu());
	}
	
	private void initViewMenu() {
		viewMenu = new JMenu("View");
		viewMenu.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent evt) {
				if (appFrame.getPickerPanel().isVisible())
					togglePickerPanelItem.setText("Hide Picker Panel");
				else
					togglePickerPanelItem.setText("Show Picker Panel");
			}
			public void menuDeselected(MenuEvent evt) {
			}
			public void menuCanceled(MenuEvent evt) {
			}
		});
		this.add(viewMenu);
		
		togglePickerPanelItem = new JMenuItem("", KeyEvent.VK_F12);
		togglePickerPanelItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		togglePickerPanelItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (appFrame.getPickerPanel().isVisible())
					appFrame.getPickerPanel().setVisible(false);
				else
					appFrame.getPickerPanel().setVisible(true);
			}
		});
		viewMenu.add(togglePickerPanelItem);
		
		zoomInItem = new JMenuItem("Zoom In");
		zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_DOWN_MASK));
		zoomInItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().getViewport().zoomIn();
			}
		});
		viewMenu.add(zoomInItem);
		
		zoomOutItem = new JMenuItem("Zoom Out");
		zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_MASK));
		zoomOutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().getViewport().zoomOut();
			}
		});
		viewMenu.add(zoomOutItem);
		
//		zoomFitItem
		
		zoomResetItem = new JMenuItem("Zoom 100%");
		zoomResetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_MASK));
		zoomResetItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().getViewport().reset();
			}
		});
		viewMenu.add(zoomResetItem);
		
//		centerCanvasItem
	}
	
	private void initWindowMenu() {
		windowMenu = new JMenu("Window");
		this.add(windowMenu);
	}
	
	private void initHelpMenu() {
		helpMenu = new JMenu("Help");
		this.add(helpMenu);
	}
	
	/**
	 * 
	 * @return int chosen scale factor or 0 if cancelled
	 */
	private int showExportScalePopup() {
		// Add choosable export qualities and calculate their final dimensions
		String exportQualities[] = new String[5];
		for (int i = 1; i <= exportQualities.length; i++) {
			exportQualities[i-1] = i +"x (" + appFrame.getCanvasPanel().getWidth()*i + "x" + appFrame.getCanvasPanel().getHeight()*i + ")";
		}
		// Pop up a dialog
		String output = (String) JOptionPane.showInputDialog(fileChooser, "Image size:", "Export Options",
									JOptionPane.PLAIN_MESSAGE, null, exportQualities, null);
		if (output != null) return Integer.parseInt(Character.toString(output.charAt(0)));
		else 				return 0;
	}
	
}
