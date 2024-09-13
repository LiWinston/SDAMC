package org.sdamc.Services;

import lombok.Getter;
import org.sdamc.DTO.Result;
import org.sdamc.DomainObject.Rsvps;
import org.sdamc.Mapper.ClubMembershipsMapper;
import org.sdamc.Mapper.EventsMapper;
import org.sdamc.Mapper.RsvpsMapper;
import org.sdamc.UnitofWork;

import java.util.List;

public class EventCascadeOpSvc {

    // 单例获取构造器
    @Getter
    private static EventCascadeOpSvc instance = new EventCascadeOpSvc();

    private final ClubMembershipsMapper clubMembershipsMapper = new ClubMembershipsMapper();

    private final EventsMapper eventsMapper = new EventsMapper();

    private final RsvpsMapper rsvpsMapper = new RsvpsMapper();

    public Result<?> deleteEvent(int eventId, int userId, int clubId) {
        UnitofWork.newCurrent();
        // 先验证此人对此club是否有权限
        if (clubMembershipsMapper.isAdmin(userId, clubId)) {

            // 查询关联记录，向结果中输出这些记录
            // rsvp
            List<Rsvps> rsvpsList = List.of();
            Result<List<Rsvps>> findByEventIdResult = rsvpsMapper.findByEventId(eventId);
            if (findByEventIdResult.getCode() == 1) {
                rsvpsList = findByEventIdResult.getData();
            }
            eventsMapper.deleteByEventId(eventId);
            UnitofWork.getCurrent().commit();
            return Result.success(rsvpsList,
                    "Event" + eventId + " deleted successfully with " + rsvpsList.size() + " rsvps deleted");
        }
        UnitofWork.getCurrent().commit();
        return Result.error("User are not authorized to delete this event");
    }

}
