package com.appsubaruod.sharabletobuylist.storage.userdata;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.appsubaruod.sharabletobuylist.storage.UserDataStorage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by s-yamada on 2017/11/23.
 * Read and write data to device local.
 * NOTE : application data can be modified by user one have when root access.
 * If the system reads and writes raw data and accesses to firebase,
 * it have crucial vulnerability since it means arbitrary firebase path can be accessible.
 * The system should consider this case to protect user data appropriately, for example,
 * limit the access to specific path of firebase unless qualified.
 */
public class LocalUserDataStorage implements UserDataStorage {
    private Context mContext;
    private static final String FILENAME = "userdatastorage";
    private static final String LOG_TAG = LocalUserDataStorage.class.getName();


    public LocalUserDataStorage(Context context) {
        mContext = context;
    }


    @Override
    public void writeChannelData(Map<String, String> channelDataMap) {
        try {
            FileOutputStream fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(fos);
            channelDataMap.forEach((channelName, firebaseId) -> pw.println(channelName + ',' + firebaseId));
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public Map<String, String> readChannelData() {
        Map<String, String> channelMap = new HashMap<>();
        try {
            InputStreamReader isr = new InputStreamReader(mContext.openFileInput(FILENAME));
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                channelMap = addToChannelMap(channelMap, line);
            }
            br.close();
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "channel file not found.");
            return new HashMap<>();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return channelMap;
    }

    private Map addToChannelMap(@NonNull Map<String, String> channelMap, String line) {
        String[] splittedLine = line.split(",");
        Log.d(LOG_TAG, line);
        channelMap.put(splittedLine[0], splittedLine[1]);
        return channelMap;
    }
}
