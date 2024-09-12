package org.sdamc.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Students;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;

public class StudentsMapper extends DataMapper {

    @Override
    public DomainObject find(int id) {
        return new Students(id);
    }

    @Override
    public void update(DomainObject obj) {
        if (!(obj instanceof Students)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Students student = (Students) obj;
        String sql = "UPDATE students SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setInt(3, student.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(DomainObject obj) {
        if (!(obj instanceof Students)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Students student = (Students) obj;
        String sql = "INSERT INTO students (name, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(0, student.getId());
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DomainObject obj) {
        if (!(obj instanceof Students)) {
            throw new IllegalArgumentException("Invalid object type");
        }
        Students student = (Students) obj;
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, student.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet getRecord(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = " + id + ";";
        PreparedStatement statement = DatabaseUtil.getConnection().prepareStatement(sql);
        return statement.executeQuery();
    }

    @Override
    public int getNewId() {
        String sql = "SELECT nextval('students_id_seq');";
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