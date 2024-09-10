package org.sdamc.DomainObject;

import org.sdamc.UnitofWork;
import org.sdamc.DataMapper.DataMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Students extends DomainObject {

	private static final String tableName = "students";

	private final int id;

	private String name;

	private String email;

	public Students(int id) {
		this.id = id;
	}

	private void load() {
		ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
		try {
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

	// Getters and Setters
	public int getId() {
		return id;
	}

	public String getName() {
		if (!initialed) {
			load();
		}
		return name;
	}

	public void setName(String name) {
		if (!initialed) {
			load();
		}
		UnitofWork.getCurrent().registerDirty(this);
		this.name = name;
	}

	public String getEmail() {
		if (!initialed) {
			load();
		}
		return email;
	}

	public void setEmail(String email) {
		if (!initialed) {
			load();
		}
		UnitofWork.getCurrent().registerDirty(this);
		this.email = email;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

}
