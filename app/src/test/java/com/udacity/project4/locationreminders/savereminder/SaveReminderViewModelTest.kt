package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun before() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun saveReminder_reminderSaved() = runBlockingTest {
        val reminder = ReminderDataItem("title", "description", "location", 0.0, 0.0)
        saveReminderViewModel.saveReminder(reminder)
        val savedReminder = (fakeDataSource.getReminder(reminder.id) as Result.Success).data
        assertThat(savedReminder.id, `is`(reminder.id))
        val showToast = saveReminderViewModel.showToast.getOrAwaitValue()
        assertThat(
            showToast,
            `is`(
                ApplicationProvider.getApplicationContext<Context>()
                    .getString(R.string.reminder_saved)
            )
        )
        val navigationCommand = saveReminderViewModel.navigationCommand.getOrAwaitValue()
        assertThat(navigationCommand, `is`(NavigationCommand.Back))
    }

    @Test
    fun saveReminder_showLoading() = runBlockingTest {
        val reminder = ReminderDataItem("title", "description", "location", 0.0, 0.0)
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminder)
        var showLoading = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoading, `is`(true))
        mainCoroutineRule.resumeDispatcher()
        showLoading = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoading, `is`(false))
    }

    @Test
    fun validateEnteredData_invalidTitle() = runBlockingTest {
        val reminder = ReminderDataItem(null, "description", "location", 0.0, 0.0)
        val isValid = saveReminderViewModel.validateEnteredData(reminder)
        assertThat(isValid, `is`(false))
        val showSnackBarInt = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(showSnackBarInt, `is`(R.string.err_enter_title))
    }

    @Test
    fun validateEnteredData_invalidLocation() = runBlockingTest {
        val reminder = ReminderDataItem("title", "description", null, 0.0, 0.0)
        val isValid = saveReminderViewModel.validateEnteredData(reminder)
        assertThat(isValid, `is`(false))
        val showSnackBarInt = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(showSnackBarInt, `is`(R.string.err_select_location))
    }

    @Test
    fun validateEnteredData_valid() = runBlockingTest {
        val reminder = ReminderDataItem("title", "description", "location", 0.0, 0.0)
        val isValid = saveReminderViewModel.validateEnteredData(reminder)
        assertThat(isValid, `is`(true))
    }

    @Test
    fun validateAndSaveReminder_valid() = runBlockingTest {
        val reminder = ReminderDataItem("title", "description", "location", 0.0, 0.0)
        saveReminderViewModel.validateAndSaveReminder(reminder)
        val savedReminder = (fakeDataSource.getReminder(reminder.id) as Result.Success).data
        assertThat(savedReminder.id, `is`(reminder.id))
        val showToast = saveReminderViewModel.showToast.getOrAwaitValue()
        assertThat(
            showToast,
            `is`(
                ApplicationProvider.getApplicationContext<Context>()
                    .getString(R.string.reminder_saved)
            )
        )
        val navigationCommand = saveReminderViewModel.navigationCommand.getOrAwaitValue()
        assertThat(navigationCommand, `is`(NavigationCommand.Back))
    }

    @Test
    fun validateAndSaveReminder_invalid() = runBlockingTest {
        val reminder = ReminderDataItem(null, "description", null, 0.0, 0.0)
        saveReminderViewModel.validateAndSaveReminder(reminder)
        val savedReminder = (fakeDataSource.getReminder(reminder.id) as Result.Error).message
        assertThat(savedReminder, `is`("Reminder was not found"))
    }

}