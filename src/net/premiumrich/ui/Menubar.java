package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.premiumrich.io.IOController;

public class Menubar extends JMenuBar {

	private static final long serialVersionUID = 0;
	
	private CanvasPanel canvasPanel;
	private IOController ioCon;
	
	private JMenu fileMenu;
	private JMenuItem fileOpenMenuItem;
	private JMenuItem fileSaveMenuItem;
	private JMenuItem fileSaveAsMenuItem;
	private static final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
	static {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
	}
	FileNameExtensionFilter mindMapFileFilter = new FileNameExtensionFilter("Mind Maps (*.json)", "json");
	FileNameExtensionFilter jpgFileFilter = new FileNameExtensionFilter("JPEG Image (*.jpg)", "jpg");
	FileNameExtensionFilter pngFileFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
	private JMenuItem fileExportMenuItem;
	
	private JMenu editMenu;
	
	private JMenu viewMenu;
	private JMenu windowMenu;
	private JMenu helpMenu;
	
	public Menubar(CanvasPanel canvasPanel, IOController ioCon) {
		this.canvasPanel = canvasPanel;
		this.ioCon = ioCon;
		initFileMenu();
		initEditMenu();
		initViewMenu();
		initWindowMenu();
		initHelpMenu();
	}
	
	private void initFileMenu() {
		fileMenu = new JMenu("File");
		this.add(fileMenu);
		
		fileOpenMenuItem = new JMenuItem("Open ...");
		fileOpenMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Open Mind Map");
				fileChooser.resetChoosableFileFilters();
				fileChooser.addChoosableFileFilter(mindMapFileFilter);

				// Change FileFilter selection label
				UIManager.put("FileChooser.filesOfTypeLabelText", "File Format:");
				SwingUtilities.updateComponentTreeUI(fileChooser);
				
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					ioCon.handleOpen(fileChooser.getSelectedFile());
				}
			}
		});
		fileMenu.add(fileOpenMenuItem);
		
		fileSaveMenuItem = new JMenuItem("Save");
		fileSaveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ioCon.getCurrentFile() != null) {
					ioCon.handleSave(ioCon.getCurrentFile());
				} else
					fileSaveAsMenuItem.doClick();
			}
		});
		fileMenu.add(fileSaveMenuItem);
		
		fileSaveAsMenuItem = new JMenuItem("Save As");
		fileSaveAsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Save Mind Map");
				fileChooser.resetChoosableFileFilters();
				fileChooser.addChoosableFileFilter(mindMapFileFilter);
				fileChooser.setSelectedFile(new File("Untitled.json"));
				
				// Change FileFilter selection label
				UIManager.put("FileChooser.filesOfTypeLabelText", "File Format:");
				SwingUtilities.updateComponentTreeUI(fileChooser);
				
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					// Append ".json" extension if missing
					File file = fileChooser.getSelectedFile();
					if (! file.getName().endsWith(".json"))
					    file = new File(file.getParentFile(), file.getName() + ".json");
					ioCon.handleSave(file);
				}
			}
		});
		fileMenu.add(fileSaveAsMenuItem);
		
		fileMenu.addSeparator();
		
		fileExportMenuItem = new JMenuItem("Export As ...");
		fileExportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Export Mind Map");
				fileChooser.resetChoosableFileFilters();
				fileChooser.addChoosableFileFilter(jpgFileFilter);
				fileChooser.addChoosableFileFilter(pngFileFilter);
				fileChooser.setSelectedFile(new File("*.*"));
				
				// Change FileFilter selection label
				UIManager.put("FileChooser.filesOfTypeLabelText", "Select Export Format:");
				SwingUtilities.updateComponentTreeUI(fileChooser);
				
				if (fileChooser.showDialog(null, "Export") == JFileChooser.APPROVE_OPTION) {
					if (fileChooser.getFileFilter() == jpgFileFilter) {
						// Append ".jpg" extension if missing
						File file = fileChooser.getSelectedFile();
						if (! file.getName().endsWith(".jpg"))
						    file = new File(file.getParentFile(), file.getName() + ".jpg");
						ioCon.handleExport(file, "jpg");
					} else if (fileChooser.getFileFilter() == pngFileFilter) {
						// Append ".png" extension if missing
						File file = fileChooser.getSelectedFile();
						if (! file.getName().endsWith(".png"))
						    file = new File(file.getParentFile(), file.getName() + ".png");
						ioCon.handleExport(file, "png");
					}
				}
			}
		});
		fileMenu.add(fileExportMenuItem);
	}

	private void initEditMenu() {
		editMenu = new JMenu("Edit");
		this.add(editMenu);
		
		editMenu.add(new ContextMenu(canvasPanel).getAddMenu());
	}
	
	private void initViewMenu() {
		viewMenu = new JMenu("View");
		this.add(viewMenu);
	}
	
	private void initWindowMenu() {
		windowMenu = new JMenu("Window");
		this.add(windowMenu);
	}
	
	private void initHelpMenu() {
		helpMenu = new JMenu("Help");
		this.add(helpMenu);
	}
	
}
