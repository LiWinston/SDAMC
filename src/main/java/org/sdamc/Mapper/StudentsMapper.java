package org.sdamc.Mapper;

import lombok.extern.slf4j.Slf4j;
import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Students;
import org.sdamc.Utils.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
CREATE TABLE students (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(30) NOT NULL
);
*/
@Slf4j
public class StudentsMapper extends DataMapper {

    @Override
    public DomainObject find(int id) {
        return new Students(id);
    }

    public DomainObject findById(int id) {
        String sql = "SELECT id, name, email, password FROM students WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                log.info("Found student {}{}{}", rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                var st = new Students(rs.getInt("id"));
                st.setName(rs.getString("name"));
                st.setEmail(rs.getString("email"));
                st.setPassword(rs.getString("password"));
                return st;
            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Students findByName(String name) {
        String sql = "SELECT id, name, email, password FROM students WHERE name = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                log.info("Found student {}{}{}", rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                var st = new Students(rs.getInt("id"));
                st.setName(rs.getString("name"));
                st.setEmail(rs.getString("email"));
                st.setPassword(rs.getString("password"));
                return st;
            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Students findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM students WHERE email = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                log.info("Found student {}{}{}", rs.getInt("id"), rs.getString("name"), rs.getString("email"));
                var st = new Students(rs.getInt("id"));
                st.setName(rs.getString("name"));
                st.setEmail(rs.getString("email"));
                st.setPassword(rs.getString("password"));
                return st;
            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        String sql = "INSERT INTO students (id, name, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, getNewId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPassword());
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