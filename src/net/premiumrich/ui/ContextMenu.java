package net.premiumrich.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ContextMenu extends JPopupMenu {

	private static final long serialVersionUID = 0;
	
	private JMenu addMenu;		// Allow access from MainMenubar
	private JMenuItem addEllipseMenuItem;
	private JMenuItem addRectangleMenuItem;
	
	JMenu editMenu;
	private JMenuItem removeElementMenuItem;
	
	public ContextMenu() {
		initAddMenu();
		initEditMenu();
	}
	
	private void initAddMenu() {
		addMenu = new JMenu("Add ...");
		this.add(addMenu);
		
		addEllipseMenuItem = new JMenuItem("Ellipse shape");
		addEllipseMenuItem.setActionCommand("+ellipse");
		addEllipseMenuItem.addActionListener(new AddShapeListener());
		addMenu.add(addEllipseMenuItem);
		
		addRectangleMenuItem = new JMenuItem("Rectangle shape");
		addRectangleMenuItem.setActionCommand("+rectangle");
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
	}
	
	public JMenu getAddMenu() {
		return addMenu;
	}
	
}
