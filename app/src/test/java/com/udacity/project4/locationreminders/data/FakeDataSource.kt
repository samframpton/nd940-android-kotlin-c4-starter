package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    val dataSource: MutableMap<String, ReminderDTO> = HashMap()

    var error = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (error) return Result.Error("Test Error")
        val reminders = dataSource.values.toList()
        return Result.Success(reminders)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        dataSource.put(reminder.id, reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminder = dataSource[id] ?: return Result.Error("Reminder was not found")
        return Result.Success(reminder)
    }

    override suspend fun deleteAllReminders() {
        dataSource.clear()
    }

}