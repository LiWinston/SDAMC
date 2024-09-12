package org.sdamc.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.ClubMemberships;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;

public class ClubMembershipsMapper extends DataMapper {

    @Override
    public DomainObject find(int id) {
        return new ClubMemberships(id);
    }

    @Override
    public void update(DomainObject obj) {
        if (!(obj instanceof ClubMemberships)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        ClubMemberships membership = (ClubMemberships) obj;
        String sql = "UPDATE club_memberships SET student_id = ?, club_id = ?, role = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, membership.getStudentId());
            stmt.setInt(2, membership.getClubId());
            stmt.setString(3, membership.getRole());
            stmt.setInt(4, membership.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(DomainObject obj) {
        if (!(obj instanceof ClubMemberships)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        ClubMemberships membership = (ClubMemberships) obj;
        String sql = "INSERT INTO club_memberships (student_id, club_id, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, membership.getStudentId());
            stmt.setInt(2, membership.getClubId());
            stmt.setString(3, membership.getRole());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DomainObject obj) {
        if (!(obj instanceof ClubMemberships)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        ClubMemberships membership = (ClubMemberships) obj;
        String sql = "DELETE FROM club_memberships WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, membership.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet getRecord(int id) {
        String sql = "SELECT * FROM club_memberships WHERE id = " + id + ";";
        try (PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql)) {
            return statement.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}