package com.clquebec.wearablehousecoat.components;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.controllable.ControllableDeviceGroup;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public class DeviceTogglesAdapter extends RecyclerView.Adapter<DeviceTogglesAdapter.ViewHolder> {
    private ControllableDeviceGroup mDeviceGroup;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DeviceControlButton button = new DeviceControlButton(parent.getContext());

        return new ViewHolder(button);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDeviceGroup != null) {
            holder.attachDevice(mDeviceGroup.getDevices().get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mDeviceGroup != null) {
            return mDeviceGroup.getDevices().size();
        } else {
            return 0;
        }
    }

    public DeviceTogglesAdapter(ControllableDeviceGroup group) {
        mDeviceGroup = group;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private DeviceControlButton mButton;

        ViewHolder(View toggle) {
            super(toggle);

            mButton = (DeviceControlButton) toggle;
        }

        public void attachDevice(ControllableDevice d) {
            mButton.attachDevice(d);
        }

        @Override
        public void onClick(View view) {
            mButton.callOnClick();
        }
    }
}
