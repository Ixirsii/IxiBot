package com.ixibot.module

import com.ixibot.CONFIG_DIRECTORY
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * URL/path to SQLite database file.
 */
private const val CONNECTION_URL = "jdbc:sqlite:" + CONFIG_DIRECTORY + "sqlite.db"

@ComponentScan("com.ixibot.database")
@Module
class DatabaseModule {
    /**
     * JDBC connection provider.
     *
     * @return JDBC connection.
     * @throws ClassNotFoundException on failure to load JDBC driver.
     * @throws SQLException if a database access error occurs.
     */
    @Single
    @Throws(ClassNotFoundException::class, SQLException::class)
    fun connection(): Connection {
        Class.forName("org.sqlite.JDBC")
        return DriverManager.getConnection(CONNECTION_URL)
    }
}