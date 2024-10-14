package org.sdamc.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sdamc.DTO.Result;
import org.sdamc.DomainObject.Events;
import org.sdamc.Mapper.ClubMembershipsMapper;
import org.sdamc.Mapper.EventsMapper;
import org.sdamc.Transaction.IsolationLevel;
import org.sdamc.Transaction.LockingStrategy;
import org.sdamc.Transaction.Transactional;
import org.sdamc.UnitofWork;
import org.sdamc.Utils.LockManager;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.sdamc.Utils.JwtUtil.VerifyToken;

@Slf4j
public class EventService {

    private final EventsMapper eventsMapper;

    private final ClubMembershipsMapper membershipsMapper;

    public EventService() {
        // 初始化mapper
        this.eventsMapper = new EventsMapper();
        this.membershipsMapper = new ClubMembershipsMapper();
    }

    // @Transactional(isolationLevel = IsolationLevel.REPEATABLE_READ, lockingStrategy =
    // LockingStrategy.OPTIMISTIC)
    public void handleGetAllEvents(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        UnitofWork.newCurrent();
        List<Events> eventsList = eventsMapper.findAll();
        for (Events event : eventsList) {
            resp.getWriter().write(event.toString() + "\n");
        }
        req.setAttribute("events", eventsList);
        req.getRequestDispatcher("/events.jsp").forward(req, resp);
    }

    @Transactional(isolationLevel = IsolationLevel.REPEATABLE_READ, lockingStrategy = LockingStrategy.PESSIMISTIC)
    public void handleCreateEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = req.getHeader("Authorization");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> requestBody = mapper.readValue(req.getInputStream(), Map.class);
        int studentId = Integer.parseInt(requestBody.get("userId"));
        log.info("Student ID: {}", studentId);

        // 验证 token 和 studentId
        if (!VerifyToken(req, resp, studentId)) {
            // resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        int clubId = Integer.parseInt(requestBody.get("clubId"));

        try {
            // UnitofWork.newCurrent();
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
            // UnitofWork.getCurrent().commit();

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

    // @Transactional(isolationLevel = IsolationLevel.REPEATABLE_READ, lockingStrategy =
    // LockingStrategy.PESSIMISTIC)
    public void handlePutEvent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> requestBody = mapper.readValue(req.getInputStream(), Map.class);

            int eventId = Integer.parseInt(requestBody.get("eventId"));
            Events event = (Events) eventsMapper.find(eventId);

            // 尝试获取锁，超时时间为2000毫秒
            boolean lockAcquired = LockManager.getInstance().acquireLock("Event_" + eventId, "handlePutEvent", 2000);

            if (!lockAcquired) {
                // 锁获取失败，返回错误响应
                resp.sendError(HttpServletResponse.SC_CONFLICT,
                        "Failed to acquire lock for the event. Try again later.");
                return; // 直接退出，避免后续操作
            }

            // 如果锁获取成功，继续执行以下操作
            UnitofWork.newCurrent();

            if (event != null) {
                // 更新事件信息
                requestBody.remove("eventId"); // 移除eventId，避免更新时出错
                eventsMapper.update(event, requestBody);

                UnitofWork.getCurrent().commit();
                LockManager.getInstance().releaseLock("Event_" + eventId, "update");
                resp.getWriter().write("Event updated successfully");
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
            }
        }
        catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
            e.printStackTrace();
        }
    }

}
