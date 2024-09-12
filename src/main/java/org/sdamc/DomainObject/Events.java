package org.sdamc.DomainObject;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sdamc.UnitofWork;
import org.sdamc.Mapper.DataMapper;

public class Events extends DomainObject {

    public static final String tableName = "events";

    private final int id;

    private String title;

    private String description;

    private String venue;

    private int capacity;

    private int clubId;

    private Timestamp beginTime;

    private Timestamp endTime;

    public Events(int id) {
        this.id = id;
    }

    private void load() {
        try {
            ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
            if (result.next()) {
                title = result.getString("title");
                description = result.getString("description");
                venue = result.getString("venue");
                capacity = result.getInt("capacity");
                clubId = result.getInt("club_id");
                beginTime = result.getTimestamp("begin_time");
                endTime = result.getTimestamp("end_time");
                initialed = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        if (!initialed) {
            load();
        }
        return title;
    }

    public void setTitle(String title) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.title = title;
    }

    public String getDescription() {
        if (!initialed) {
            load();
        }
        return description;
    }

    public void setDescription(String description) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.description = description;
    }

    public String getVenue() {
        if (!initialed) {
            load();
        }
        return venue;
    }

    public void setVenue(String venue) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.venue = venue;
    }

    public Integer getCapacity() {
        if (!initialed) {
            load();
        }
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.capacity = capacity;
    }

    public int getClubId() {
        if (!initialed) {
            load();
        }
        return clubId;
    }

    public void setClubId(int clubId) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.clubId = clubId;
    }

    public Timestamp getBeginTime() {
        if (!initialed) {
            load();
        }
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.beginTime = beginTime;
    }

    public Timestamp getEndTime() {
        if (!initialed) {
            load();
        }
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.endTime = endTime;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

}
