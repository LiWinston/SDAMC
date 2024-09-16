package org.sdamc.Mapper;

import lombok.extern.java.Log;
import org.sdamc.DTO.ClubMember;
import org.sdamc.DomainObject.ClubMemberships;
import org.sdamc.DomainObject.Clubs;
import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Students;
import org.sdamc.Utils.DatabaseUtil;

import java.io.Console;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("student id: " + membership.getStudentId());
            System.out.println("club id: " + membership.getClubId());
            System.out.println("role: " + membership.getRole());
            System.out.println("id: " + membership.getId());
            stmt.setInt(1, membership.getStudentId());
            stmt.setInt(2, membership.getClubId());
            stmt.setString(3, membership.getRole());
            stmt.setInt(4, membership.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database update failed", e);
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

    public boolean isAdmin(int studentId, int clubId) {
        String sql = "SELECT * FROM club_memberships WHERE student_id = ? AND club_id = ? AND role = 'admin'";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, clubId);
            ResultSet result = stmt.executeQuery();
            return result.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMember(int studentId, int clubId) {
        String sql = "SELECT * FROM club_memberships WHERE student_id = ? AND club_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, clubId);
            ResultSet result = stmt.executeQuery();
            return result.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Clubs> findClubsAdminedByStudent(int studentId) {
        String sql = "SELECT c.id, c.name FROM clubs c " + "JOIN club_memberships cm ON c.id = cm.club_id "
                + "WHERE cm.student_id = ? AND cm.role = 'admin'";

        List<Clubs> clubs = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Clubs club = new Clubs(rs.getInt("id"));
                club.setName(rs.getString("name"));
                clubs.add(club);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }

    public List<Clubs> findClubsSuperAdminedByStudent(int studentId) {
        String sql = "SELECT c.id, c.name FROM clubs c " + "JOIN club_memberships cm ON c.id = cm.club_id "
                + "WHERE cm.student_id = ? AND cm.role = 'super_admin'";

        List<Clubs> clubs = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Clubs club = new Clubs(rs.getInt("id"));
                club.setName(rs.getString("name"));
                clubs.add(club);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }

    public List<ClubMember> getClubMembers(int clubId) {
        String sql = "SELECT cm.id, cm.student_id, s.name, s.email, cm.club_id, cm.role FROM students s "
                + "JOIN club_memberships cm ON s.id = cm.student_id "
                + "WHERE cm.club_id = ?";

        List<ClubMember> members = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ClubMember member = new ClubMember(rs.getInt("id"), rs.getInt("student_id"),
                        rs.getString("name"), rs.getString("email"),
                        rs.getInt("club_id"), rs.getString("role"));
                members.add(member);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

}