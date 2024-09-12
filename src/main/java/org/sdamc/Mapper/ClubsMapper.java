package org.sdamc.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Clubs;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;

public class ClubsMapper extends DataMapper {

    @Override
    public DomainObject find(int id) {
        return new Clubs(id);
    }

    @Override
    public void update(DomainObject obj) {
        if (!(obj instanceof Clubs)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Clubs club = (Clubs) obj;
        String sql = "UPDATE clubs SET name = ?, description = ?, location = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            stmt.setString(3, club.getLocation());
            stmt.setInt(4, club.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(DomainObject obj) {
        if (!(obj instanceof Clubs)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Clubs club = (Clubs) obj;
        String sql = "INSERT INTO clubs (id, name, description, location) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, club.getId());
            stmt.setString(2, club.getName());
            stmt.setString(3, club.getDescription());
            stmt.setString(4, club.getLocation());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DomainObject obj) {
        if (!(obj instanceof Clubs)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Clubs club = (Clubs) obj;
        String sql = "DELETE FROM clubs WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, club.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet getRecord(int id) throws SQLException {
        String sql = "SELECT * FROM clubs WHERE id=" + id + ";";
        PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql);
        return statement.executeQuery();
    }

    @Override
    public int getNewId() {
        String sql = "SELECT nextval('clubs_id_seq');";
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