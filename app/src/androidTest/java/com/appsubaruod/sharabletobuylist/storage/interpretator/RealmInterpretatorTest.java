package com.appsubaruod.sharabletobuylist.storage.interpretator;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.di.DaggerStorageInterpretatorComponent;
import com.appsubaruod.sharabletobuylist.di.StorageInterpretatorModule;
import com.appsubaruod.sharabletobuylist.storage.StorageInterpretator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by s-yamada on 2017/07/29.
 */
@RunWith(AndroidJUnit4.class)
public class RealmInterpretatorTest {
    public static final String TEST_ITEM = "hoge";
    public static final String TEST_ITEM2 = "foo";
    @Inject
    StorageInterpretator mInterpretator;
    private List<String> addedList;
    private List<String> completedList;
    private List<String> deletedList;
    private CountDownLatch mLatch;

    public RealmInterpretatorTest() {
        mInterpretator = DaggerStorageInterpretatorComponent.builder()
                .storageInterpretatorModule(new StorageInterpretatorModule(
                        InstrumentationRegistry.getTargetContext())).build().inject();
    }

    @Before
    public void setUp() {
        addedList = new ArrayList<>();
        completedList = new ArrayList<>();
        deletedList = new ArrayList<>();
        mLatch = new CountDownLatch(1);
        mInterpretator.removeAllItems();
    }

    @After
    public void tearDown() {
        mInterpretator.removeAllItems();
    }

    @Test
    public void thereIsNoItem() {
        assertThat(mInterpretator.getAllItems().size(), is(0));
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

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.add(TEST_ITEM);
        mLatch.await(1, TimeUnit.SECONDS);
        assertThat(addedList, hasItem(TEST_ITEM));
        assertThat(completedList, not(hasItem(TEST_ITEM)));

        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        Log.d("item list" , Integer.toString(itemList.size()));
        assertThat(itemList, is(contains(TEST_ITEM)));
    }

    @Test
    public void addSameItem() throws Exception {
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

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.add(TEST_ITEM);
        mLatch.await(1, TimeUnit.SECONDS);

        assertThat(completedList, not(hasItem(TEST_ITEM)));

        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        Log.d("item list" , Integer.toString(itemList.size()));
        assertThat(itemList.size(), is(1));
        assertThat(itemList, is(contains(TEST_ITEM)));
    }

    @Test
    public void addTwoItems() throws Exception {
        mLatch = new CountDownLatch(2);
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                mLatch.countDown();
            }

            @Override
            public void onItemCompleted(String itemCompleted) {

            }

            @Override
            public void onItemDeleted(String itemDeleted) {

            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.add(TEST_ITEM2);

        if (!mLatch.await(1, TimeUnit.SECONDS)) {
            fail("onItemAdded does not called twice");
        }
        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        assertThat(itemList, is(contains(TEST_ITEM, TEST_ITEM2)));
    }

    @Test
    public void setCompleted() throws Exception {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                addedList.add(itemAdded);
            }

            @Override
            public void onItemCompleted(String itemCompleted) {
                completedList.add(itemCompleted);
                mLatch.countDown();
            }

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.setCompleted(TEST_ITEM);
        mLatch.await(1, TimeUnit.SECONDS);

        assertThat(addedList, hasItem(TEST_ITEM));
        assertThat(completedList, hasItem(TEST_ITEM));
    }

    @Test
    public void setCompletedWithoutAdding() throws Exception {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                addedList.add(itemAdded);
            }

            @Override
            public void onItemCompleted(String itemCompleted) {
                completedList.add(itemCompleted);
                mLatch.countDown();
            }

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.setCompleted(TEST_ITEM);
        mLatch.await(1, TimeUnit.SECONDS);

        assertThat(addedList, not(hasItem(TEST_ITEM)));
        assertThat(completedList, not(hasItem(TEST_ITEM)));
    }

    @Test
    public void removeItemWithoutData() throws Exception {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {

            }

            @Override
            public void onItemCompleted(String itemCompleted) {

            }

            @Override
            public void onItemDeleted(String itemDeleted) {
                deletedList.add(itemDeleted);
                mLatch.countDown();
            }
        });
        mInterpretator.removeItem(TEST_ITEM);
        if (mLatch.await(1, TimeUnit.SECONDS)) {
            fail("onItemDeleted was called without deletion");
        }
        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        assertThat(itemList.size(), is(0));
    }

    @Test
    public void removeItem() throws Exception {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {

            }

            @Override
            public void onItemCompleted(String itemCompleted) {

            }

            @Override
            public void onItemDeleted(String itemDeleted) {
                deletedList.add(itemDeleted);
                mLatch.countDown();
            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.removeItem(TEST_ITEM);
        if (!mLatch.await(1, TimeUnit.SECONDS)) {
            fail("onItemDeleted was not called");
        }
        List<String> itemList = mInterpretator.getAllItems()
                .stream().map(item -> item.getItemName()).collect(Collectors.toList());
        assertThat(itemList.size(), is(0));
    }

    @Test
    public void removeItem2() throws Exception {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {

            }

            @Override
            public void onItemCompleted(String itemCompleted) {

            }

            @Override
            public void onItemDeleted(String itemDeleted) {
                deletedList.add(itemDeleted);
                mLatch.countDown();
            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.add(TEST_ITEM2);
        mInterpretator.removeItem(TEST_ITEM);
        if (!mLatch.await(1, TimeUnit.SECONDS)) {
            fail("onItemDeleted was not called");
        }
        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        assertThat(itemList.size(), is(1));
        assertThat(itemList, is(contains(TEST_ITEM2)));
    }
}