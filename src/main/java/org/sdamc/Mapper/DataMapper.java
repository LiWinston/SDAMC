package org.sdamc.Mapper;

import org.sdamc.DomainObject.Clubs;
import org.sdamc.DomainObject.ClubMemberships;
import org.sdamc.DomainObject.DomainObject;
import org.sdamc.DomainObject.Events;
import org.sdamc.DomainObject.Rsvps;
import org.sdamc.DomainObject.Students;

import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DataMapper {

    private static final Map<String, DataMapper> mapperMap = new HashMap<>();

    static {
        mapperMap.put(Clubs.tableName, new ClubsMapper());
        mapperMap.put(ClubMemberships.tableName, new ClubMembershipsMapper());
        mapperMap.put(Events.tableName, new EventsMapper());
        mapperMap.put(Rsvps.tableName, new RsvpsMapper());
        mapperMap.put(Students.tableName, new StudentsMapper());
    }

    public static DataMapper GetMapper(String tableName) {
        return mapperMap.get(tableName);
    }

    abstract public DomainObject find(int id);

    abstract public void update(DomainObject obj);

    abstract public void insert(DomainObject obj);

    abstract public void delete(DomainObject obj);

    // Function for Lazy load, should only call by Domainobject
    // TODO: may be write a seperate gateway for this job
    abstract public ResultSet getRecord(int id) throws SQLException;

}
