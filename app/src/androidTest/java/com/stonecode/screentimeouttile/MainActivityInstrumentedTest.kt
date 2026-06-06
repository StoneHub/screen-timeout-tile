package com.stonecode.screentimeouttile

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @Test
    fun shouldUseExpectedPackageName_whenAppContextIsLoaded() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertEquals("com.stonecode.screentimeouttile", appContext.packageName)
    }

    @Test
    fun shouldShowPermissionAndTileCtas_whenActivityLaunches() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.permissionButton)).check(matches(isDisplayed()))
            onView(withId(R.id.tileButton)).check(matches(isDisplayed()))
            onView(withId(R.id.nextAction)).check(matches(isDisplayed()))
        }
    }
}
