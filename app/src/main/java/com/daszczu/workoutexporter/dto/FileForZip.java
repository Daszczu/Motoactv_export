package com.daszczu.workoutexporter.dto;
import java.util.ArrayList;
import java.util.List;

public class FileForZip {
    private List<String> filenames;
    private Long size;

    public FileForZip() {
        this.filenames = new ArrayList<>();
        this.size = 0L;
    }

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}