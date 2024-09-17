package org.sdamc.DomainObject;

import org.sdamc.Mapper.DataMapper;
import org.sdamc.UnitofWork;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FundingApplication extends DomainObject {

    public static final String tableName = "funding_applications";

    private final int id;

    private String description;

    private float amount;

    private int studentId;

    private int clubId;

    private String status;

    // Constructor
    public FundingApplication(int id) {
        this.id = id;
    }

    public FundingApplication(int id, String description, float amount, int studentId, int clubId, String status) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.studentId = studentId;
        this.clubId = clubId;
        this.status = status;
        this.initialed = true;
    }

    private void load() {
        try {
            ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
            if (result.next()) {
                description = result.getString("description");
                amount = result.getFloat("amount");
                studentId = result.getInt("student_id");
                clubId = result.getInt("club_id");
                status = result.getString("funding_status");
                initialed = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static FundingApplication insert(String description, float amount, int studentId, int clubId, String status) {
        FundingApplication application = new FundingApplication(DataMapper.GetMapper(tableName).getNewId());
        application.description = description;
        application.amount = amount;
        application.studentId = studentId;
        application.clubId = clubId;
        application.status = status;
        application.insert = true;
        application.initialed = true;
        UnitofWork.getCurrent().registerNew(application);
        return application;
    }

    public void delete() {
        UnitofWork.getCurrent().registerDeleted(this);
        deleted = true;
    }

    // Getters and Setters
    public String getDescription() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return description;
    }

    public float getAmount() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return amount;
    }

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

    public String getStatus() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return status;
    }

    public void setDescription(String description) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.description = description;
    }

    public void setAmount(float amount) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.amount = amount;
    }

    public void setStudentId(int studentId) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.studentId = studentId;
    }

    public void setClubId(int clubId) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.clubId = clubId;
    }

    public void setStatus(String status) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.status = status;
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
