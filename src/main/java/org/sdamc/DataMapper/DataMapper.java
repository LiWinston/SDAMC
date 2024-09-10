package org.sdamc.DataMapper;

import org.sdamc.DomainObject.DomainObject;
import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;

public abstract class DataMapper {

	private static final Map<String, DataMapper> mapperMap = new HashMap<>();

	static {
		// mapperMap.put("user", new UserMapper());
		// Add more mappings as needed
	}

	public static DataMapper GetMapper(String tableName) {
		return mapperMap.get(tableName);
	}

	abstract public DomainObject find(int id);

	abstract public void update(DomainObject obj);

	abstract public void insert(DomainObject obj);

	abstract public void delete(DomainObject obj);

	// Function for Lazy lord, should only call by Domainobject
	// TODO: may be write a seperate gateway for this job
	abstract public ResultSet getRecord(int id);

}
