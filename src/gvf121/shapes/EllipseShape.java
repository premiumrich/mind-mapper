package gvf121.shapes;

import java.awt.geom.Ellipse2D;

public class EllipseShape extends MapShape {

	public EllipseShape(int x, int y, int width, int height) {
		super(new Ellipse2D.Double(x, y, width, height));
	}

	@Override
	public void setNewCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
		shape = new Ellipse2D.Double(x, y, shape.getBounds().width, shape.getBounds().height);
		updateTextFieldBounds();
	}
	
}
