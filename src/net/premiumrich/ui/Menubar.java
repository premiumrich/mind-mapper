package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

import net.premiumrich.io.IOController;
import net.premiumrich.main.AppFrame;

public class Menubar extends JMenuBar {

	private static final long serialVersionUID = 0;
	
	private static final IOController io = new IOController(AppFrame.canvasPanel);
	
	private JMenu fileMenu;
	private JMenuItem fileOpenMenuItem;
	private JMenuItem fileSaveMenuItem;
	private JMenuItem fileSaveAsMenuItem;
	private static final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
	static {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Mind Maps (*.json)", "json"));
	}
	
	private JMenu editMenu;
	
	private JMenu viewMenu;
	private JMenu windowMenu;
	private JMenu helpMenu;
	
	public Menubar() {
		initFileMenu();
		initEditMenu();
		initViewMenu();
		initWindowMenu();
		initHelpMenu();
	}
	
	private void initFileMenu() {
		fileMenu = new JMenu("File");
		this.add(fileMenu);
		
		fileOpenMenuItem = new JMenuItem("Open");
		fileOpenMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Open Mind Map");
				fileChooser.setSelectedFile(new File(""));
				int action = fileChooser.showOpenDialog(null);
				if (action == JFileChooser.APPROVE_OPTION) {
					io.handleOpen(fileChooser.getSelectedFile());
				}
			}
		});
		fileMenu.add(fileOpenMenuItem);
		
		fileSaveMenuItem = new JMenuItem("Save");
		fileSaveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (io.fileOpened) {
					io.handleSave(io.getCurrentFile());
				} else
					saveAs();
			}
		});
		fileMenu.add(fileSaveMenuItem);
		
		fileSaveAsMenuItem = new JMenuItem("Save As");
		fileSaveAsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});
		fileMenu.add(fileSaveAsMenuItem);
	}

	private void initEditMenu() {
		editMenu = new JMenu("Edit");
		this.add(editMenu);
		
		editMenu.add(new ContextMenu().getAddMenu());
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
	
	// Helper methods
	private void saveAs() {
		fileChooser.setDialogTitle("Save Mind Map");
		fileChooser.setSelectedFile(new File("My Mind Map.json"));
		int action = fileChooser.showSaveDialog(null);
		if (action == JFileChooser.APPROVE_OPTION) {
			// Append ".json" extension if missing
			File file = fileChooser.getSelectedFile();
			if (! FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
			    file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName()) + ".json");
			}
			io.handleSave(file);
		}
	}
	
}
