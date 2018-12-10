package net.premiumrich.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;

import com.google.gson.JsonObject;

public abstract class MapShape {
	
	private Shape shape;
	private int x, y;
	private int borderWidth;
	private Color borderColour;
	private String text;
	private Font textFont;
	private Color fontColour;
	public boolean isHighlighted = false;
	
	public MapShape(Shape shape) {
		this.shape = shape;
		this.x = shape.getBounds().x;
		this.y = shape.getBounds().y;
		// Defaults
		setBorderWidth(3);
		borderColour = Color.black;
		text = "Example";
		textFont = new Font("Serif", Font.PLAIN, 12);
		fontColour = Color.black;
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
	public int getBorderWidth() {
		return borderWidth;
	}
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
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
		thisShape.addProperty("Border width", borderWidth);
		thisShape.addProperty("Border colour", "#"+Integer.toHexString(borderColour.getRGB()).substring(2));
		thisShape.addProperty("Text", text);
		thisShape.addProperty("Text font name", textFont.getName());
		thisShape.addProperty("Text font style", textFont.getStyle());
		thisShape.addProperty("Text font size", textFont.getSize());
		thisShape.addProperty("Font colour", "#"+Integer.toHexString(fontColour.getRGB()).substring(2));
		return thisShape;
	}

}
