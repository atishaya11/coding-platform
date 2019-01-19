package com.dscjss.codingplatform.problems.dto;

import org.springframework.web.multipart.MultipartFile;

public class UploadTestCaseDto {

    private MultipartFile inputFile;
    private MultipartFile outputFile;
    private boolean sample;
    private String tag;

    public MultipartFile getInputFile() {
        return inputFile;
    }

    public void setInputFile(MultipartFile inputFile) {
        this.inputFile = inputFile;
    }

    public MultipartFile getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(MultipartFile outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isSample() {
        return sample;
    }

    public void setSample(boolean sample) {
        this.sample = sample;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
