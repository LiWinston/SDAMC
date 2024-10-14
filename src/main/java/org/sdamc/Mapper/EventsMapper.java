package org.sdamc.Mapper;

import lombok.extern.slf4j.Slf4j;
import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Events;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class EventsMapper extends DataMapper {

    @Override
    public DomainObject find(int id) {
        // TODO I do not know in lazy load pattern, if mapper should check if given id
        // exist?
        return new Events(id);
    }

    public List<Events> findAll() {
        List<Events> events = new java.util.ArrayList<>();
        String sql = "SELECT * FROM events";
        try (PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Events event = new Events(rs.getInt("id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setVenue(rs.getString("venue"));
                event.setCapacity(rs.getInt("capacity"));
                event.setClubId(rs.getInt("club_id"));
                event.setBeginTime(rs.getTimestamp("begin_time"));
                event.setEndTime(rs.getTimestamp("end_time"));
                events.add(event);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (events.isEmpty()) {
            log.info("No events found");
        }
        return events;
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

    public void update(Events event, Map<String, String> updates) throws SQLException {
        // 构建动态 SQL
        StringBuilder sqlBuilder = new StringBuilder("UPDATE events SET ");
        List<Object> params = new ArrayList<>();

        if (updates.containsKey("title")) {
            sqlBuilder.append("title = ?, ");
            params.add(updates.get("title"));
        }
        if (updates.containsKey("description")) {
            sqlBuilder.append("description = ?, ");
            params.add(updates.get("description"));
        }
        if (updates.containsKey("venue")) {
            sqlBuilder.append("venue = ?, ");
            params.add(updates.get("venue"));
        }
        if (updates.containsKey("capacity")) {
            sqlBuilder.append("capacity = ?, ");
            params.add(Integer.parseInt(updates.get("capacity")));
        }
        if (updates.containsKey("clubId")) {
            sqlBuilder.append("club_id = ?, ");
            params.add(Integer.parseInt(updates.get("clubId")));
        }
        if (updates.containsKey("beginTime")) {
            sqlBuilder.append("begin_time = ?, ");
            String beginTimeStr = updates.get("beginTime").replace("T", " ") + ":00";
            params.add(Timestamp.valueOf(beginTimeStr));
        }
        if (updates.containsKey("endTime")) {
            sqlBuilder.append("end_time = ?, ");
            if (updates.get("endTime") != null && !updates.get("endTime").isBlank()) {
                String endTimeStr = updates.get("endTime").replace("T", " ") + ":00";
                params.add(Timestamp.valueOf(endTimeStr));
            } else {
                params.add(null);
            }
        }

        // 去掉末尾的逗号，并加上 WHERE 条件
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 2);
        sqlBuilder.append(" WHERE id = ?");

        params.add(event.getId());

        String sql = sqlBuilder.toString();

        // 执行 SQL 语句
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public void insert(DomainObject obj) {
        if (!(obj instanceof Events)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Events event = (Events) obj;
        String sql = "INSERT INTO events (id,title, description, venue, capacity, club_id, begin_time, end_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, event.getId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setString(4, event.getVenue());
            stmt.setInt(5, event.getCapacity());
            stmt.setInt(6, event.getClubId());
            stmt.setTimestamp(7, event.getBeginTime());
            stmt.setTimestamp(8, event.getEndTime());
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

    // 删除事件CascadeLy
    public void deleteByEventId(int eventId) {
        // 若关联表中有数据，级联删除
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet getRecord(int id) throws SQLException {
        String sql = "SELECT * FROM events WHERE id = " + id + " FOR UPDATE;";
        PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql);
        return statement.executeQuery();
    }

    public void deleteById(int eventId) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNewId() {
        String sql = "SELECT nextval('events_id_seq');";
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

}