package net.premiumrich.io;

import java.io.*;

import com.google.gson.*;

public class MindMapWriter {
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private File outFile;
	private JsonObject output;
	
	public MindMapWriter(File outFile) {
		this.outFile = outFile;
		output = new JsonObject();
	}
	
	public void add(String property, Object obj) {
		output.add(property, gson.toJsonTree(obj));
	}
	
	public void save() {
		System.out.print("Saving to " + outFile.getAbsolutePath() + " ... ");
		try {
			PrintWriter p = new PrintWriter(new FileWriter(outFile));
			p.write(gson.toJson(output));
			p.close();
			System.out.println("Success!");
		} catch (IOException e) {
			System.out.println("File not found! " + e);
		}
	}
	
}
