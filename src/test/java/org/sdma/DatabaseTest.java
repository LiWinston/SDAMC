package org.sdma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.sdamc.Mapper.DataMapper;
import org.sdamc.UnitofWork;
import org.sdamc.DomainObject.*;
import org.sdamc.Utils.DatabaseUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseTest {

    @BeforeEach
    public void setUp() throws IOException, SQLException {
        String url = "jdbc:postgresql://ep-icy-sea-a7vt9oiq.ap-southeast-2.aws.neon.tech/test?user=postgres1_owner&password=nt4ug9SwXZUr&sslmode=require";
        String user = "postgres1_owner";
        String password = "nt4ug9SwXZUr";
        DatabaseUtil.connectDatabase(url, user, password);
        // Initial the database with seed
        // Read the SQL file into a string
        String sql = new String(Files.readAllBytes(Paths.get("./SchemaCreate.sql")));

        // Create a Statement object
        Statement statement = DatabaseUtil.getConnection().createStatement();

        // Execute the SQL commands
        statement.execute(sql);

        sql = new String(Files.readAllBytes(Paths.get("./seed.sql")));

        statement.execute(sql);

        // Close the statement
        statement.close();
    }

    @Test
    public void testclub() throws SQLException {
        assertNotNull(DatabaseUtil.getConnection(), "Connection should not be null");
        UnitofWork.newCurrent();
        Clubs club = (Clubs) DataMapper.GetMapper(Clubs.tableName).find(1);
        assertEquals(club.getName(), "Photography Club");
        club.setName("test name");
        UnitofWork.getCurrent().commit();
        club = (Clubs) DataMapper.GetMapper(Clubs.tableName).find(1);
        assertEquals(club.getName(), "test name");
    }

    @AfterEach
    public void tearDown() throws SQLException {

    }

}