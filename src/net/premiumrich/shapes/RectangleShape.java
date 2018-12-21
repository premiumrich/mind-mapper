package net.premiumrich.shapes;

import java.awt.geom.Rectangle2D;

public class RectangleShape extends MapShape {
	
	public RectangleShape(int x, int y, int width, int height) {
		super(new Rectangle2D.Double(x, y, width, height));
	}
	
	@Override
	public void setNewCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
		shape = new Rectangle2D.Double(x, y, shape.getBounds().width, shape.getBounds().height);
		updateTextFieldBounds();
	}
	
}
