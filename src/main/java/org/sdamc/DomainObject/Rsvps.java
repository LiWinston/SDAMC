package org.sdamc.DomainObject;

import org.sdamc.Mapper.DataMapper;
import org.sdamc.UnitofWork;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Rsvps extends DomainObject {

    public static final String tableName = "rsvps";

    private final int id;

    private int studentId;

    private int eventId;

    private int numTickets;

    public Rsvps(int id) {
        this.id = id;
    }

    private void load() {
        try {
            ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
            if (result.next()) {
                studentId = result.getInt("student_id");
                eventId = result.getInt("event_id");
                numTickets = result.getInt("num_tickets");
                initialed = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Rsvps insert(int studentId, int eventId, int numTickets) {
        Rsvps rsvp = new Rsvps(DataMapper.GetMapper(tableName).getNewId());
        rsvp.studentId = studentId;
        rsvp.eventId = eventId;
        rsvp.numTickets = numTickets;
        rsvp.insert = true;
        rsvp.initialed = true;
        UnitofWork.getCurrent().registerNew(rsvp);
        return rsvp;
    }

    public void delete() {
        UnitofWork.getCurrent().registerDeleted(this);
        deleted = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getStudentId() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return studentId;
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

    public int getEventId() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return eventId;
    }

    public void setEventId(int eventId) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.eventId = eventId;
    }

    public int getNumTickets() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return numTickets;
    }

    public void setNumTickets(int numTickets) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.numTickets = numTickets;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

}
