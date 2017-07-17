package com.daszczu.workoutexporter.dto;

import java.io.InputStream;

public class WorkoutToUpload {
	private String filename;
	private InputStream inputStream;

	public WorkoutToUpload(String filename, InputStream inputStream) {
		this.filename = filename;
		this.inputStream = inputStream;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}
