package org.sdamc.DTO;

import lombok.Data;

@Data
public class ClubMember {
    private int id;

    private String name;

    private String email;

    private int clubid;

    private String role;

    public ClubMember(int id, String name, String email, int clubid, String role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.clubid = clubid;
        this.role = role;
    }
}
