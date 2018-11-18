package me.varunon9.saathmetravel;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import me.varunon9.saathmetravel.ui.place.AddPlaceReviewFragment;
import me.varunon9.saathmetravel.ui.place.PlaceDetailsFragment;
import me.varunon9.saathmetravel.ui.place.PlaceReviewsFragment;
import me.varunon9.saathmetravel.utils.FirestoreDbUtility;
import me.varunon9.saathmetravel.utils.GeneralUtility;
import me.varunon9.saathmetravel.utils.ajax.AjaxUtility;

public class PlaceDetailActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Singleton singleton;
    private String TAG = "PlaceDetailActivity";
    private ProgressDialog progressDialog;
    public FirestoreDbUtility firestoreDbUtility;
    public GeneralUtility generalUtility;
    public AjaxUtility ajaxUtility;
    public Place selectedPlace;
    public String selectedPlaceExtract = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // display back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        singleton = Singleton.getInstance(getApplicationContext());
        firestoreDbUtility = new FirestoreDbUtility();
        generalUtility = new GeneralUtility();
        ajaxUtility = new AjaxUtility(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        String placeId = bundle.getString("id");
        if (placeId != null) {
            if (placeId.equals(singleton.getSourcePlace().getId())) {
                selectedPlace = singleton.getSourcePlace();
            } else {
                selectedPlace = singleton.getDestinationPlace();
            }
        }

        getSupportActionBar().setTitle(selectedPlace.getName());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_place_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0: fragment = new PlaceDetailsFragment();
                    break;
                case 1: fragment = new PlaceReviewsFragment();
                    break;
                case 2: fragment = new AddPlaceReviewFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public void showProgressDialog(String title, String message, boolean isCancellable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(PlaceDetailActivity.this);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(isCancellable);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showMessage(String message) {
        View parentLayout = findViewById(R.id.container);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
