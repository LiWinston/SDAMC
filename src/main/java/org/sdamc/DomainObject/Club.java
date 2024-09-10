package org.sdamc.DomainObject;

import org.sdamc.DataMapper.DataMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Club extends DomainObject {

	private static final String tableName = "club";

	private final int id;

	private String name;

	private String description;

	private String location;

	Club(int id) {
		this.id = id;
	}

	private void load() {
		ResultSet result = DataMapper.GetMapper(tableName).getRecord(id);
		try {
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
		this.name = name;
	}

	public String getDescription() {
		if (!initialed) {
			load();
		}
		return description;
	}

	public void setDescription(String description) {
		if (!initialed) {
			load();
		}
		this.description = description;
	}

	public String getLocation() {
		if (!initialed) {
			load();
		}
		return location;
	}

	public void setLocation(String location) {
		if (!initialed) {
			load();
		}
		this.location = location;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

}
