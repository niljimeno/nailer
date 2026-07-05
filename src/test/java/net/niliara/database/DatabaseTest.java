package net.niliara.database;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import net.niliara.dto.DataDirectory;

class DatabaseTest {

    @Test
    void connectsToSqliteDatabase() throws Exception {
        Database database = new Database(new DataDirectory(Path.of(":memory:")));
        database.connect();
        database.close();
    }

}
