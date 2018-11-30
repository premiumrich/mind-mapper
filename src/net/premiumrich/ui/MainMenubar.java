package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class MainMenubar extends JMenuBar {

	private static final long serialVersionUID = 0;
	
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu viewMenu;
	private JMenu windowMenu;
	private JMenu helpMenu;
	
	private JMenuItem fileOpenMenuItem;
	private JMenuItem fileSaveMenuItem;
	
	public MainMenubar() {
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
				CanvasPanel.handleOpen();
			}
		});
		fileMenu.add(fileOpenMenuItem);
		
		fileSaveMenuItem = new JMenuItem("Save");
		fileSaveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CanvasPanel.handleSave();
			}
		});
		fileMenu.add(fileSaveMenuItem);
	}

	private void initEditMenu() {
		editMenu = new JMenu("Edit");
		this.add(editMenu);
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
