package org.sdamc.DTO;

import lombok.Data;

@Data
public class Fundings {
    private int id;

    private String clubName;

    private String applicant;

    private String description;

    private float amount;

    private String status;

    public Fundings(int id, String clubName, String applicant, String description, float amount, String status) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.clubName = clubName;
        this.applicant = applicant;
        this.status = status;
    }
}
