package com.dscjss.codingplatform.contests.dto;

import com.dscjss.codingplatform.contests.model.Contest;

import java.util.List;

public class ProblemsUpdateForm {

    private List<ContestProblemDto> problemDetailsList;

    public List<ContestProblemDto> getProblemDetailsList() {
        return problemDetailsList;
    }

    public void setProblemDetailsList(List<ContestProblemDto> problemDetailsList) {
        this.problemDetailsList = problemDetailsList;
    }
}


