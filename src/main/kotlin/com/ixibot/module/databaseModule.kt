/*
 * Copyright (c) 2019, Ryan Porterfield
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
 * @throws SQLException           if a database access error occurs.
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