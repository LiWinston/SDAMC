package org.sdamc.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Events;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;

public class EventsMapper extends DataMapper {

    @Override
    public DomainObject find(int id) {
        // TODO I do not know in lazy lord pattern, if mapper should check if given id
        // exist?
        return new Events(id);
    }

    @Override
    public void update(DomainObject obj) {
        if (!(obj instanceof Events)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Events event = (Events) obj;
        String sql = "UPDATE events SET title = ?, description = ?, venue = ?, capacity = ?, club_id = ?, begin_time = ?, end_time = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getVenue());
            stmt.setInt(4, event.getCapacity());
            stmt.setInt(5, event.getClubId());
            stmt.setTimestamp(6, event.getBeginTime());
            stmt.setTimestamp(7, event.getEndTime());
            stmt.setInt(8, event.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(DomainObject obj) {
        if (!(obj instanceof Events)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Events event = (Events) obj;
        String sql = "INSERT INTO events (title, description, venue, capacity, club_id, begin_time, end_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getVenue());
            stmt.setInt(4, event.getCapacity());
            stmt.setInt(5, event.getClubId());
            stmt.setTimestamp(6, event.getBeginTime());
            stmt.setTimestamp(7, event.getEndTime());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DomainObject obj) {
        if (!(obj instanceof Events)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Events event = (Events) obj;
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, event.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet getRecord(int id) {
        String sql = "SELECT * FROM events WHERE id = " + id + ";";
        try (PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql)) {
            return statement.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
