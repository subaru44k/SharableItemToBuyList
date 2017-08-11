package com.appsubaruod.sharabletobuylist.storage.interpretator;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.di.DaggerStorageInterpretatorComponent;
import com.appsubaruod.sharabletobuylist.di.StorageInterpretatorModule;
import com.appsubaruod.sharabletobuylist.models.Item;
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
    private boolean isCompletedValue;
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
    public void add() {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                addedList.add(itemAdded);
                mLatch.countDown();
            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {
                completedList.add(itemCompleted);
            }

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.add(TEST_ITEM);
        try {
            mLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        assertThat(addedList, hasItem(TEST_ITEM));
        assertThat(completedList, not(hasItem(TEST_ITEM)));

        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        Log.d("item list" , Integer.toString(itemList.size()));
        assertThat(itemList, is(contains(TEST_ITEM)));
    }

    @Test
    public void addSameItem() {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                addedList.add(itemAdded);
                mLatch.countDown();
            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {
                completedList.add(itemCompleted);
            }

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.add(TEST_ITEM);
        try {
            mLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        assertThat(completedList, not(hasItem(TEST_ITEM)));

        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        Log.d("item list" , Integer.toString(itemList.size()));
        assertThat(itemList.size(), is(1));
        assertThat(itemList, is(contains(TEST_ITEM)));
    }

    @Test
    public void addTwoItems() {
        mLatch = new CountDownLatch(2);
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                mLatch.countDown();
            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {

            }

            @Override
            public void onItemDeleted(String itemDeleted) {

            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.add(TEST_ITEM2);

        try {
            if (!mLatch.await(1, TimeUnit.SECONDS)) {
                fail("onItemAdded does not called twice");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        assertThat(itemList, is(contains(TEST_ITEM, TEST_ITEM2)));
    }

    @Test
    public void setCompleted() {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                addedList.add(itemAdded);
            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {
                completedList.add(itemCompleted);
                mLatch.countDown();
            }

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.setCompleted(TEST_ITEM, true);
        try {
            mLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        assertThat(addedList, hasItem(TEST_ITEM));
        assertThat(completedList, hasItem(TEST_ITEM));
    }

    @Test
    public void setCompletedTwice() {
        mLatch = new CountDownLatch(2);
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {

            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {
                mLatch.countDown();
                isCompletedValue = isCompleted;
            }

            @Override
            public void onItemDeleted(String itemDeleted) {

            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.setCompleted(TEST_ITEM, true);
        mInterpretator.setCompleted(TEST_ITEM, true);

        try {
            if (mLatch.await(1, TimeUnit.SECONDS)) {
                fail("onItemCompleted is unexpectedly called twice.");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        List<Item> allItem = mInterpretator.getAllItems();
        assertThat(allItem.get(0).isBought(), is(true));
        assertThat(isCompletedValue, is(true));
    }

    @Test
    public void setCompletedToFalse() {
        mLatch = new CountDownLatch(2);
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {

            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {
                mLatch.countDown();
                isCompletedValue = isCompleted;
            }

            @Override
            public void onItemDeleted(String itemDeleted) {

            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.setCompleted(TEST_ITEM, true);
        mInterpretator.setCompleted(TEST_ITEM, false);

        try {
            if (!mLatch.await(1, TimeUnit.SECONDS)) {
                fail("onItemCompleted is not called twice.");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        List<Item> allItem = mInterpretator.getAllItems();
        assertThat(allItem.get(0).isBought(), is(false));
        assertThat(isCompletedValue, is(false));
    }

    @Test
    public void setCompletedWithoutAdding() {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                addedList.add(itemAdded);
            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {
                completedList.add(itemCompleted);
                mLatch.countDown();
            }

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.setCompleted(TEST_ITEM, true);
        try {
            mLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }

        assertThat(addedList, not(hasItem(TEST_ITEM)));
        assertThat(completedList, not(hasItem(TEST_ITEM)));
    }

    @Test
    public void setCompletedAndAdd() {
        mLatch = new CountDownLatch(2);
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {
                addedList.add(itemAdded);
            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {
                completedList.add(itemCompleted);
                mLatch.countDown();
            }

            @Override
            public void onItemDeleted(String itemDeleted) {
            }
        });
        mInterpretator.add(TEST_ITEM);
        mInterpretator.setCompleted(TEST_ITEM, true);
        mInterpretator.add(TEST_ITEM);
        try {
            mLatch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        assertThat(completedList.size(), is(2));
        assertThat(completedList.get(0), is(completedList.get(1)));
    }

    @Test
    public void removeItemWithoutData() {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {

            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {

            }

            @Override
            public void onItemDeleted(String itemDeleted) {
                deletedList.add(itemDeleted);
                mLatch.countDown();
            }
        });
        mInterpretator.removeItem(TEST_ITEM);
        try {
            if (mLatch.await(1, TimeUnit.SECONDS)) {
                fail("onItemDeleted was called without deletion");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
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
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {

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
    public void removeItem2() {
        mInterpretator.registerStorageEventListener(new StorageInterpretator.StorageEvent() {
            @Override
            public void onItemAdded(String itemAdded) {

            }

            @Override
            public void onItemCompleted(String itemCompleted, boolean isCompleted) {

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
        try {
            if (!mLatch.await(1, TimeUnit.SECONDS)) {
                fail("onItemDeleted was not called");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        List<String> itemList = mInterpretator.getAllItems().stream()
                .map(item -> item.getItemName()).collect(Collectors.toList());
        assertThat(itemList.size(), is(1));
        assertThat(itemList, is(contains(TEST_ITEM2)));
    }
}