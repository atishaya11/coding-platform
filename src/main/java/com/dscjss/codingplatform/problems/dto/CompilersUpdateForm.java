package com.dscjss.codingplatform.problems.dto;

import com.dscjss.codingplatform.compilers.dto.CompilerDto;

import java.util.List;

public class CompilersUpdateForm {

    private List<CompilerDto> compilers;

    public List<CompilerDto> getCompilers() {
        return compilers;
    }

    public void setCompilers(List<CompilerDto> compilers) {
        this.compilers = compilers;
    }
}
