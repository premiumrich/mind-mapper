package gvf121.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.border.Border;
import javax.swing.*;

public class DebugPanel extends JPanel {

    private static final long serialVersionUID = 0;
    
	protected static JLabel fpsLbl;
	protected static JLabel zoomLbl;
	protected static JLabel xOffsetLbl;
	protected static JLabel yOffsetLbl;

    public DebugPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setPreferredSize(new Dimension(100, 0));
		this.setBackground(Color.LIGHT_GRAY);
		Border compound = BorderFactory.createEtchedBorder();
		compound = BorderFactory.createCompoundBorder(compound, BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.setBorder(compound);
        this.setVisible(false);
        
		fpsLbl = new JLabel();
		this.add(fpsLbl);
		
		zoomLbl = new JLabel();
		this.add(zoomLbl);
		
		xOffsetLbl = new JLabel();
		this.add(xOffsetLbl);
		yOffsetLbl = new JLabel();
		this.add(yOffsetLbl);
    }
	
}
