package net.premiumrich.shapes;

import java.awt.geom.Ellipse2D;

public class EllipseShape extends MapShape {

	public EllipseShape(double x, double y, int width, int height) {
		this.setX(x);
		this.setY(y);
		
		this.setShape(new Ellipse2D.Double(x, y, width, height));
	}
	
}
