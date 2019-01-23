package net.premiumrich.shapes;

import java.awt.Shape;
import java.awt.geom.Path2D;

public class TriangleShape extends MapShape {
	
	public TriangleShape(int x, int y, int width, int height) {
		super(createNewTriangle(x, y, width, height));
    }
    
    private static Shape createNewTriangle(int x, int y, int width, int height) {
        Path2D triangle = new Path2D.Double();
        triangle.moveTo(x + width/2, y);             // Draw top point
        triangle.lineTo(x, y + height);             // Draw left point
        triangle.lineTo(x + width, y + height);      // Draw left point
        triangle.closePath();
        return triangle;
    }
	
	@Override
	public void setNewCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
		shape = createNewTriangle(x, y, shape.getBounds().width, shape.getBounds().height);
		updateTextFieldBounds();
	}
	
}
