package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FitPlanRepository
import com.example.notifications.FitPlanNotificationHelper
import com.example.notifications.ReminderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FITPLAN", appName)
  }

  @Test
  fun `repository has initial workouts and meals`() {
    val repo = FitPlanRepository()
    val workouts = repo.workouts.value
    assertTrue("Workouts should not be empty", workouts.isNotEmpty())
    assertTrue("Should include Home Workout", workouts.any { it.title.contains("Home", ignoreCase = true) })
    assertTrue("Should include Yoga", workouts.any { it.title.contains("Yoga", ignoreCase = true) })

    val meals = repo.meals.value
    assertTrue("Meals should not be empty", meals.isNotEmpty())
    assertEquals(4, meals.size)
  }

  @Test
  fun `repository water tracker works`() {
    val repo = FitPlanRepository()
    val initial = repo.waterGlasses.value
    repo.addWaterGlass()
    assertEquals(initial + 1, repo.waterGlasses.value)
    repo.removeWaterGlass()
    assertEquals(initial, repo.waterGlasses.value)
    repo.setWaterGlasses(8)
    assertEquals(8, repo.waterGlasses.value)
    repo.resetWater()
    assertEquals(0, repo.waterGlasses.value)
  }

  @Test
  fun `reminder system default settings and toggles`() {
    val repo = FitPlanRepository()
    val settings = repo.reminderSettings.value
    assertNotNull(settings)
    assertEquals(5, settings.reminders.size)

    val workoutReminder = settings.reminders.firstOrNull { it.type == ReminderType.WORKOUT }
    assertNotNull(workoutReminder)
    assertTrue(workoutReminder!!.isEnabled)
    assertEquals(7, workoutReminder.hour)
    assertEquals(0, workoutReminder.minute)

    // Toggle reminder off
    repo.toggleReminder(ReminderType.WORKOUT, false)
    val updatedWorkout = repo.reminderSettings.value.reminders.first { it.type == ReminderType.WORKOUT }
    assertEquals(false, updatedWorkout.isEnabled)

    // Update reminder time to 6:30 AM
    repo.updateReminderTime(ReminderType.WORKOUT, 6, 30)
    val timeUpdated = repo.reminderSettings.value.reminders.first { it.type == ReminderType.WORKOUT }
    assertEquals(6, timeUpdated.hour)
    assertEquals(30, timeUpdated.minute)
    assertEquals("6:30 AM", timeUpdated.formattedTime)
  }

  @Test
  fun `theme mode state and persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = FitPlanRepository()
    repo.initPreferences(context)

    // Default theme should be Dark
    assertEquals(com.example.model.AppThemeMode.DARK, repo.themeMode.value)

    // Switch to Light
    repo.setThemeMode(com.example.model.AppThemeMode.LIGHT, context)
    assertEquals(com.example.model.AppThemeMode.LIGHT, repo.themeMode.value)
    assertEquals(com.example.model.AppThemeMode.LIGHT, repo.userProfile.value.themeMode)

    // Toggle back to Dark
    repo.toggleTheme(context)
    assertEquals(com.example.model.AppThemeMode.DARK, repo.themeMode.value)

    // Test SharedPreferences directly via FitPlanPreferences
    assertEquals(com.example.model.AppThemeMode.DARK, com.example.data.FitPlanPreferences.getThemeMode(context))
    com.example.data.FitPlanPreferences.saveThemeMode(context, com.example.model.AppThemeMode.SYSTEM)
    assertEquals(com.example.model.AppThemeMode.SYSTEM, com.example.data.FitPlanPreferences.getThemeMode(context))
  }

  @Test
  fun `notification helper channel and test alert`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    FitPlanNotificationHelper.createNotificationChannel(context)
    FitPlanNotificationHelper.sendTestNotification(context, ReminderType.WORKOUT)
    FitPlanNotificationHelper.sendTestNotification(context, ReminderType.BREAKFAST)
  }
}
