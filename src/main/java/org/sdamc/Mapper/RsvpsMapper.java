package org.sdamc.Mapper;

import org.sdamc.DTO.Result;
import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Rsvps;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            throw new RuntimeException("Error inserting RSVP", e);
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

    @Override
    public int getNewId() {
        String sql = "SELECT nextval('rsvps_id_seq');";
        try {
            PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Result<List<Rsvps>> findByEventId(int eventId) {
        String sql = "SELECT * FROM rsvps WHERE event_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet result = stmt.executeQuery();

            List<Rsvps> res = new ArrayList<>();
            while (result.next()) {
                Rsvps rsvp = new Rsvps(result.getInt("id"));
                rsvp.setStudentId(result.getInt("student_id"));
                rsvp.setEventId(result.getInt("event_id"));
                rsvp.setNumTickets(result.getInt("num_tickets"));
                res.add(rsvp);
            }
            return Result.success(res);
        }
        catch (SQLException e) {
            return Result.error("Failed to find rsvps by event id " + eventId + ": " + e.getMessage());
        }
    }

    // 在 RsvpsMapper 类中添加以下方法

    public List<Rsvps> findByStudentId(int studentId) {
        List<Rsvps> rsvps = new ArrayList<>();
        String sql = "SELECT * FROM rsvps WHERE student_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Rsvps rsvp = new Rsvps(rs.getInt("id"));
                rsvp.setStudentId(rs.getInt("student_id"));
                rsvp.setEventId(rs.getInt("event_id"));
                rsvp.setNumTickets(rs.getInt("num_tickets"));
                rsvps.add(rsvp);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return rsvps;
    }

    public Rsvps findByStudentIdAndEventId(int studentId, int eventId) {
        String sql = "SELECT * FROM rsvps WHERE student_id = ? AND event_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Rsvps rsvp = new Rsvps(rs.getInt("id"));
                rsvp.setStudentId(rs.getInt("student_id"));
                rsvp.setEventId(rs.getInt("event_id"));
                rsvp.setNumTickets(rs.getInt("num_tickets"));
                return rsvp;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}