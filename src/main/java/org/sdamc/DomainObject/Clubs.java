package org.sdamc.DomainObject;

import org.sdamc.UnitofWork;
import org.sdamc.Mapper.DataMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Clubs extends DomainObject {

    public static final String tableName = "clubs";

    private final int id;

    private String name;

    private String description;

    private String location;

    public Clubs(int id) {
        this.id = id;
    }

    private void load() {
        try {
            ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
            if (result.next()) {
                name = result.getString("name");
                description = result.getString("description");
                location = result.getString("location");
                initialed = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Clubs insert(String name, String description, String location) {
        Clubs club = new Clubs(DataMapper.GetMapper(tableName).getNewId());
        club.name = name;
        club.description = description;
        club.location = location;
        club.insert = true;
        club.initialed = true;
        UnitofWork.getCurrent().registerNew(club);
        return club;
    }

    public void delete() {
        UnitofWork.getCurrent().registerDeleted(this);
        deleted = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return name;
    }

    public void setName(String name) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.name = name;
    }

    public String getDescription() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return description;
    }

    public void setDescription(String description) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.description = description;
    }

    public String getLocation() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return location;
    }

    public void setLocation(String location) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.location = location;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

}
