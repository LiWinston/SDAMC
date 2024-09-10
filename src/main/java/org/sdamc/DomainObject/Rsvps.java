package org.sdamc.DomainObject;

import org.sdamc.UnitofWork;
import org.sdamc.DataMapper.DataMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Rsvps extends DomainObject {

    private static final String tableName = "rsvps";

    private final int id;

    private int studentId;

    private int eventId;

    private int numTickets;

    public Rsvps(int id) {
        this.id = id;
    }

    private void load() {
        ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
        try {
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

    // Getters and Setters
    public int getId() {
        return id;
    }

    public int getStudentId() {
        if (!initialed) {
            load();
        }
        return studentId;
    }

    public void setStudentId(int studentId) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.studentId = studentId;
    }

    public int getEventId() {
        if (!initialed) {
            load();
        }
        return eventId;
    }

    public void setEventId(int eventId) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.eventId = eventId;
    }

    public int getNumTickets() {
        if (!initialed) {
            load();
        }
        return numTickets;
    }

    public void setNumTickets(int numTickets) {
        if (!initialed) {
            load();
        }
        UnitofWork.getCurrent().registerDirty(this);
        this.numTickets = numTickets;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

}
