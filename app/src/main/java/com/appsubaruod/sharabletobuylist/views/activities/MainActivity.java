package com.appsubaruod.sharabletobuylist.views.activities;

import android.content.Intent;
import android.net.Uri;
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
import android.view.SubMenu;
import android.view.View;
import android.widget.RelativeLayout;

import com.appsubaruod.sharabletobuylist.R;
import com.appsubaruod.sharabletobuylist.models.InputBoxModel;
import com.appsubaruod.sharabletobuylist.models.ModelManipulator;
import com.appsubaruod.sharabletobuylist.util.FirebaseAnalyticsOperator;
import com.appsubaruod.sharabletobuylist.util.messages.ChannelAddedEvent;
import com.appsubaruod.sharabletobuylist.util.messages.ExpandInputBoxEvent;
import com.appsubaruod.sharabletobuylist.util.messages.MultipleChannelAddedEvent;
import com.appsubaruod.sharabletobuylist.util.messages.StartActionModeEvent;
import com.appsubaruod.sharabletobuylist.views.fragments.CreateChannelFragment;
import com.appsubaruod.sharabletobuylist.views.fragments.InputBoxFragment;
import com.appsubaruod.sharabletobuylist.views.fragments.ItemListDialogFragment;
import com.appsubaruod.sharabletobuylist.views.fragments.ShareChannelFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int ID_CHANNEL_ITEM = 100;
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
        mModelManipulator.initializeChannelModel(getApplicationContext());

        handleFirebaseDynamicLink();

    }

    private void handleFirebaseDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Log.d(LOG_TAG, "getDynamicLink:onSuccess");
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        String channelName = deepLink.getQueryParameter("channelName");
                        String channelId = deepLink.getQueryParameter("channelId");

                        Log.d(LOG_TAG, "deepLink : " + deepLink.toString());
                        Log.d(LOG_TAG, "add channel : " + channelName + ", " + channelId);
                        mModelManipulator.addChannel(channelName, channelId);
                        mModelManipulator.changeChannel(channelName);
                    }
                })
                .addOnFailureListener(this, e ->
                        Log.w(LOG_TAG, "getDynamicLink:onFailure", e));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Since android:launchmode="singleTask" is specified, somecases onCreate will not called.
        // Instead, onNewIntent is called.
        // You should investigate each attribute and lifecyle behavior correctly.
        handleFirebaseDynamicLink();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mModelManipulator.cancelNotification();
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
                mModelManipulator.changeToDefaultChannel();
                break;
            case R.id.create_channel:
                CreateChannelFragment fragment = new CreateChannelFragment();
                fragment.show(getFragmentManager(), "createchannel");
                break;
            case R.id.nav_share:
                ArrayList<String> channelList = new ArrayList<>(mModelManipulator.getChannelList());
                ShareChannelFragment shareChannelFragment = ShareChannelFragment.newInstance(channelList);
                shareChannelFragment.show(getFragmentManager(), "sharechannel");
                break;
            case ID_CHANNEL_ITEM:
                mModelManipulator.changeChannel(item.getTitle().toString());
                break;
            default:
                Log.w(LOG_TAG, "Unknown menu id : " + id);
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMultipleChannelAdded(MultipleChannelAddedEvent event) {
        Log.d(LOG_TAG, "onMultipleChannelAdded");
        event.getChannelSet().forEach(channel -> onChannelAdded(new ChannelAddedEvent(channel)));
        EventBus.getDefault().removeStickyEvent(MultipleChannelAddedEvent.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChannelAdded(ChannelAddedEvent event) {
        Log.d(LOG_TAG, "onChannelAdded : " + event.getChannelName());
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = getChannelMenuItem(menu);
        if (item == null) {
            return;
        }
        SubMenu subMenu = item.getSubMenu();
        subMenu.add(Menu.NONE, ID_CHANNEL_ITEM, Menu.NONE, event.getChannelName())
                .setIcon(R.drawable.ic_menu_slideshow);
    }

    private MenuItem getChannelMenuItem(Menu menu) {
        MenuItem item = null;
        for (int i=0; i < menu.size() - 1; i++) {
            item = menu.getItem(i);
            if (getString(R.string.navigation_drawer_channels).equals(item.getTitle())) {
                break;
            }
        }
        return item;
    }
}
