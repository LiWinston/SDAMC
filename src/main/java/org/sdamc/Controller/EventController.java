package org.sdamc.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sdamc.DomainObject.Events;
import org.sdamc.Mapper.EventsMapper;
import org.sdamc.UnitofWork;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "EventController", value = "/events/*")
public class EventController extends HttpServlet {

    private EventsMapper eventsMapper;

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

    // 创建事件 (POST)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 从请求中获取参数并创建事件
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String venue = req.getParameter("venue");
        int capacity = Integer.parseInt(req.getParameter("capacity"));

        // 生成uuid 用于事件的唯一标识
        int uuid = UUID.randomUUID().hashCode();
        Events event = new Events(uuid);
        event.setTitle(title);
        event.setDescription(description);
        event.setVenue(venue);
        event.setCapacity(capacity);

        eventsMapper.insert(event); // 使用Mapper代替JDBC操作

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
