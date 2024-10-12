package org.sdamc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.sdamc.DTO.Result;
import org.sdamc.Services.EventCascadeOpSvc;
import org.sdamc.Transaction.TransactionalScanner;

import java.io.IOException;
import java.util.Map;

import static org.sdamc.Utils.JwtUtil.VerifyToken;

@WebServlet(name = "EventController", value = "/events/*")
public class EventController extends HttpServlet {

    private EventService eventService;

    private final EventCascadeOpSvc eventCascadeOpSvc = EventCascadeOpSvc.getInstance();

    public void init() {
        this.eventService = (EventService) TransactionalScanner.getProxy(EventService.class);
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
        eventService.handleGetAllEvents(req, resp);
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
        eventService.handleCreateEvent(req, resp);
    }

    // 修改事件 (PUT)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        eventService.handlePutEvent(req, resp);
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
