package com.roberts.adrian.bakeit;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.roberts.adrian.bakeit.IdlingResource.MainActivityIdlingResource;
import com.roberts.adrian.bakeit.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.roberts.adrian.bakeit.R.id.list_view_main;
import static org.hamcrest.Matchers.not;

;

/**
 * Created by Adrian on 07/09/2017.
 * Using a MainActivityIdlingResource to
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {
    public static  String RECIPE_NAME;
    public static  String RECIPE_NAME_ERROR ;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivityIdlingResource mainActivityIdlingResource;


    @Before
    public void init() {
        mActivityTestRule.getActivity().getSupportFragmentManager().beginTransaction().commit();
        RECIPE_NAME = mActivityTestRule.getActivity().getString(R.string.test_main_recipe_name);
        RECIPE_NAME_ERROR = mActivityTestRule.getActivity().getString(R.string.test_main_recipe_error);
    }

    @Before
    public void registerIntentServiceIdlingResource() {
        MainActivity activity = mActivityTestRule.getActivity();
        mainActivityIdlingResource = new MainActivityIdlingResource(activity);
        IdlingRegistry.getInstance().register(mainActivityIdlingResource);

    }




    @Test
    public void testRecyclerView_OnClickToDetailActivity() {
        onView(withId(list_view_main)).check(matches(isDisplayed()));
        onView(withId(R.id.list_view_main))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Checks that the DetailActivity opens with the correct name displayed
        onView(withId(R.id.text_view_recipe_name)).check(matches(withText(RECIPE_NAME)));
        onView(withId(R.id.text_view_recipe_name)).check(matches(not(withText(RECIPE_NAME_ERROR))));

    }

    @After
    public void unregisterIdlingResource() {
        if (mainActivityIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mainActivityIdlingResource);
        }

    }
}
