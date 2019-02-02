package com.dscjss.codingplatform.contests.model;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Lob;

@Embeddable
public class ContestBody {

    @Lob
    private String description;

    @Lob
    private String prizes;

    @Lob
    private String rules;

    @Lob
    private String scoring;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrizes() {
        return prizes;
    }

    public void setPrizes(String prizes) {
        this.prizes = prizes;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getScoring() {
        return scoring;
    }

    public void setScoring(String scoring) {
        this.scoring = scoring;
    }
}
