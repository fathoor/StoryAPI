package com.fathoor.storyapi.view.ui

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.fathoor.storyapi.R
import com.fathoor.storyapi.helper.JsonConverter
import com.fathoor.storyapi.model.remote.retrofit.ApiConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private val mockWebServer = MockWebServer()
    private lateinit var idlingResource: CountingIdlingResource

    @Before
    fun setup() {
        mockWebServer.start(1337)
        ApiConfig.BASE_URL = "http://127.0.0.1:1337/"

        idlingResource = CountingIdlingResource("MainActivityTest")
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    @MediumTest
    fun mainActivity_showLogoutButton() {
        launchActivity<MainActivity>()

        onView(withId(R.id.action_logout)).check(matches(isDisplayed()))
    }

    @Test
    @LargeTest
    fun loginActivity_showLogoutSuccess() {
        Intents.init()
        launchActivity<MainActivity>()

        idlingResource.increment()
        val mockResponse = MockResponse().setResponseCode(200).setBody(JsonConverter.readStringFromFile("story_response.json"))
        mockWebServer.enqueue(mockResponse)
        idlingResource.decrement()

        onView(withId(R.id.action_logout)).perform(click())
        onView(withText(R.string.dialog_logout)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(R.string.dialog_yes)).inRoot(isDialog()).perform(click())

        intended(hasComponent(OnboardActivity::class.java.name))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }
}