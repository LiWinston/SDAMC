package org.sdamc.Mapper;

import org.sdamc.DTO.ClubMember;
import org.sdamc.DomainObject.FundingApplication;
import org.sdamc.DomainObject.DomainObject;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FundingApplicationMapper extends DataMapper {

    @Override
    public DomainObject find(int id) {
        return new FundingApplication(id);
    }

    public List<FundingApplication> findByClubId(int clubId) throws SQLException {
        String sql = "SELECT * FROM funding_applications WHERE club_id = ?;";
        List<FundingApplication> applications = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FundingApplication application = new FundingApplication(rs.getInt("id"),
                        rs.getString("description"), rs.getFloat("amount"),
                        rs.getInt("student_id"), rs.getInt("club_id"),
                        rs.getString("status"));
                applications.add(application);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    @Override
    public void update(DomainObject obj) {
        if (!(obj instanceof FundingApplication)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        FundingApplication application = (FundingApplication) obj;
        String sql = "UPDATE funding_applications SET description = ?, amount = ?, students_id = ?, club_id = ?," +
                " status = ?::funding_status WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, application.getDescription());
            stmt.setFloat(2, application.getAmount());
            stmt.setInt(3, application.getStudentId());
            stmt.setInt(4, application.getClubId());
            stmt.setString(5, application.getStatus());
            stmt.setInt(6, application.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(DomainObject obj) {
        if (!(obj instanceof FundingApplication)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        FundingApplication application = (FundingApplication) obj;
        String sql = "INSERT INTO funding_applications (id, description, amount, students_id, club_id, status) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, application.getId());
            stmt.setString(2, application.getDescription());
            stmt.setFloat(3, application.getAmount());
            stmt.setInt(4, application.getStudentId());
            stmt.setInt(5, application.getClubId());
            stmt.setString(6, application.getStatus());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DomainObject obj) {
        if (!(obj instanceof FundingApplication)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        FundingApplication application = (FundingApplication) obj;
        String sql = "DELETE FROM funding_applications WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, application.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet getRecord(int id) throws SQLException {
        String sql = "SELECT * FROM funding_applications WHERE id=" + id + ";";
        PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql);
        return statement.executeQuery();
    }

    @Override
    public int getNewId() {
        String sql = "SELECT nextval('funding_applications_id_seq');";
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