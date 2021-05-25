package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun before() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun loadReminders_noReminders() = runBlockingTest {
        remindersListViewModel.loadReminders()
        val reminders = remindersListViewModel.remindersList.getOrAwaitValue()
        assertThat(reminders.isEmpty(), `is`(true))
        val showNoData = remindersListViewModel.showNoData.getOrAwaitValue()
        assertThat(showNoData, `is`(true))
    }

    @Test
    fun loadReminders_withReminders() = runBlockingTest {
        val reminder1 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        val reminder2 = ReminderDTO("title", "description", "location", 0.0, 0.0)
        fakeDataSource.dataSource.put(reminder1.id, reminder1)
        fakeDataSource.dataSource.put(reminder2.id, reminder2)
        remindersListViewModel.loadReminders()
        val reminders = remindersListViewModel.remindersList.getOrAwaitValue()
        assertThat(reminders.size, `is`(2))
        val showNoData = remindersListViewModel.showNoData.getOrAwaitValue()
        assertThat(showNoData, `is`(false))
    }

    @Test
    fun loadReminders_error() = runBlockingTest {
        fakeDataSource.error = true
        remindersListViewModel.loadReminders()
        val showSnackBar = remindersListViewModel.showSnackBar.getOrAwaitValue()
        assertThat(showSnackBar, `is`("Test Error"))
    }

    @Test
    fun loadReminders_showLoading() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        var showLoading = remindersListViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoading, `is`(true))
        mainCoroutineRule.resumeDispatcher()
        showLoading = remindersListViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoading, `is`(false))
    }

}