package net.premiumrich.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class QuickStartGuide extends JPanel {

    private static final long serialVersionUID = 0;
    
    // Render the quick start guide with HTML
    private static final String message = 
        "<html>" +
            "<h1 style=\"font-family: serif;\">Quick Start Guide</h1>" +
            "<ul>" +
                "<li style=\"margin-bottom: 3px\">Add shapes via the left picker panel, top menu bar, or right-click context menu" +
                "<li style=\"margin-bottom: 3px\">Edit the text of shapes by double-clicking inside" +
                "<li style=\"margin-bottom: 3px\">Change the style of shapes via the right-click \"Edit\" menu" +
                "<li style=\"margin-bottom: 3px\">Link shapes with lines via the right-click \"Connections\" menu" +
                "<li style=\"margin-bottom: 3px\">Options for open, save, save as, export, etc. can be found in the \"File\" menu" +
            "</ul>" +
        "</html>"
    ;

    private static JTextPane guide;

    public QuickStartGuide() {
        this.setPreferredSize(new Dimension(500, 400));
        this.setBorder(new EmptyBorder(0, 10, 20, 40));
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());

        guide = new JTextPane();
        guide.setContentType("text/html");
        guide.setText(message);
        this.add(guide, BorderLayout.CENTER);
    }
    
}