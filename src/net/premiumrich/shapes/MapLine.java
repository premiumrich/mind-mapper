package net.premiumrich.shapes;

import java.awt.geom.Line2D;

public class MapLine {

	private Line2D line;
	
	private MapShape origin;
	private MapShape termination;
	
	public MapLine(MapShape origin, MapShape termination) {
		this.origin = origin;
		this.termination = termination;
		updateConnection();
	}
	
	public void updateConnection() {
		// This only creates a line between the upper left corner of the two shapes
		line = new Line2D.Double(origin.getShape().getBounds().getLocation(), 
								termination.getShape().getBounds().getLocation());
	}
	
	public Line2D getLine() {
		return line;
	}
	
}
