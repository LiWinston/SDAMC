package org.sdamc.DTO;

import lombok.Data;
import java.util.List;

@Data
public class RsvpSubmitDTO {

    private String eventId;

    private List<Attendee> attendees;

    @Data
    public static class Attendee {

        private String inputType;

        private String inputValue;

    }

}
