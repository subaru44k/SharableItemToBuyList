package com.appsubaruod.sharabletobuylist.views.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.models.InputBoxModel;
import com.appsubaruod.sharabletobuylist.models.ModelManipulator;
import com.appsubaruod.sharabletobuylist.util.FirebaseAnalyticsOperator;
import com.appsubaruod.sharabletobuylist.util.messages.ExpandInputBoxEvent;
import com.appsubaruod.sharabletobuylist.util.messages.StartActionModeEvent;
import com.appsubaruod.sharabletobuylist.views.fragments.CreateChannelFragment;
import com.appsubaruod.sharabletobuylist.views.fragments.InputBoxFragment;
import com.appsubaruod.sharabletobuylist.views.fragments.ItemListDialogFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = MainActivity.class.getName();
    private BottomSheetBehavior mBottomSheetBehavior;
    private ModelManipulator mModelManipulator;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAnalytics = FirebaseAnalyticsOperator.getInstance(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.FragmentInputContainer);
        mBottomSheetBehavior = BottomSheetBehavior.from(layout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(LOG_TAG, "state dragging");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d(LOG_TAG, "state settling");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d(LOG_TAG, "state expanded");
                        // Change InputBox state
                        InputBoxModel.getInstanceIfCreated()
                                .forceSetInputBoxExpansionState(newState);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d(LOG_TAG, "state collapsed");
                        // Change InputBox state
                        InputBoxModel.getInstanceIfCreated()
                                .forceSetInputBoxExpansionState(newState);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(LOG_TAG, "state hidden");
                        break;
                    case BottomSheetBehavior.PEEK_HEIGHT_AUTO:
                        Log.d(LOG_TAG, "state peek height auto");
                        break;
                    default:
                        Log.d(LOG_TAG, "state default");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            // Add fragments
            transaction.add(R.id.FragmentMainContainer, ItemListDialogFragment.newInstance(50));
            transaction.add(R.id.FragmentInputContainer, InputBoxFragment.newInstance());

            transaction.commit();
        }

        mModelManipulator = new ModelManipulator();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startActionMode(StartActionModeEvent event) {
        this.startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.item_selected_menu, menu);
                mModelManipulator.setActionMode(true);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_archive:
                        mModelManipulator.archiveSelectedItems();
                        actionMode.finish();
                        break;
                    default:
                        Log.w(LOG_TAG, "Unsupported item is clicked on ActionMode");
                        break;
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                mModelManipulator.changeToDefaultBackgroundColor();
                mModelManipulator.setActionMode(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mModelManipulator.getInputBoxExpantionState() == BottomSheetBehavior.STATE_EXPANDED) {
            mModelManipulator.toggleInputBox();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.public_channel:
                break;
            case R.id.create_channel:
                CreateChannelFragment fragment = new CreateChannelFragment();
                fragment.show(getFragmentManager(), "fragment");
                break;
            case R.id.nav_share:
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleInputBoxExpansion(ExpandInputBoxEvent event) {
        Log.d(LOG_TAG, "handleInputBoxExpansion");
        mBottomSheetBehavior.setState(event.getExpansionType());
    }
}
