package org.sdamc.DTO;

import lombok.Data;

@Data
public class ClubMember {
    private int id;

    private int studentId;

    private String name;

    private String email;

    private int clubid;

    private String role;

    public ClubMember(int id, int studentId, String name, String email, int clubid, String role){
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.clubid = clubid;
        this.role = role;
    }
}
