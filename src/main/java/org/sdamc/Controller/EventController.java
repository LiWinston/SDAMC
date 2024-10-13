package org.sdamc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.sdamc.DTO.Result;
import org.sdamc.DTO.RsvpDTO;
import org.sdamc.Mapper.RsvpsMapper;
import org.sdamc.Services.EventCascadeOpSvc;
import org.sdamc.Services.EventService;
import org.sdamc.Transaction.TransactionalScanner;
import org.sdamc.Utils.IOWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "EventController", value = "/events/*")
public class EventController extends HttpServlet {

    private EventService eventService;

    private EventCascadeOpSvc eventCascadeOpSvc;

    private RsvpsMapper rsvpsMapper;

    public void init() {
        this.eventService = (EventService) TransactionalScanner.getProxy(EventService.class);
        this.eventCascadeOpSvc = (EventCascadeOpSvc) TransactionalScanner.getProxy(EventCascadeOpSvc.class);
        this.rsvpsMapper = new RsvpsMapper();
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
            case "/user-rsvps":
                handleGetUserRsvps(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid path");
        }
    }

    private void handleGetUserRsvps(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userIdStr = req.getParameter("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required");
            return;
        }

        int userId = Integer.parseInt(userIdStr);
        List<RsvpDTO> userRsvps = rsvpsMapper.findDetailedRsvpsByStudentId(userId);

        IOWrapper.writeValue(resp, Result.success(userRsvps));
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

        if (pathInfo == null || pathInfo.equals("/")) {
            handleCreateEvent(req, resp); // 创建事件
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid path");
        }
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
