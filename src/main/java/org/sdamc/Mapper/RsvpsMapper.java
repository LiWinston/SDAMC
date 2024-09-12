package org.sdamc.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Rsvps;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;

public class RsvpsMapper extends DataMapper {

    @Override
    public DomainObject find(int id) {
        return new Rsvps(id);
    }

    @Override
    public void update(DomainObject obj) {
        if (!(obj instanceof Rsvps)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Rsvps rsvp = (Rsvps) obj;
        String sql = "UPDATE rsvps SET student_id = ?, event_id = ?, num_tickets = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, rsvp.getStudentId());
            stmt.setInt(2, rsvp.getEventId());
            stmt.setInt(3, rsvp.getNumTickets());
            stmt.setInt(4, rsvp.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(DomainObject obj) {
        if (!(obj instanceof Rsvps)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Rsvps rsvp = (Rsvps) obj;
        String sql = "INSERT INTO rsvps (student_id, event_id, num_tickets) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, rsvp.getStudentId());
            stmt.setInt(2, rsvp.getEventId());
            stmt.setInt(3, rsvp.getNumTickets());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DomainObject obj) {
        if (!(obj instanceof Rsvps)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Rsvps rsvp = (Rsvps) obj;
        String sql = "DELETE FROM rsvps WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, rsvp.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet getRecord(int id) throws SQLException {
        String sql = "SELECT * FROM rsvps WHERE id = " + id + ";";
        PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql);
        return statement.executeQuery();
    }

}