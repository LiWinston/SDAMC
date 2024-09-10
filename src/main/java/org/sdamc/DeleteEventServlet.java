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

@WebServlet(name = "deleteEventServlet", value = "/deleteEvent")
public class DeleteEventServlet extends HttpServlet {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String eventId = request.getParameter("id");
        String clubId = request.getParameter("clubId");

        String query = "DELETE FROM events WHERE id = ? AND club_id = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Integer.parseInt(eventId));
            stmt.setInt(2, Integer.parseInt(clubId));
            stmt.executeUpdate();

            response.sendRedirect(request.getContextPath() + "/events");

        }
        catch (SQLException e) {
            throw new ServletException(e);
        }
    }

}
