package org.sdamc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sdamc.Pojo.Event;
import org.sdamc.Utils.DatabaseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "createEventServlet", value = "/createEvent")
public class CreateEventServlet extends HttpServlet {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 创建或更新事件的逻辑
        String eventId = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String venue = request.getParameter("venue");
        String capacity = request.getParameter("capacity");
        // String clubId = request.getPathInfo().split("/")[1];
        String clubId = "1";

        String query = "INSERT INTO events (title, description, venue, capacity, club_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, venue);
            stmt.setInt(4, capacity != null ? Integer.parseInt(capacity) : 0);

            if (eventId != null) {
                stmt.setInt(5, Integer.parseInt(eventId));
                stmt.setInt(6, Integer.parseInt(clubId));
            }
            else {
                stmt.setInt(5, Integer.parseInt(clubId));
            }

            stmt.executeUpdate();
            response.sendRedirect(request.getContextPath() + "/events");

        }
        catch (SQLException e) {
            throw new ServletException(e);
        }
    }

}
