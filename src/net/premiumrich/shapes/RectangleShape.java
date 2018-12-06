package net.premiumrich.shapes;

import java.awt.geom.Rectangle2D;

public class RectangleShape extends MapShape {
	
	public RectangleShape(int x, int y, int width, int height) {
		super(new Rectangle2D.Double(x, y, width, height), x, y);
	}
	
}
