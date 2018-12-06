package net.premiumrich.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;

import com.google.gson.JsonObject;

public abstract class MapShape {
	
	private Shape shape;
	private int x, y;
	private Color borderColour;
	private String text;
	private Font textFont;
	private Color fontColour;
	public boolean isHighlighted = false;
	
	public MapShape(Shape shape, int x, int y) {
		this.shape = shape;
		this.x = x;
		this.y = y;
		borderColour = Color.black;
		text = "Example";
		textFont = new Font("Serif", Font.PLAIN, 12);
		setFontColour(Color.black);
	}
	
	// Getters and setters
	public Shape getShape() {
		return shape;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Color getBorderColour() {
		return borderColour;
	}
	public void setBorderColour(Color borderColour) {
		this.borderColour = borderColour;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Font getTextFont() {
		return textFont;
	}
	public void setTextFont(Font textFont) {
		this.textFont = textFont;
	}
	public Color getFontColour() {
		return fontColour;
	}
	public void setFontColour(Color fontColour) {
		this.fontColour = fontColour;
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
