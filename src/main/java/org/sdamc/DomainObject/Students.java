package org.sdamc.DomainObject;

import lombok.Getter;
import lombok.ToString;
import org.sdamc.UnitofWork;
import org.sdamc.Mapper.DataMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

@ToString
public class Students extends DomainObject {

    public static final String tableName = "students";

    // Getters and Setters
    @Getter
    private final int id;

    private String name;

    private String email;

    private String password;

    public Students(int id) {
        this.id = id;
    }

    private void load() {
        try {
            ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
            if (result.next()) {
                name = result.getString("name");
                email = result.getString("email");
                initialed = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Students insert(String name, String email) {
        Students student = new Students(DataMapper.GetMapper(tableName).getNewId());
        student.name = name;
        student.email = email;
        student.insert = true;
        student.initialed = true;
        UnitofWork.getCurrent().registerNew(student);
        return student;
    }

    public void delete() {
        UnitofWork.getCurrent().registerDeleted(this);
        deleted = true;
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

    public String getEmail() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return email;
    }

    public void setEmail(String email) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.email = email;
    }

    public String getPassword() {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return password;
    }

    public void setPassword(String password) {
        assert (!deleted);
        if (!initialed) {
            load();
        }
        if (!insert) {
            UnitofWork.getCurrent().registerDirty(this);
        }
        this.password = password;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

}
