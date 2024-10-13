package org.sdamc.DomainObject;

import org.sdamc.Mapper.DataMapper;
import org.sdamc.UnitofWork;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin extends DomainObject {

    public static final String tableName = "admins";

    private final int id;

    private String name;

    private String email;

    private String password;

    // Constructor
    public Admin(int id) {
        this.id = id;
    }

    private void load() {
        try {
            ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
            if (result.next()) {
                name = result.getString("name");
                email = result.getString("email");
                password = result.getString("password");
                initialed = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Admin insert(int id, String name, String email, String password) {
        Admin admin = new Admin(id);
        admin.name = name;
        admin.email = email;
        admin.password = password;
        admin.insert = true;
        admin.initialed = true;
        UnitofWork.getCurrent().registerNew(admin);
        return admin;
    }

    public void delete() {
        UnitofWork.getCurrent().registerDeleted(this);
        deleted = true;
    }

    // Getters and Setters
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
        assert (!deleted);
        if (!initialed) {
            load();
        }
        return tableName;
    }

    @Override
    public int getId() {
        return id;
    }

}