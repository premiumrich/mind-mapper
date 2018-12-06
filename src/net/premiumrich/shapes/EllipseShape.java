package net.premiumrich.shapes;

import java.awt.geom.Ellipse2D;

public class EllipseShape extends MapShape {

	public EllipseShape(int x, int y, int width, int height) {
		super(new Ellipse2D.Double(x, y, width, height), x, y);
	}
	
}
