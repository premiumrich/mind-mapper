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
	private JMenuItem newFile;
	private JMenuItem open;
	private JMenuItem save;
	private JMenuItem saveAs;
	private JMenuItem export;
	private static final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
	static {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
	}
	private static final FileNameExtensionFilter mindMapFilter = new FileNameExtensionFilter("Mind Maps (*.json)", "json");
	private static final FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Image (*.jpg)", "jpg");
	private static final FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
	private JMenuItem exit;
	
	private JMenu editMenu;
	
	private JMenu viewMenu;
	private JMenuItem toggleGrid;
	private JMenuItem zoomIn;
	private JMenuItem zoomOut;
	private JMenuItem zoomReset;
	private JMenuItem centerCanvas;
	
	private JMenu windowMenu;
	private JMenuItem togglePickerPanel;
	private JMenuItem toggleDebugPanel;
	
	private JMenu helpMenu;
	private JMenuItem viewQuickStartGuide;
	
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
		
		newFile = new JMenuItem("New");
		newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		newFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().reset();
			}
		});
		fileMenu.add(newFile);
		
		open = new JMenuItem("Open ...");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		open.addActionListener(new ActionListener() {
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
		fileMenu.add(open);
		
		save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (appFrame.getIOCon().getCurrentFile() != null) {
					appFrame.getIOCon().handleSave(appFrame.getIOCon().getCurrentFile());
				} else
					saveAs.doClick();
			}
		});
		fileMenu.add(save);
		
		saveAs = new JMenuItem("Save As ...");
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		saveAs.addActionListener(new ActionListener() {
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
		fileMenu.add(saveAs);
		
		fileMenu.addSeparator();
		
		export = new JMenuItem("Export As ...");
		export.addActionListener(new ActionListener() {
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
		fileMenu.add(export);
		
		fileMenu.addSeparator();
		
		exit = new JMenuItem("Exit");
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
		fileMenu.add(exit);
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
				if (appFrame.getCanvasPanel().getViewport().isGridVisible()) toggleGrid.setText("Hide Grid");
				else toggleGrid.setText("Show Grid");
			}
			public void menuDeselected(MenuEvent evt) {
			}
			public void menuCanceled(MenuEvent evt) {
			}
		});
		this.add(viewMenu);
		
		toggleGrid = new JMenuItem("", KeyEvent.VK_F4);
		toggleGrid.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		toggleGrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (appFrame.getCanvasPanel().getViewport().isGridVisible()) appFrame.getCanvasPanel().getViewport().setGridVisible(false);
				else appFrame.getCanvasPanel().getViewport().setGridVisible(true);
			}
		});
		viewMenu.add(toggleGrid);

		viewMenu.addSeparator();

		zoomIn = new JMenuItem("Zoom In");
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().getViewport().zoomIn();
			}
		});
		viewMenu.add(zoomIn);
		
		zoomOut = new JMenuItem("Zoom Out");
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
		zoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().getViewport().zoomOut();
			}
		});
		viewMenu.add(zoomOut);
		
		zoomReset = new JMenuItem("Zoom 100%");
		zoomReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_MASK));
		zoomReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().getViewport().reset();
			}
		});
		viewMenu.add(zoomReset);
		
		centerCanvas = new JMenuItem("Center Canvas");
		centerCanvas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, KeyEvent.CTRL_MASK));
		centerCanvas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appFrame.getCanvasPanel().getViewport().centerView();
			}
		});
		viewMenu.add(centerCanvas);
	}
	
	private void initWindowMenu() {
		windowMenu = new JMenu("Window");
		windowMenu.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent evt) {
				if (appFrame.getPickerPanel().isVisible()) togglePickerPanel.setText("Hide Picker Panel");
				else togglePickerPanel.setText("Show Picker Panel");

				if (appFrame.getDebugPanel().isVisible()) toggleDebugPanel.setText("Hide Debug Panel");
				else toggleDebugPanel.setText("Show Debug Panel");
			}
			public void menuDeselected(MenuEvent evt) {
			}
			public void menuCanceled(MenuEvent evt) {
			}
		});
		this.add(windowMenu);

		togglePickerPanel = new JMenuItem("", KeyEvent.VK_F12);
		togglePickerPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		togglePickerPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (appFrame.getPickerPanel().isVisible()) appFrame.getPickerPanel().setVisible(false);
				else appFrame.getPickerPanel().setVisible(true);
			}
		});
		windowMenu.add(togglePickerPanel);

		toggleDebugPanel = new JMenuItem("", KeyEvent.VK_F3);
		toggleDebugPanel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		toggleDebugPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (appFrame.getDebugPanel().isVisible()) {
					appFrame.getDebugPanel().setVisible(false);
					appFrame.getCanvasPanel().getViewport().debugLabelsUpdater.stop();
				} else {
					appFrame.getDebugPanel().setVisible(true);
					appFrame.getCanvasPanel().getViewport().debugLabelsUpdater.start();
				}
			}
		});
		windowMenu.add(toggleDebugPanel);
	}
	
	private void initHelpMenu() {
		helpMenu = new JMenu("Help");
		this.add(helpMenu);

		viewQuickStartGuide = new JMenuItem("Quick Start Guide");
		viewQuickStartGuide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(appFrame, new QuickStartGuide(), "Mind Mapper", JOptionPane.INFORMATION_MESSAGE, null);
			}
		});
		helpMenu.add(viewQuickStartGuide);
	}
	
	/**
	 * Allow the user to select the export quality via a dialog
	 * @return int chosen scale factor or 0 if cancelled
	 */
	private int showExportScalePopup() {
		// Add choosable export qualities and calculate their final dimensions
		String exportQualities[] = new String[5];
		for (int i = 1; i <= exportQualities.length; i++)
			exportQualities[i-1] = i +"x (" + appFrame.getCanvasPanel().getWidth()*i + "x" + appFrame.getCanvasPanel().getHeight()*i + ")";
		// Pop up a dialog
		String output = (String) JOptionPane.showInputDialog(fileChooser, "Image size:", "Export Options",
									JOptionPane.PLAIN_MESSAGE, null, exportQualities, null);
		if (output != null) return Integer.parseInt(Character.toString(output.charAt(0)));
		else 				return 0;
	}
	
}
