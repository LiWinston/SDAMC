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
import org.sdamc.Services.EventCascadeOpSvc;
import org.sdamc.UnitofWork;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.sdamc.Utils.JwtUtil.VerifyToken;

@WebServlet(name = "EventController", value = "/events/*")
public class EventController extends HttpServlet {

    private EventsMapper eventsMapper;

    private ClubMembershipsMapper membershipsMapper;

    private final EventCascadeOpSvc eventCascadeOpSvc = EventCascadeOpSvc.getInstance();

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
        int studentId = Integer.parseInt(req.getParameter("userId"));
        if (!VerifyToken(req, resp, studentId))
            return;
        int eventId = Integer.parseInt(req.getParameter("eventId"));

        // 检查是否已 RSVP 过该事件，避免重复操作

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
        if (!VerifyToken(req, resp, studentId)) {
            // resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
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
            Integer capacity = null;
            if (requestBody.get("capacity") != null && !requestBody.get("capacity").isEmpty()
                    && !requestBody.get("capacity").isBlank()) {
                capacity = Integer.parseInt(requestBody.get("capacity"));
            }
            else {
                capacity = 0;
            }
            String beginTimeStr = requestBody.get("beginTime").replace("T", " ") + ":00"; // 确保有秒部分
            Timestamp beginTime = Timestamp.valueOf(beginTimeStr);

            Timestamp endTime = null;
            if (requestBody.get("endTime") != null && !requestBody.get("endTime").isEmpty()
                    && !requestBody.get("endTime").isBlank()) {
                String endTimeStr = requestBody.get("endTime").replace("T", " ") + ":00";
                endTime = Timestamp.valueOf(endTimeStr);
            }

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
                    Result.success(null, "Event: " + event.getTitle() + " created successfully"));
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
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> requestBody = mapper.readValue(req.getInputStream(), Map.class);

        UnitofWork.newCurrent();

        int eventId = Integer.parseInt(requestBody.get("eventId"));
        Events event = (Events) eventsMapper.find(eventId);
        if (event != null) {
            // 更新事件信息
            event.setTitle(requestBody.get("title"));
            event.setDescription(requestBody.get("description"));
            event.setVenue(requestBody.get("venue"));
            event.setCapacity(Integer.parseInt(requestBody.get("capacity")));
            String beginTimeStr = requestBody.get("beginTime").replace("T", " ") + ":00"; // 确保有秒部分
            Timestamp beginTime = Timestamp.valueOf(beginTimeStr);

            Timestamp endTime = null;
            if (requestBody.get("endTime") != null && !requestBody.get("endTime").isEmpty()
                    && !requestBody.get("endTime").isBlank()) {
                String endTimeStr = requestBody.get("endTime").replace("T", " ") + ":00";
                endTime = Timestamp.valueOf(endTimeStr);
            }
            event.setBeginTime(beginTime);
            event.setEndTime(endTime);
            eventsMapper.update(event);
            resp.getWriter().write("Event updated successfully");
        }

        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
        }
        UnitofWork.getCurrent().commit();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> requestBody = mapper.readValue(req.getInputStream(), Map.class);
            int userId = Integer.parseInt(requestBody.get("userId"));
            int eventId = Integer.parseInt(requestBody.get("eventId"));
            int clubId = Integer.parseInt(requestBody.get("clubId"));

            // 调用 deleteEvent 方法
            Result<?> result = eventCascadeOpSvc.deleteEvent(eventId, userId, clubId);

            // 返回结果
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK); // 成功状态码
            new ObjectMapper().writeValue(resp.getOutputStream(), result); // 返回结果
        }
        catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input data" + e.getMessage());
        }
        catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error" + e.getMessage());
        }
    }

}
