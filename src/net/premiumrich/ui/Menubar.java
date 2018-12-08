package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.premiumrich.io.IOHandler;
import net.premiumrich.main.AppFrame;

public class Menubar extends JMenuBar {

	private static final long serialVersionUID = 0;
	
	private JMenu fileMenu;
	private JMenuItem fileOpenMenuItem;
	private JMenuItem fileSaveMenuItem;
	
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
				IOHandler io = new IOHandler(AppFrame.canvasPanel);
				io.handleOpen();
			}
		});
		fileMenu.add(fileOpenMenuItem);
		
		fileSaveMenuItem = new JMenuItem("Save");
		fileSaveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IOHandler io = new IOHandler(AppFrame.canvasPanel);
				io.handleSave();
			}
		});
		fileMenu.add(fileSaveMenuItem);
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
	
}
