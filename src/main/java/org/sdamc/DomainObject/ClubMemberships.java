package org.sdamc.DomainObject;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sdamc.UnitofWork;
import org.sdamc.Mapper.DataMapper;

public class ClubMemberships extends DomainObject {

    public static final String tableName = "club_memberships";

    private final int id;

    private int studentId;

    private int clubId;

    private String role;

    // Constructor
    public ClubMemberships(int id) {
        this.id = id;
    }

    private void load() {
        try {
            ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
            if (result.next()) {
                studentId = result.getInt("student_id");
                clubId = result.getInt("club_id");
                role = result.getString("member_role");
                initialed = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getters and Setters
    public int getStudentId() {
        if (!initialed) {
            load();
        }
        return studentId;
    }

    public int getClubId() {
        if (!initialed) {
            load();
        }
        return clubId;
    }

    public String getRole() {
        if (!initialed) {
            load();
        }
        return role;
    }

    public void setRole(String role) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.role = role;
    }

    @Override
    public String getTableName() {
        if (!initialed) {
            load();
        }
        return tableName;
    }

    @Override
    public int getId() {
        return id;
    }

}
