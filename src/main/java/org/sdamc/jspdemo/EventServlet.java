package org.sdamc.jspdemo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/clubs/*/events")
public class EventServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/yourdb";
    private static final String JDBC_USER = "youruser";
    private static final String JDBC_PASSWORD = "yourpassword";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取社团ID和事件ID
        String clubId = request.getPathInfo().split("/")[1];
        String eventId = request.getParameter("id");

        // 根据是否有事件ID决定是查看单个事件还是所有事件
        String query = eventId != null
                ? "SELECT * FROM events WHERE id = ? AND club_id = ?"
                : "SELECT * FROM events WHERE club_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (eventId != null) {
                stmt.setInt(1, Integer.parseInt(eventId));
                stmt.setInt(2, Integer.parseInt(clubId));
            } else {
                stmt.setInt(1, Integer.parseInt(clubId));
            }

            ResultSet rs = stmt.executeQuery();
            request.setAttribute("events", rs);
            request.getRequestDispatcher("/WEB-INF/jsp/events.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 创建或更新事件的逻辑
        String eventId = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String venue = request.getParameter("venue");
        String capacity = request.getParameter("capacity");
        String clubId = request.getPathInfo().split("/")[1];

        String query = eventId != null
                ? "UPDATE events SET title = ?, description = ?, venue = ?, capacity = ? WHERE id = ? AND club_id = ?"
                : "INSERT INTO events (title, description, venue, capacity, club_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, venue);
            stmt.setInt(4, capacity != null ? Integer.parseInt(capacity) : 0);

            if (eventId != null) {
                stmt.setInt(5, Integer.parseInt(eventId));
                stmt.setInt(6, Integer.parseInt(clubId));
            } else {
                stmt.setInt(5, Integer.parseInt(clubId));
            }

            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/clubs/" + clubId + "/events");

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 删除事件的逻辑
        String eventId = request.getParameter("id");
        String clubId = request.getPathInfo().split("/")[1];

        String query = "DELETE FROM events WHERE id = ? AND club_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Integer.parseInt(eventId));
            stmt.setInt(2, Integer.parseInt(clubId));
            stmt.executeUpdate();

            response.sendRedirect(request.getContextPath() + "/clubs/" + clubId + "/events");

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
