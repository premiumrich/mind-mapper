package net.premiumrich.io;

import java.io.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class MindMapReader {

	private static final Gson gson = new Gson();
	private JsonReader reader;
	private JsonObject data;
	
	public MindMapReader(File inFile) {
		System.out.print("Opening " + inFile.getAbsolutePath() + " ...");
		try {
			reader = new JsonReader(new FileReader(inFile));
			data = gson.fromJson(reader, JsonObject.class);
			System.out.println("Success!");
		} catch (IOException e) {
			System.out.println("File not found! " + e);
		}
	}
	
	public JsonObject getViewportData() {
		return data.get("Viewport").getAsJsonObject();
	}
	
	public JsonArray getShapesData() {
		return data.get("Shapes").getAsJsonArray();
	}
}
