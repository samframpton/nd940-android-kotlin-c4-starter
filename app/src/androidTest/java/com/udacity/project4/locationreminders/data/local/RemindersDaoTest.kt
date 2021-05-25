package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun after() = database.close()

    @Test
    fun getReminders() = runBlockingTest {
        val reminder1 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder2 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder3 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders[0], `is`(reminder1))
        assertThat(reminders[1], `is`(reminder2))
        assertThat(reminders[2], `is`(reminder3))
    }

    @Test
    fun getReminderById() = runBlockingTest {
        val reminder1 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder1)
        val reminder = database.reminderDao().getReminderById(reminder1.id)
        assertThat(reminder, `is`(reminder1))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        val reminder1 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder2 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder3 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)
        database.reminderDao().deleteAllReminders()
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.isEmpty(), `is`(true))
    }

}