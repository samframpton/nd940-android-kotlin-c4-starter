package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun after() = database.close()

    @Test
    fun getReminders() = runBlocking {
        val reminder1 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder2 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder3 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)
        val reminders = (remindersLocalRepository.getReminders() as Result.Success).data
        MatcherAssert.assertThat(reminders[0], Matchers.`is`(reminder1))
        MatcherAssert.assertThat(reminders[1], Matchers.`is`(reminder2))
        MatcherAssert.assertThat(reminders[2], Matchers.`is`(reminder3))
    }

    @Test
    fun getReminder() = runBlocking {
        val reminder1 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        remindersLocalRepository.saveReminder(reminder1)
        val reminder = (remindersLocalRepository.getReminder(reminder1.id) as Result.Success).data
        MatcherAssert.assertThat(reminder, Matchers.`is`(reminder1))
    }

    @Test
    fun getReminder_noReminder() = runBlocking {
        val reminder = (remindersLocalRepository.getReminder("test") as Result.Error).message
        MatcherAssert.assertThat(reminder, Matchers.`is`("Reminder not found!"))
    }

    @Test
    fun deleteAllReminders() = runBlocking {
        val reminder1 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder2 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder3 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)
        remindersLocalRepository.deleteAllReminders()
        val reminders = (remindersLocalRepository.getReminders() as Result.Success).data
        MatcherAssert.assertThat(reminders.isEmpty(), Matchers.`is`(true))
    }

}