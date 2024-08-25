package org.zeen.autostart.store

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.zeen.autostart.Application
import com.zeen.autostart.Database

val driver: SqlDriver by lazy {
    val driver = JdbcSqliteDriver("jdbc:sqlite:test.db")
    try {
        Database.Schema.create(driver)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    driver
}

private val database: Database by lazy {
    Database(driver)
}

fun buildApplication(name: String, path: String): Application {
    return Application(0, path, name)
}

fun queryAll(): List<Application> {
    val applications = database.databaseQueries.selectAll().executeAsList()
    return applications
}

fun removeApplication(application: Application): Boolean {
    if (application.id == 0L) {
        println("删除失败")
        return false
    }
    try {
        database.databaseQueries.removeById(application.id)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun removeAll() {
    database.databaseQueries.removeAll()
}

fun checkNameOrPathExist(application: Application): Boolean {
    val size: Long = database.databaseQueries.checkNameOrPathExists(application.name, application.path).executeAsOneOrNull() ?: 0
    return size > 0
}

fun insert(application: Application) {
    database.databaseQueries.insert(application.name, application.path)
}