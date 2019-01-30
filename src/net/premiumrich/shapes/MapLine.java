package net.premiumrich.shapes;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.geom.Line2D;

import com.google.gson.JsonObject;

public class MapLine {

	private Line2D line;
	private BasicStroke stroke;

	private MapShape origin;
	private MapShape termination;
	
	public MapLine(MapShape origin, MapShape termination) {
		stroke = new BasicStroke(2);
		this.origin = origin;
		this.termination = termination;
		updateConnection();
	}
	
	public void updateConnection() {
		// Line exists between the center of the origin and the termination
		Point originP = origin.getShape().getBounds().getLocation();
		originP.translate(origin.getShape().getBounds().width/2, origin.getShape().getBounds().height/2);
		Point terminationP = termination.getShape().getBounds().getLocation();
		terminationP.translate(termination.getShape().getBounds().width/2, termination.getShape().getBounds().height/2);
		
		line = new Line2D.Double(originP, terminationP);
	}
	
	// Getters
	public BasicStroke getStroke() {
		return stroke;
	}
	public MapShape getOrigin() {
		return origin;
	}
	public MapShape getTermination() {
		return termination;
	}
	public Line2D getLine() {
		return line;
	}
	public JsonObject getAsJsonObject() {
		JsonObject thisConnection = new JsonObject();
		thisConnection.addProperty("Stroke width", stroke.getLineWidth());
		thisConnection.addProperty("Origin ID", origin.getId().toString());
		thisConnection.addProperty("Termination ID", termination.getId().toString());
		return thisConnection;
	}
	
}
