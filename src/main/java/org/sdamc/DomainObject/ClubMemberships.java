package org.sdamc.DomainObject;

import lombok.NoArgsConstructor;
import org.sdamc.Mapper.DataMapper;
import org.sdamc.UnitofWork;

import java.sql.ResultSet;
import java.sql.SQLException;

@NoArgsConstructor(force = true)
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
                role = result.getString("role");
                initialed = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ClubMemberships insert(int studentId, int clubId, String role) {
        ClubMemberships membership = new ClubMemberships(DataMapper.GetMapper(tableName).getNewId());
        membership.studentId = studentId;
        membership.clubId = clubId;
        membership.role = role;
        membership.insert = true;
        membership.initialed = true;
        UnitofWork.getCurrent().registerNew(membership);
        return membership;
    }

    public void delete() {
        UnitofWork.getCurrent().registerDeleted(this);
        deleted = true;
    }

    // Getters and Setters
    public int getStudentId() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return studentId;
    }

    public int getClubId() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return clubId;
    }

    public String getRole() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return role;
    }

    public void setRole(String role) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.role = role;
    }

    @Override
    public String getTableName() {
        assert (!deleted);
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
