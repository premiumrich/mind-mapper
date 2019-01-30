package net.premiumrich.shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Grid {

	private int gridSize;
	private int gridInterval;
	private float gridWidth;
	private float gridWidthMinorAxes;
	private float gridWidthMajorAxes;
	private Color gridLines;
	private Color gridMinorAxes;
	private Color gridMajorAxes;

    public Grid(int gridSize) {
        this.gridSize = gridSize;
        gridInterval = 25;
        gridWidth = (float)0.5;
        gridWidthMinorAxes = (float)0.5;
        gridWidthMajorAxes = 2;
        gridLines = Color.lightGray;
        gridMinorAxes = Color.gray;
        gridMajorAxes = Color.gray;
    }

    public void drawGrid(Graphics2D g2d) {
		for (int x = -gridSize; x <= gridSize; x += gridInterval) {     // Draw all vertical lines
			if (x == 0) {							                    // Draw major axes darker
				g2d.setColor(gridMajorAxes);
				g2d.setStroke(new BasicStroke(gridWidthMajorAxes));
			} else if (x % (gridInterval*10) == 0) {                    // Draw minor axes (every 10 intervals) slighly darker
				g2d.setColor(gridMinorAxes);
				g2d.setStroke(new BasicStroke(gridWidthMinorAxes));
			} else {
				g2d.setColor(gridLines);
				g2d.setStroke(new BasicStroke(gridWidth));
			}
			g2d.drawLine(x, -gridSize, x, gridSize);
		}
		for (int y = -gridSize; y <= gridSize; y += gridInterval) {     // Draw all horizontal lines
			if (y == 0) {                                               // Draw major axes darker
				g2d.setColor(gridMajorAxes);
				g2d.setStroke(new BasicStroke(gridWidthMajorAxes));
			} else if (y % (gridInterval*10) == 0) {                    // Draw minor axes (every 10 intervals) slighly darker
				g2d.setColor(gridMinorAxes);
				g2d.setStroke(new BasicStroke(gridWidthMinorAxes));
			} else {
				g2d.setColor(gridLines);
				g2d.setStroke(new BasicStroke(gridWidth));
			}
			g2d.drawLine(-gridSize, y, gridSize, y);
		}
    }

}