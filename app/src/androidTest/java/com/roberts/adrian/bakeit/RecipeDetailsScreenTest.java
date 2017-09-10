package com.roberts.adrian.bakeit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.roberts.adrian.bakeit.activities.RecipeDetailzActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
import static com.google.android.exoplayer2.util.Assertions.checkNotNull;


/**
 * Created by Adrian on 07/09/2017.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeDetailsScreenTest {
    private String recipe_name = "Brownies";
    private int recipe_ide = 2;
    private final String INGREDIENT = "Bittersweet chocolate (60-70% cacao)";//"100 G light brown sugar";//
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
    public void registerIntentServiceIdlingResource() {
        //DetailsIngredientsFragment fragment = DetailsIngredientsFragment.newInstance(recipe_ide, recipe_name);
        mRecipeDetailsIdlingResource = new RecipeDetailsIdlingResource(mActivityTestRule.getActivity());
        IdlingRegistry.getInstance().register(mRecipeDetailsIdlingResource);
    }

    @Test
    public void showIngredients_specificIngredient() {
        onView(withId(R.id.recyclerViewIngredients)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerViewIngredients)).perform(RecyclerViewActions.scrollToPosition(3)).check(
                matches((isDisplayed())) //withText(INGREDIENT))
        );
       
        onView(withId(R.id.recyclerViewIngredients))
                .check(matches(atPosition(0, R.id.ingredient_name,withText(INGREDIENT))));
        // onData(anything()).inAdapterView(withId(R.id.recyclerViewIngredients)).atPosition(0).check(matches(withText(INGREDIENT)));

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

    public static Matcher<View> atPosition(final int position, final int textView, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView.findViewById(textView));
            }
        };
    }
}
