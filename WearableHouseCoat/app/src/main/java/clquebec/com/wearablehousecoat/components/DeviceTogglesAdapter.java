package clquebec.com.wearablehousecoat.components;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import clquebec.com.framework.location.Place;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 04/02/18
 */

public class DeviceTogglesAdapter extends RecyclerView.Adapter<DeviceTogglesAdapter.ViewHolder> {
    private DeviceGroup mDeviceGroup;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setDeviceGroup(DeviceGroup group){
        mDeviceGroup = group;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Button mButton;
        ViewHolder(View toggle){
            super(toggle);

            mButton = (Button) toggle;
        }

        @Override
        public void onClick(View view) {
            mButton.callOnClick();
        }
    }
}
