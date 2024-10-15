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
                FundingApplication application = new FundingApplication(rs.getInt("id"), rs.getString("description"),
                        rs.getFloat("amount"), rs.getInt("student_id"), rs.getInt("club_id"), rs.getString("status"),
                        rs.getString("semester"), rs.getInt("version"));
                applications.add(application);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public List<FundingApplication> getAll() throws SQLException {
        String sql = "SELECT * FROM funding_applications " + "WHERE status = 'submitted' or status = 'in_review';";
        List<FundingApplication> applications = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FundingApplication application = new FundingApplication(rs.getInt("id"), rs.getString("description"),
                        rs.getFloat("amount"), rs.getInt("student_id"), rs.getInt("club_id"), rs.getString("status"),
                        rs.getString("semester"), rs.getInt("version"));
                applications.add(application);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public void optimisticUpdate(DomainObject obj) {
        if (!(obj instanceof FundingApplication)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        FundingApplication application = (FundingApplication) obj;

        String checkSql = "SELECT COUNT(*) FROM funding_applications WHERE id = ? AND status = ?::funding_status";
        try (PreparedStatement checkStmt = DatabaseUtil.getConnection().prepareStatement(checkSql)) {
            checkStmt.setInt(1, application.getId());
            checkStmt.setString(2, "submitted");

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    return;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String sql = "UPDATE funding_applications SET description = ?, amount = ?, student_id = ?, club_id = ?,"
                + " status = ?::funding_status, semester = '2024_S2', version = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, application.getDescription());
            stmt.setFloat(2, application.getAmount());
            stmt.setInt(3, application.getStudentId());
            stmt.setInt(4, application.getClubId());
            stmt.setString(5, application.getStatus());
            stmt.setInt(6, application.getVersion());
            stmt.setInt(7, application.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(DomainObject obj) {
        if (!(obj instanceof FundingApplication)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        FundingApplication application = (FundingApplication) obj;
        String sql = "UPDATE funding_applications " + "SET description = ?, amount = ?, student_id = ?, club_id = ?, "
                + "status = ?::funding_status, semester = ?, version = version + 1 " + "WHERE id = ? AND version = ?";

        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, application.getDescription());
            stmt.setFloat(2, application.getAmount());
            stmt.setInt(3, application.getStudentId());
            stmt.setInt(4, application.getClubId());
            stmt.setString(5, application.getStatus());
            stmt.setString(6, "2024_S2"); // 使用传入的学期
            stmt.setInt(7, application.getId());
            stmt.setInt(8, application.getVersion()); // 校验传入的 version

            int affectedRows = stmt.executeUpdate();
            try {
                if (affectedRows == 0) {
                    // 如果没有任何记录更新，说明版本冲突，抛出异常或提示用户
                    throw new SQLException("The funding application was modified by another user.");
                }
            }
            catch (SQLException ignored) {
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            // 可以根据业务需求进一步处理异常
        }
    }

    @Override
    public void insert(DomainObject obj) {
        if (!(obj instanceof FundingApplication)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        FundingApplication application = (FundingApplication) obj;

        String checkSql = "SELECT COUNT(*) FROM funding_applications WHERE club_id = ? AND semester = ?";
        try (PreparedStatement checkStmt = DatabaseUtil.getConnection().prepareStatement(checkSql)) {
            checkStmt.setInt(1, application.getClubId());
            checkStmt.setString(2, application.getSemester());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // 如果记录已存在，则抛出异常或返回错误信息
                    throw new IllegalStateException("Funding application already existed");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String sql = "INSERT INTO funding_applications (id, description, amount, student_id, club_id, status, semester, version) "
                + "VALUES (?, ?, ?, ?, ?, ?::funding_status, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, application.getId());
            stmt.setString(2, application.getDescription());
            stmt.setFloat(3, application.getAmount());
            stmt.setInt(4, application.getStudentId());
            stmt.setInt(5, application.getClubId());
            stmt.setString(6, application.getStatus());
            stmt.setString(7, application.getSemester());
            stmt.setInt(8, application.getVersion());
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