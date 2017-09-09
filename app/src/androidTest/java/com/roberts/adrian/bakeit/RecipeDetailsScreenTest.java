package com.roberts.adrian.bakeit;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;


/**
 * Created by Adrian on 07/09/2017.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDetailsScreenTest {
    private String recipe_name = "Brownies";
    private int recipe_ide = 1;
    final String INGREDIENT = "350 G Bittersweet chocolate (60-70% cacao)";
    @Rule
    public ActivityTestRule<RecipeDetailzActivity> mActivityTestRule =
            new ActivityTestRule<RecipeDetailzActivity>(RecipeDetailzActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent extras = new Intent(targetContext, RecipeDetailzActivity.class);
                    extras.putExtra(RecipeDetailzActivity.EXTRA_RECIPE_NAME, recipe_name);
                    extras.putExtra(RecipeDetailzActivity.EXTRA_RECIPE_ID, recipe_ide);
                    extras.putExtra(RecipeDetailzActivity.EXTRA_FROM_WIDGET, false);
                    return extras;
                }
            };

    private RecipeDetailsIdlingResource mRecipeDetailsIdlingResource;

    @Before
    public void init() {


    }

    @Before
    public void registerIntentServiceIdlingResource() {
        //DetailsIngredientsFragment fragment = DetailsIngredientsFragment.newInstance(recipe_ide, recipe_name);
        mRecipeDetailsIdlingResource = new RecipeDetailsIdlingResource(mActivityTestRule.getActivity());
        IdlingRegistry.getInstance().register(mRecipeDetailsIdlingResource);
    }

    @Test
    public void showIngredients() {
        onView(withId(R.id.recyclerViewIngredients)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.recyclerViewIngredients)).atPosition(0).check(matches(withText(INGREDIENT)));

    }

    @Test
    public void showSteps_interact() {
        onView(withId(R.id.recyclerViewSteps)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerViewSteps)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.step_details_title)).check(matches(isDisplayed()));
    }


    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(mRecipeDetailsIdlingResource);
    }
}
