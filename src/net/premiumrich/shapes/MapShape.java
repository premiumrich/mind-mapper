package net.premiumrich.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;

import javax.swing.JTextField;

import com.google.gson.JsonObject;

public abstract class MapShape {
	
	protected Shape shape;
	protected int x, y;
	private int borderWidth;
	private Color borderColour;
	private JTextField textField;
	public boolean isHighlighted = false;
	
	public MapShape(Shape shape) {
		this.shape = shape;
		this.x = shape.getBounds().x;
		this.y = shape.getBounds().y;
		// Defaults
		setBorderWidth(3);
		borderColour = Color.black;
		textField = new JTextField("Example");
		textField.setFont(new Font("Serif", Font.PLAIN, 12));
		textField.setForeground(Color.black);
		textField.setBorder(null);		// Remove border
		textField.setOpaque(false);
		textField.setHorizontalAlignment(JTextField.CENTER);
		updateTextFieldBounds();
	}
	
	public void updateTextFieldBounds() {
		textField.setBounds(x + shape.getBounds().width/2 - 100, y + shape.getBounds().height/2 - 50, 200, 100);
	}
	
	// Getters and setters
	public Shape getShape() {
		return shape;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public abstract void setNewCoordinates(int x, int y);	// Force subclasses (shapes) to override this
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
	public JTextField getTextField() {
		return textField;
	}
	public JsonObject getAsJsonObj() {
		JsonObject thisShape = new JsonObject();
		thisShape.addProperty("Type", this.getClass().getName());
		thisShape.addProperty("X", x);
		thisShape.addProperty("Y", y);
		thisShape.addProperty("Width", shape.getBounds().width);
		thisShape.addProperty("Height", shape.getBounds().height);
		thisShape.addProperty("Border width", borderWidth);
		thisShape.addProperty("Border colour", "#"+Integer.toHexString(borderColour.getRGB()).substring(2));
		thisShape.addProperty("Text", textField.getText());
		thisShape.addProperty("Text font name", textField.getFont().getName());
		thisShape.addProperty("Text font style", textField.getFont().getStyle());
		thisShape.addProperty("Text font size", textField.getFont().getSize());
		thisShape.addProperty("Font colour", "#"+Integer.toHexString(textField.getForeground().getRGB()).substring(2));
		return thisShape;
	}

}
