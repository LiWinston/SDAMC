package org.sdamc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.sdamc.DTO.Result;
import org.sdamc.DomainObject.Events;
import org.sdamc.Mapper.ClubMembershipsMapper;
import org.sdamc.Mapper.EventsMapper;
import org.sdamc.UnitofWork;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@WebServlet(name = "EventController", value = "/events/*")
public class EventController extends HttpServlet {

    private EventsMapper eventsMapper;

    private ClubMembershipsMapper membershipsMapper;

    @Override
    public void init() {
        // 初始化mapper
        this.eventsMapper = new EventsMapper();
        this.membershipsMapper = new ClubMembershipsMapper();
    }

    // 查找所有事件 (GET)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // 获取所有事件
        String pathInfo = req.getPathInfo();
        switch (pathInfo) {
            case null:
            case "/":
                handleGetAllEvents(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid path");
        }
    }

    private void handleGetAllEvents(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        UnitofWork.newCurrent();
        List<Events> eventsList = eventsMapper.findAll();

        for (Events event : eventsList) {
            resp.getWriter().write(event.toString() + "\n");
        }
        req.setAttribute("events", eventsList);
        req.getRequestDispatcher("/events.jsp").forward(req, resp);
    }

    /*
     * Create an event for a Student Club (POST /events) RSVP to an event (POST
     * /events/{id}/rsvp)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // RSVP to an event
        if (pathInfo != null && pathInfo.matches("/\\d+/rsvp")) {
            handleRSVP(req, resp);
        }
        else if (pathInfo == null || pathInfo.equals("/")) {
            handleCreateEvent(req, resp); // 创建事件
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid path");
        }
    }

    private void handleRSVP(@NotNull HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = req.getHeader("Authorization");
        int studentId = Integer.parseInt(req.getParameter("userId"));
        int eventId = Integer.parseInt(req.getParameter("eventId"));

        // 验证 token 和 studentId
        if (token == null || !isValidToken(token, studentId)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        // 检查是否已 RSVP 过该事件，避免重复操作

    }

    private boolean isValidToken(String token, int userId) {
        // 在这里检查 token 和 userId 的匹配关系，确保身份验证
        return true; // 假设验证通过
    }

    // 创建事件 (POST /events)
    /*
     * {"clubId":"1","title":"111","description":"11","venue":"11","capacity":"1",
     * "beginTime":"2024-09-13T16:35","endTime":"2024-09-13T21:31","userId":"1"}
     */
    private void handleCreateEvent(@NotNull HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = req.getHeader("Authorization");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> requestBody = mapper.readValue(req.getInputStream(), Map.class);
        int studentId = Integer.parseInt(requestBody.get("userId"));
        log("studentId: " + studentId);

        // 验证 token 和 studentId
        if (token == null || !isValidToken(token, studentId)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        int clubId = Integer.parseInt(requestBody.get("clubId"));

        try {
            UnitofWork.newCurrent();
            // 检查用户是否为此社团的管理员
            if (!membershipsMapper.isAdmin(studentId, clubId)) {
                resp.setContentType("application/json");
                new ObjectMapper().writeValue(resp.getOutputStream(),
                        Result.error("You are not the admin of this club"));
                return;
            }

            // 获取事件信息
            String title = requestBody.get("title");
            String description = requestBody.get("description");
            String venue = requestBody.get("venue");
            int capacity = Integer.parseInt(requestBody.get("capacity"));
            String beginTimeStr = requestBody.get("beginTime").replace("T", " ") + ":00"; // 确保有秒部分
            String endTimeStr = requestBody.get("endTime").replace("T", " ") + ":00";
            Timestamp beginTime = Timestamp.valueOf(beginTimeStr);
            Timestamp endTime = Timestamp.valueOf(endTimeStr);

            // 创建事件
            Events event = new Events(eventsMapper.getNewId());
            event.setTitle(title);
            event.setDescription(description);
            event.setVenue(venue);
            event.setCapacity(capacity);
            event.setClubId(clubId);
            event.setBeginTime(beginTime);
            event.setEndTime(endTime);

            eventsMapper.insert(event);
            UnitofWork.getCurrent().commit();

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getOutputStream(),
                    Result.success("Event: " + event.getTitle() + " created successfully"));
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            new ObjectMapper().writeValue(resp.getOutputStream(), Result.error("Database error"));
            e.printStackTrace();
        }
    }

    // 修改事件 (PUT)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int eventId = Integer.parseInt(req.getPathInfo().substring(1)); // 获取路径中的ID
        Events event = (Events) eventsMapper.find(eventId);
        if (event != null) {
            // 更新事件信息
            event.setTitle(req.getParameter("title"));
            event.setDescription(req.getParameter("description"));
            event.setVenue(req.getParameter("venue"));
            event.setCapacity(Integer.parseInt(req.getParameter("capacity")));
            eventsMapper.update(event);
            resp.getWriter().write("Event updated successfully");
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
        }
    }

    // 删除事件 (DELETE)
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int eventId = Integer.parseInt(req.getPathInfo().substring(1));
        eventsMapper.deleteById(eventId);
        resp.getWriter().write("Event deleted successfully");
    }

}
