package net.premiumrich.shapes;

import java.awt.Shape;

import com.google.gson.JsonObject;

public abstract class MapShape {
	
	private Shape shape;
	private double x, y;
	
	public MapShape(Shape shape, double x, double y) {
		this.shape = shape;
		this.x = x;
		this.y = y;
	}
	
	public Shape getShape() {
		return shape;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
	public JsonObject getAsJsonObj() {
		JsonObject thisShape = new JsonObject();
		thisShape.addProperty("Type", this.getClass().getName());
		thisShape.addProperty("X", x);
		thisShape.addProperty("Y", y);
		thisShape.addProperty("Width", shape.getBounds().getWidth());
		thisShape.addProperty("Height", shape.getBounds().getHeight());
		return thisShape;
	}
	
}
