package com.ixibot.module

import com.ixibot.CONFIG_DIRECTORY
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.sql.DriverManager

val databaseModule: Module = module {
    /**
     * Connection URL provider.
     */
    single(named("connectionUrl")) {"jdbc:sqlite:${CONFIG_DIRECTORY}sqlite.db" }

    /**
     * JDBC connection provider.
     */
    single { DriverManager.getConnection(get(named("connectionUrl"))) }
}
