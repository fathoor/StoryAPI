package com.fathoor.storyapi.view.ui

import com.fathoor.storyapi.R
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.fathoor.storyapi.BuildConfig
import com.fathoor.storyapi.helper.JsonConverter
import com.fathoor.storyapi.model.remote.retrofit.ApiConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    private val mockWebServer = MockWebServer()
    private lateinit var idlingResource: CountingIdlingResource

    @Before
    fun setup() {
        mockWebServer.start(1337)
        ApiConfig.BASE_URL = "http://127.0.0.1:1337/"

        idlingResource = CountingIdlingResource("LoginActivityTest")
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    @MediumTest
    fun loginActivity_showCorrectForm() {
        launchActivity<LoginActivity>()

        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }

    @Test
    @LargeTest
    fun loginActivity_showLoginSuccess() {
        Intents.init()
        launchActivity<LoginActivity>()

        idlingResource.increment()
        val mockResponse = MockResponse().setResponseCode(200).setBody(JsonConverter.readStringFromFile("login_response.json"))
        mockWebServer.enqueue(mockResponse)
        idlingResource.decrement()

        onView(withId(R.id.ed_login_email)).perform(typeText(BuildConfig.TEST_EMAIL))
        onView(withId(R.id.ed_login_password)).perform(typeText(BuildConfig.TEST_PASSWORD))
        onView(withId(R.id.btn_login)).perform(scrollTo(), click())

        intended(hasComponent(MainActivity::class.java.name))
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
    }
}