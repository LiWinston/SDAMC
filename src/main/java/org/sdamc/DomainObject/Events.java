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

    public void increaseCapacity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.capacity += amount;
        UnitofWork.getCurrent().registerDirty(this);
    }

    public void decreaseCapacity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.capacity < amount) {
            throw new IllegalArgumentException("Not enough capacity");
        }
        this.capacity -= amount;
        UnitofWork.getCurrent().registerDirty(this);
    }

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

    public static Events insert(String title, String description, String venue, int capacity, int clubId,
            Timestamp beginTime, Timestamp endTime) {
        Events event = new Events(DataMapper.GetMapper(tableName).getNewId());
        event.title = title;
        event.description = description;
        event.venue = venue;
        event.capacity = capacity;
        event.clubId = clubId;
        event.beginTime = beginTime;
        event.endTime = endTime;
        event.insert = true;
        event.initialed = true;
        UnitofWork.getCurrent().registerNew(event);
        return event;
    }

    public void delete() {
        UnitofWork.getCurrent().registerDeleted(this);
        deleted = true;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return title;
    }

    public void setTitle(String title) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.title = title;
    }

    public String getDescription() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return description;
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

    public String getVenue() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return venue;
    }

    public void setVenue(String venue) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.venue = venue;
    }

    public Integer getCapacity() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.capacity = capacity;
    }

    public int getClubId() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return clubId;
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

    public Timestamp getBeginTime() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.beginTime = beginTime;
    }

    public Timestamp getEndTime() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.endTime = endTime;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

}
