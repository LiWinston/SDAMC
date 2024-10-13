package org.sdamc.DTO;

import lombok.Setter;

import java.sql.Timestamp;

public class RsvpDTO {
    @Setter
    private int rsvpId;
    @Setter
    private int studentId;
    @Setter
    private int eventId;
    @Setter
    private String eventTitle;
    @Setter
    private Timestamp beginTime;
    @Setter
    private Timestamp endTime;
    @Setter
    private String venue;
    @Setter
    private String attendeeName;
    @Setter
    private String attendeeEmail;

    public int getRsvpId() {
        return rsvpId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getVenue() {
        return venue;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public String getAttendeeEmail() {
        return attendeeEmail;
    }
}
