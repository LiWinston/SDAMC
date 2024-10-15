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
import org.sdamc.Utils.LockManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            System.out.println(event.getCapacity());
            event.increaseCapacity(1); // Assuming each RSVP is for 1 ticket
            System.out.println(event.getCapacity());
            eventsMapper.update(event);
            System.out.println(event.getCapacity());
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

        List<RsvpSubmitDTO.Attendee> successfulAttendees = new ArrayList<>();
        List<String> failedAttendees = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try {
            RsvpSubmitDTO rsvpSubmitDTO = IOWrapper.readValue(req, RsvpSubmitDTO.class);
            String eventIdStr = rsvpSubmitDTO.getEventId();

            if (eventIdStr == null || eventIdStr.isEmpty()) {
                IOWrapper.writeValue(resp, Result.error("Missing Event ID"));
                return;
            }
            int eventId = Integer.parseInt(eventIdStr);

            Events event = (Events) eventsMapper.find(eventId);
            if (event == null) {
                IOWrapper.writeValue(resp, Result.error("Event not found"));
                return;
            }

            int totalAttendeesToRsvp = rsvpSubmitDTO.getAttendees().size();
            if (event.getCapacity() < totalAttendeesToRsvp) {
                IOWrapper.writeValue(resp, Result.error("Not enough capacity for this RSVP"));
                return;
            }

            boolean lockAcquired = LockManager.getInstance().acquireLock("Event_" + eventId, "handleRsvpSubmit", 2000);
            if (!lockAcquired) {
                IOWrapper.writeValue(resp, Result.error("Could not acquire lock for event"));
                return;
            }

            // 使用 try-finally 确保事件锁在所有情况下都能释放
            try {
                List<Students> validStudents = new ArrayList<>();

                for (RsvpSubmitDTO.Attendee attendee : rsvpSubmitDTO.getAttendees()) {
                    boolean attendeeLockAcquired = LockManager.getInstance()
                        .acquireLock("RSVP_USER_" + attendee.getInputValue() + "_Event_" + eventId, "handleRsvpSubmit",
                                2000);
                    if (!attendeeLockAcquired) {
                        failedAttendees.add(attendee.getInputValue());
                        errorMessages
                            .add("Could not acquire lock for attendee with input: " + attendee.getInputValue());
                        continue;
                    }

                    try {
                        Students student = null;
                        // 根据 inputType 检测合法性并查找学生
                        switch (attendee.getInputType()) {
                            case "studentId":
                                try {
                                    int studentId = Integer.parseInt(attendee.getInputValue());
                                    student = (Students) studentsMapper.findById(studentId);
                                }
                                catch (Exception e) {
                                    errorMessages.add("Invalid student ID: " + attendee.getInputValue());
                                    continue;
                                }
                                break;
                            case "email":
                                student = studentsMapper.findByEmail(attendee.getInputValue());
                                break;
                            case "name":
                                student = studentsMapper.findByName(attendee.getInputValue());
                                break;
                            default:
                                errorMessages.add("Invalid input type: " + attendee.getInputType());
                                continue;
                        }

                        if (student == null) {
                            failedAttendees.add(attendee.getInputValue());
                            errorMessages.add("Student not found for input: " + attendee.getInputValue());
                            continue;
                        }

                        if (rsvpsMapper.findByStudentIdAndEventId(student.getId(), eventId) != null) {
                            failedAttendees.add(attendee.getInputValue());
                            errorMessages.add("Student " + student.getId() + " already RSVPed for this event");
                            continue;
                        }

                        // 插入 RSVP
                        Rsvps.insert(student.getId(), eventId, 1);
                        validStudents.add(student);
                        successfulAttendees.add(attendee);
                    }
                    finally {
                        // 始终释放与 Attendee 相关的锁
                        LockManager.getInstance()
                            .releaseLock("RSVP_USER_" + attendee.getInputValue() + "_Event_" + eventId,
                                    "handleRsvpSubmit");
                    }
                }

                // 更新事件容量
                event.decreaseCapacity(validStudents.size());
                eventsMapper.update(event);

                // 构建最终消息
                String successMsg = validStudents.isEmpty() ? ""
                        : "RSVP successful for: " + validStudents.stream()
                            .map(Students::getId)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));
                String errorMsg = !errorMessages.isEmpty() ? ". Failed for: " + String.join(", ", failedAttendees)
                        + ". Reasons: " + String.join("; ", errorMessages) : "";

                IOWrapper.writeValue(resp, validStudents.size() == rsvpSubmitDTO.getAttendees().size()
                        ? Result.success(successMsg + errorMsg) : Result.error(successMsg + errorMsg));
            }
            finally {
                // 始终释放事件锁
                LockManager.getInstance().releaseLock("Event_" + eventId, "handleRsvpSubmit");
            }
        }
        catch (Exception e) {
            System.out.println("Error in handleRsvpSubmit: " + e.getMessage());
            e.printStackTrace();
            IOWrapper.writeValue(resp, Result.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

}