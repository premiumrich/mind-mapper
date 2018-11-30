package net.premiumrich.shapes;

import java.awt.geom.Ellipse2D;

public class EllipseShape extends MapShape {

	public EllipseShape(double x, double y, int width, int height) {
		super(new Ellipse2D.Double(x, y, width, height), x, y);
	}
	
}
