package com.appsubaruod.sharabletobuylist.util;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by s-yamada on 2017/11/20.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationTaskCoordinatorTest {
    static NotificationTaskCoordinator mNotificationTaskCoordinator;

    @Before
    public void setUp() throws Exception {
        mNotificationTaskCoordinator =
                new NotificationTaskCoordinator(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getNullSequenceOfModification() {
        assertThat(mNotificationTaskCoordinator.getSequenceOfModification(), is(""));
    }

    @Test
    public void getAddedSequence() {
        mNotificationTaskCoordinator.requestAddedNotification("hoge");
        assertThat(mNotificationTaskCoordinator.getSequenceOfModification(), is("hoge is added"));
    }

    @Test
    public void addTwoItems() {
        mNotificationTaskCoordinator.requestAddedNotification("hoge");
        mNotificationTaskCoordinator.requestAddedNotification("hoge2");
        assertThat(mNotificationTaskCoordinator.getSequenceOfModification(), containsString("hoge"));
        assertThat(mNotificationTaskCoordinator.getSequenceOfModification(), containsString("hoge2"));
    }

    @Test
    public void deleteItem() {
        mNotificationTaskCoordinator.requestDeletedNotification("hoge");
        assertThat(mNotificationTaskCoordinator.getSequenceOfModification(), is("hoge is deleted"));
    }

    @Test
    public void completeItem() {
        mNotificationTaskCoordinator.requestCompletedNotification("hoge");
        assertThat(mNotificationTaskCoordinator.getSequenceOfModification(), is("hoge is completed"));
    }

//    @Test
//    public void addDeleteItem() {
//        mNotificationTaskCoordinator.requestAddedNotification("hoge");
//        mNotificationTaskCoordinator.requestDeletedNotification("hoge");
//        assertThat(mNotificationTaskCoordinator.getSequenceOfModification(), is(""));
//    }

}