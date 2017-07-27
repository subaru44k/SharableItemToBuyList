package com.appsubaruod.sharabletobuylist.storage.interpretator;

import com.appsubaruod.sharabletobuylist.BuildConfig;
import com.appsubaruod.sharabletobuylist.di.DaggerStorageInterpretatorComponent;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by s-yamada on 2017/07/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(packageName = "com.appsubaruod.sharabletobuylist.storage.interpretator", constants = BuildConfig.class, sdk = 21)
public class MockInterpretatorTest {
    public static final String TEST_ITEM = "hoge";
    @Inject StorageInterpretator mInterpretator;
    private List<String> addedList;
    private List<String> completedList;
    private CountDownLatch mLatch;

    public MockInterpretatorTest() {
        mInterpretator = DaggerStorageInterpretatorComponent.create().inject();
    }

    @Before
    public void setUp() {
        addedList = new ArrayList<>();
        completedList = new ArrayList<>();
        mLatch = new CountDownLatch(1);
    }

    @Test
    public void add() throws Exception {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                addedList.add(itemAdded);
                mLatch.countDown();
            }

            @Override
            public void onItemCompleted(String itemCompleted) {
                completedList.add(itemCompleted);
            }
        });
        mInterpretator.add(TEST_ITEM);
        mLatch.await(1, TimeUnit.SECONDS);
        assertThat(addedList, hasItem(TEST_ITEM));
        assertThat(completedList, not(hasItem(TEST_ITEM)));
    }

    @Test
    public void setCompleted() throws Exception {

        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
            }

            @Override
            public void onItemCompleted(String itemCompleted) {
                completedList.add(TEST_ITEM);
                mLatch.countDown();
            }
        });
        mInterpretator.setCompleted(TEST_ITEM);
        mLatch.await(1, TimeUnit.SECONDS);
        assertThat(addedList, not(hasItem(TEST_ITEM)));
        assertThat(completedList, hasItem(TEST_ITEM));
    }

}