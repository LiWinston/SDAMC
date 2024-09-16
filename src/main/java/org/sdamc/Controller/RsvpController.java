package org.sdamc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sdamc.DTO.Result;
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
        UnitofWork.newCurrent();
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                handleRsvpPage(req, resp);
            } else if ("/student".equals(pathInfo)) {
                handleStudentRsvps(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid path");
            }
        } finally {
            UnitofWork.getCurrent().commit();
        }
    }

    private void handleRsvpPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventId = req.getParameter("eventId");
        System.out.println("Received eventId: " + eventId); // 添加日志
        req.setAttribute("eventId", eventId);
        if (eventId != null && !eventId.isEmpty()) {
            Events event = (Events) eventsMapper.find(Integer.parseInt(eventId));
            req.setAttribute("event", event);
            System.out.println("Event found: " + (event != null)); // 添加日志
        } else {
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
        System.out.println("Received POST request to " + req.getRequestURI());
        System.out.println("All parameters: " + req.getParameterMap());

        UnitofWork.newCurrent();
        try {
            String pathInfo = req.getPathInfo();
            if ("/submit".equals(pathInfo)) {
                handleRsvpSubmit(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid path");
            }
        } finally {
            UnitofWork.getCurrent().commit();
        }
    }

    private void handleRsvpSubmit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
//
//        System.out.println("Handling RSVP submit");
//        System.out.println("Request method: " + req.getMethod());
//        System.out.println("Content type: " + req.getContentType());
//        System.out.println("All parameters:");
//        req.getParameterMap().forEach((key, value) ->
//                System.out.println(key + ": " + String.join(", ", value)));

        try {
            RsvpSubmitDTO rsvpSubmitDTO = IOWrapper.readValue(req, RsvpSubmitDTO.class);
            String eventIdStr = rsvpSubmitDTO.getEventId();
            System.out.println("Received eventId in submit: " + eventIdStr);  //这里没有获取到eventid！！

            if (eventIdStr == null || eventIdStr.isEmpty()) {
                throw new IllegalArgumentException("missing Event ID");
            }
            int eventId = Integer.parseInt(eventIdStr);

            String[] studentIds = rsvpSubmitDTO.getAttendees().stream()
                    .map(attendee -> attendee.getStudentId().toString()) // 将 Integer 转为 String
                    .toArray(String[]::new);
            String[] names = rsvpSubmitDTO.getAttendees().stream().map(RsvpSubmitDTO.Attendee::getName).toArray(String[]::new);
            String[] emails = rsvpSubmitDTO.getAttendees().stream().map(RsvpSubmitDTO.Attendee::getEmail).toArray(String[]::new);

            if (studentIds == null || names == null || emails == null ||
                    studentIds.length != names.length || studentIds.length != emails.length) {
                throw new IllegalArgumentException("Invalid or mismatched student information");
            }

            for (int i = 0; i < studentIds.length; i++) {
                int studentId = Integer.parseInt(studentIds[i]);
                Students student = (Students) studentsMapper.find(studentId);
                if (student == null || !student.getName().equals(names[i]) || !student.getEmail().equals(emails[i])) {
                    throw new IllegalArgumentException("Invalid student information for ID: " + studentId);
                }
                if (rsvpsMapper.findByStudentIdAndEventId(studentId, eventId) != null) {
                    throw new IllegalArgumentException("Student " + studentId + " already RSVPed for this event");
                }
                Rsvps rsvp = Rsvps.insert(studentId, eventId, 1);
                rsvpsMapper.insert(rsvp);
            }

            new ObjectMapper().writeValue(resp.getOutputStream(), Result.success("RSVP successful"));
        } catch (Exception e) {
            System.out.println("Error in handleRsvpSubmit: " + e.getMessage());
            e.printStackTrace();
            new ObjectMapper().writeValue(resp.getOutputStream(), Result.error(e.getMessage()));
        }
    }
}