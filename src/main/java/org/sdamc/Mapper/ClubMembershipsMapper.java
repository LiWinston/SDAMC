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
        String sql = "UPDATE club_memberships SET student_id = ?, club_id = ?, role = ?::member_role WHERE id = ?";
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
        String sql = "INSERT INTO club_memberships (id, student_id, club_id, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, membership.getId());
            stmt.setInt(2, membership.getStudentId());
            stmt.setInt(3, membership.getClubId());
            stmt.setString(4, membership.getRole());
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
    public ResultSet getRecord(int id) throws SQLException {
        String sql = "SELECT * FROM club_memberships WHERE id = " + id + ";";
        PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql);
        return statement.executeQuery();
    }

    @Override
    public int getNewId() {
        String sql = "SELECT nextval('club_memberships_id_seq');";
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