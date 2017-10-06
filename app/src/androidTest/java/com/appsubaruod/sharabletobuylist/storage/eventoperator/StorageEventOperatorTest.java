package com.appsubaruod.sharabletobuylist.storage.eventoperator;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by s-yamada on 2017/08/11.
 */
@RunWith(AndroidJUnit4.class)
public class StorageEventOperatorTest {
    public static final int TIMEOUT = 500;
    StorageEventOperator mOperator;
    CountDownLatch mLatch;
    public static final int MILLIS = 200;

    @Before
    public void setUp() {
        mOperator = new StorageEventOperator(InstrumentationRegistry.getTargetContext());
        mOperator.removeAllItems();
        mLatch = new CountDownLatch(1);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void addAndGetItem() {
        mOperator.addItem("hoge");
        waitAWhile();
        mOperator.getItemsAsync((itemList) -> {
            assertThat(itemList.size(), is(1));
            assertThat(itemList.get(0).getItemName(), equalTo("hoge"));
            mLatch.countDown();
        });
        try {
            if (!mLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                fail("add timeout");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getEmptyItem() {
        mOperator.getItemsAsync((itemList) -> {
            assertThat(itemList.size(), is(0));
            mLatch.countDown();
        });
        try {
            if (!mLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                fail("timeout");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void removeItem() {
        mOperator.addItem("hoge");
        waitAWhile();
        mOperator.removeItem("hoge");
        waitAWhile();
        mOperator.getItemsAsync((itemList) -> {
            assertThat(itemList.size(), is(0));
            mLatch.countDown();
        });
        try {
            if (!mLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                fail("timeout");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void removeAllItemsWithEmptyDb() {
        mOperator.removeAllItems();
        waitAWhile();
        mOperator.getItemsAsync((itemList) -> {
            assertThat(itemList.size(), is(0));
            mLatch.countDown();
        });
        try {
            if (!mLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                fail("timeout");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void removeAllItems() {
        mOperator.addItem("hoge");
        waitAWhile();
        mOperator.addItem("fuga");
        waitAWhile();
        mOperator.removeAllItems();
        waitAWhile();
        mOperator.getItemsAsync((itemList) -> {
            assertThat(itemList.size(), is(0));
            mLatch.countDown();
        });
        try {
            if (!mLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                fail("timeout");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void completeItem() {
        mOperator.addItem("hoge");
        waitAWhile();
        mOperator.setItemCompleted("hoge", true);
        waitAWhile();
        mOperator.getItemsAsync((itemList) -> {
            assertThat(itemList.size(), is(1));
            assertThat(itemList.get(0).isBought(), is(true));
            mLatch.countDown();
        });
        try {
            if (!mLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                fail("timeout");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void addCompletedItem() {
        mOperator.addItem("hoge");
        waitAWhile();
        mOperator.setItemCompleted("hoge", true);
        waitAWhile();
        mOperator.addItem("hoge");
        waitAWhile();
        mOperator.getItemsAsync((itemList) -> {
            assertThat(itemList.size(), is(1));
            assertThat(itemList.get(0).isBought(), is(false));
            mLatch.countDown();
        });
        try {
            if (!mLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                fail("timeout");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    private void waitAWhile() {
        try {
            Thread.sleep(MILLIS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }
}