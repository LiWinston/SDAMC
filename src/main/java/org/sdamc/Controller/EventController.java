package org.sdamc.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.sdamc.DomainObject.Events;
import org.sdamc.Mapper.ClubMembershipsMapper;
import org.sdamc.Mapper.EventsMapper;
import org.sdamc.UnitofWork;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "EventController", value = "/events/*")
public class EventController extends HttpServlet {

    private EventsMapper eventsMapper;

    private ClubMembershipsMapper membershipsMapper;

    @Override
    public void init() {
        // 初始化mapper
        this.eventsMapper = new EventsMapper();
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
    private void handleCreateEvent(@NotNull HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = req.getHeader("Authorization");
        int studentId = Integer.parseInt(req.getParameter("userId"));
        int clubId = Integer.parseInt(req.getParameter("clubId"));

        // 验证 token 和 studentId
        if (token == null || !isValidToken(token, studentId)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        // 检查用户是否为管理员
        if (!membershipsMapper.isAdmin(studentId, clubId)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to create events");
            return;
        }

        // 获取事件信息
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String venue = req.getParameter("venue");
        int capacity = Integer.parseInt(req.getParameter("capacity"));

        // 创建事件
        Events event = new Events(eventsMapper.getNewId());
        event.setTitle(title);
        event.setDescription(description);
        event.setVenue(venue);
        event.setCapacity(capacity);

        eventsMapper.insert(event);

        resp.getWriter().write("Event created successfully");
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
