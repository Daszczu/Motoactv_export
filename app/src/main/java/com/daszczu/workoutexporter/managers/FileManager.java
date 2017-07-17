package com.daszczu.workoutexporter.managers;

import com.daszczu.workoutexporter.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class FileManager {
	private String name;
	private String extension;
	private StringBuilder fileContent;
	
	public FileManager() {
		this.fileContent = new StringBuilder();
	}
	
	public FileManager(String name, String extension) {
		this();
		this.name = name;
		this.extension = extension;
		this.fileContent = new StringBuilder();
	}
	
//	public List<String> read() throws IOException {
//		if (StringUtils.areNullOrEmpty(name, extension))
//			return null;
//
//		Path path = FileSystems.getDefault().getPath(name + "." + extension);
//		List<String> fileContent = Files.readAllLines(path,Charset.forName("UTF-8"));
//		removeNonParseableLines(fileContent);
//		removeDuplicatedLines(fileContent);
//		return fileContent;
//	}
	
	private void removeNonParseableLines(List<String> lines) {
		Iterator<String> iterator = lines.iterator();
		while (iterator.hasNext()) {
			String line = iterator.next();
			if (line.isEmpty() || !Character.isDigit(line.charAt(0)))
				iterator.remove();
		}
	}
	
	private void removeDuplicatedLines(List<String> lines) {
		if (lines == null || lines.isEmpty())
			return;
		List<String> unique = new ArrayList<>(new LinkedHashSet<>(lines)); 
		lines.clear();
		lines.addAll(unique);
	}
	
	public void save(Writer writer) {
		String content = fileContent.toString();
		PrintWriter out = new PrintWriter(writer);
		out.print(content);
		out.close();
	}
	
	public void append(String s) {
		if (StringUtils.isNullOrEmpty(s))
			return;
		if (fileContent.length() != 0)
			fileContent.append("\n");
		fileContent.append(s);
	}
	
	public void append(String...strings) {
		for (String s : strings)
			append(s);
	}
}
