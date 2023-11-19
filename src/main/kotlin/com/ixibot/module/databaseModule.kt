package com.ixibot.module

import com.ixibot.CONFIG_DIRECTORY
import com.ixibot.database.Database
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * URL/path to SQLite database file.
 */
private const val CONNECTION_URL = "jdbc:sqlite:" + CONFIG_DIRECTORY + "sqlite.db"

/**
 * JDBC connection provider.
 *
 * @return JDBC connection.
 * @throws ClassNotFoundException on failure to load JDBC driver.
 * @throws SQLException if a database access error occurs.
 */
@Throws(ClassNotFoundException::class, SQLException::class)
fun connection(): Connection {
    Class.forName("org.sqlite.JDBC")
    return DriverManager.getConnection(CONNECTION_URL)
}

/**
 * Database interface provider.
 *
 * @param connection JDBC connection.
 * @return Database interface.
 * @throws SQLException on error reading from database.
 */
@Throws(SQLException::class)
fun database(connection: Connection): Database {
    val database = Database(connection)
    database.init()
    return database
}
