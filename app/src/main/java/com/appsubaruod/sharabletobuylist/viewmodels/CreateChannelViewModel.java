package com.appsubaruod.sharabletobuylist.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.appsubaruod.sharabletobuylist.BR;
import com.appsubaruod.sharabletobuylist.models.CreateChannelModel;

/**
 * Created by s-yamada on 2017/11/20.
 */

public class CreateChannelViewModel extends BaseObservable {

    private String channelNameText;
    private CreateChannelModel mModel;

    public CreateChannelViewModel() {
        mModel = new CreateChannelModel();
    }

    @Bindable
    public void setChannelNameText(String channelName) {
        channelNameText = channelName;
        notifyPropertyChanged(BR.channelNameText);
    }

    public String getChannelNameText() {
        return channelNameText;
    }

    public void create() {
        mModel.createNewChannel(channelNameText);
    }

    public void cancel() {
        mModel.cancelCreatingChannel();
    }

}
