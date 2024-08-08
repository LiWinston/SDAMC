package org.sdamc.jspdemo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sdamc.jspdemo.Pojo.Event;
import org.sdamc.jspdemo.Utils.DatabaseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "eventServlet", value = "/events")
public class EventServlet extends HttpServlet {
    //driver
//    private static final String JDBC_DRIVER = "org.postgresql.Driver";
//    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/postgres";
//    private static final String JDBC_USER = "postgres";
//    private static final String JDBC_PASSWORD = "lyc980820";

    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    // 新的 JDBC URL
    private static final String JDBC_URL = "jdbc:postgresql://ep-icy-sea-a7vt9oiq.ap-southeast-2.aws.neon.tech/postgres1?user=postgres1_owner&password=nt4ug9SwXZUr&sslmode=require";

    // 新的数据库用户名
    private static final String JDBC_USER = "postgres1_owner";

    // 新的数据库密码
    private static final String JDBC_PASSWORD = "nt4ug9SwXZUr";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ServletException(e);
        }
        List<Event> events = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT e.id, e.title, e.description, e.venue, e.capacity, c.id AS clubId, c.name AS clubName " +
                    "FROM events e INNER JOIN clubs c ON e.club_id = c.id";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Event event = new Event();
                        event.setId(resultSet.getInt("id"));
                        event.setTitle(resultSet.getString("title"));
                        event.setDescription(resultSet.getString("description"));
                        event.setVenue(resultSet.getString("venue"));
                        event.setCapacity(resultSet.getInt("capacity"));
                        event.setClubId(resultSet.getInt("clubId"));
                        event.setClubName(resultSet.getString("clubName"));
                        events.add(event);
                        System.out.println(event);
                        System.out.println("events"+ event);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Error retrieving events", e);
        }

        request.setAttribute("events", events);
        request.getRequestDispatcher("/events.jsp").forward(request, response);
//
//        // 获取社团ID和事件ID
//        String clubId = request.getPathInfo().split("/")[1];
//        String eventId = request.getParameter("id");
//
//        // 根据是否有事件ID决定是查看单个事件还是所有事件
//        String query = eventId != null
//                ? "SELECT * FROM events WHERE id = ? AND club_id = ?"
//                : "SELECT * FROM events WHERE club_id = ?";
//
//        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            if (eventId != null) {
//                stmt.setInt(1, Integer.parseInt(eventId));
//                stmt.setInt(2, Integer.parseInt(clubId));
//            } else {
//                stmt.setInt(1, Integer.parseInt(clubId));
//            }
//
//            ResultSet rs = stmt.executeQuery();
//            request.setAttribute("events", rs);
//            request.getRequestDispatcher("/WEB-INF/jsp/events.jsp").forward(request, response);
//
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
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
