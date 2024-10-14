package org.sdamc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sdamc.DTO.Result;
import org.sdamc.DTO.RsvpDTO;
import org.sdamc.DTO.RsvpSubmitDTO;
import org.sdamc.DomainObject.Events;
import org.sdamc.DomainObject.Rsvps;
import org.sdamc.DomainObject.Students;
import org.sdamc.Mapper.EventsMapper;
import org.sdamc.Mapper.RsvpsMapper;
import org.sdamc.Mapper.StudentsMapper;
import org.sdamc.UnitofWork;
import org.sdamc.Utils.IOWrapper;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "RsvpController", value = "/rsvp/*")
public class RsvpController extends HttpServlet {

    private RsvpsMapper rsvpsMapper;

    private StudentsMapper studentsMapper;

    private EventsMapper eventsMapper;

    @Override
    public void init() {
        this.rsvpsMapper = new RsvpsMapper();
        this.studentsMapper = new StudentsMapper();
        this.eventsMapper = new EventsMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                handleRsvpPage(req, resp);
            }
            else if ("/student".equals(pathInfo)) {
                handleStudentRsvps(req, resp);
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid path");
            }
        }
        catch (Exception e) {
            // 记录错误
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    private void handleRsvpPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventId = req.getParameter("eventId");
        req.setAttribute("eventId", eventId);
        if (eventId != null && !eventId.isEmpty()) {
            Events event = (Events) eventsMapper.find(Integer.parseInt(eventId));
            req.setAttribute("event", event);
            System.out.println("Event found: " + (event != null)); // 添加日志
        }
        else {
            System.out.println("No eventId provided"); // 添加日志
        }
        req.getRequestDispatcher("/rsvp.jsp").forward(req, resp);
    }

    private void handleStudentRsvps(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int studentId = Integer.parseInt(req.getParameter("studentId"));
        List<Rsvps> rsvps = rsvpsMapper.findByStudentId(studentId);
        resp.setContentType("application/json");
        new ObjectMapper().writeValue(resp.getOutputStream(), Result.success(rsvps));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UnitofWork.newCurrent();
        try {
            String pathInfo = req.getPathInfo();
            if ("/submit".equals(pathInfo)) {
                handleRsvpSubmit(req, resp);
            }
            else if ("/cancel".equals(pathInfo)) {
                handleRsvpCancel(req, resp);
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid path");
            }
        }
        finally {
            UnitofWork.getCurrent().commit();
        }
    }

    private void handleRsvpCancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            RsvpDTO cancelDTO = IOWrapper.readValue(req, RsvpDTO.class);
            int rsvpId = cancelDTO.getRsvpId();

            Rsvps rsvp = (Rsvps) rsvpsMapper.find(rsvpId);
            if (rsvp == null) {
                throw new IllegalArgumentException("RSVP not found");
            }

            Events event = (Events) eventsMapper.find(rsvp.getEventId());
            //System.out.println(event.getCapacity());
            event.increaseCapacity(1); // Assuming each RSVP is for 1 ticket
            //System.out.println(event.getCapacity());
            eventsMapper.update(event);
            //System.out.println(event.getCapacity());
            rsvpsMapper.delete(rsvp);

            IOWrapper.writeValue(resp, Result.success("RSVP cancelled successfully"));
        }
        catch (Exception e) {
            e.printStackTrace();
            IOWrapper.writeValue(resp, Result.error(e.getMessage()));
        }
    }

    private void handleRsvpSubmit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            RsvpSubmitDTO rsvpSubmitDTO = IOWrapper.readValue(req, RsvpSubmitDTO.class);
            String eventIdStr = rsvpSubmitDTO.getEventId();

            if (eventIdStr == null || eventIdStr.isEmpty()) {
                throw new IllegalArgumentException("missing Event ID");
            }
            int eventId = Integer.parseInt(eventIdStr);

            Events event = (Events) eventsMapper.find(eventId);
            int totalAttendeesToRsvp = rsvpSubmitDTO.getAttendees().size();
            if (event.getCapacity() < totalAttendeesToRsvp) {
                throw new IllegalArgumentException("Not enough capacity for this RSVP");
            }

            for (RsvpSubmitDTO.Attendee attendee : rsvpSubmitDTO.getAttendees()) {
                int studentId = attendee.getStudentId();
                Students student = (Students) studentsMapper.find(studentId);
                if (student == null || !student.getName().equals(attendee.getName())
                        || !student.getEmail().equals(attendee.getEmail())) {
                    throw new IllegalArgumentException("Invalid student information for ID: " + studentId);
                }
                if (rsvpsMapper.findByStudentIdAndEventId(studentId, eventId) != null) {
                    throw new IllegalArgumentException("Student " + studentId + " already RSVPed for this event");
                }
                Rsvps.insert(studentId, eventId, 1);
            }

            // 更新事件容量
            event.decreaseCapacity(totalAttendeesToRsvp);
            //eventsMapper.update(event);

            new ObjectMapper().writeValue(resp.getOutputStream(), Result.success("RSVP successful"));
        }
        catch (Exception e) {
            System.out.println("Error in handleRsvpSubmit: " + e.getMessage());
            e.printStackTrace();
            new ObjectMapper().writeValue(resp.getOutputStream(), Result.error(e.getMessage()));
            throw e; // 重新抛出异常，让 doPost 方法捕获并处理
        }
    }

}