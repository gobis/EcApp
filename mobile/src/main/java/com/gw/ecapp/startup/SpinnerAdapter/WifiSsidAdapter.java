package com.gw.ecapp.startup.SpinnerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gw.ecapp.R;
import com.gw.ecapp.engine.udpEngine.EngineUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by iningosu on 8/29/2017.
 */

public class WifiSsidAdapter extends BaseAdapter {

    private Context mContext;

    private  ArrayList<HashMap<String, String>> mMasterData;


    public WifiSsidAdapter(Context context){
        mContext = context ;
    }

    public void setData(ArrayList<HashMap<String, String>> data){
        mMasterData = data;
    }

    @Override
    public int getCount() {
        int count = 0 ;

        if(null != mMasterData){
            count = mMasterData.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.wifi_list_row, parent, false);
            viewHolder.wifiName = (TextView) convertView.findViewById(R.id.wifi_name);
            viewHolder.wifiStrength = (ImageView) convertView.findViewById(R.id.wifi_strength);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HashMap wifiMap = mMasterData.get(position);

        viewHolder.wifiName.setText(wifiMap.get(EngineUtils.SSID).toString());

        int wifiLevel = Integer.parseInt(wifiMap.get(EngineUtils.WIFI_LEVEL).toString());

        switch (wifiLevel) {
            case 2:
                viewHolder.wifiStrength.setImageResource(R.drawable.assetswifi__range_4b);
                break;
            case 3:
                viewHolder.wifiStrength.setImageResource(R.drawable.assetswifi__range_3c);
                break;
            case 4:
                viewHolder.wifiStrength.setImageResource(R.drawable.assetswifi__range_2c);
                break;
            case 5:
                viewHolder.wifiStrength.setImageResource(R.drawable.assetswifi__range_1c);
                break;
            default:
                viewHolder.wifiStrength.setImageResource(R.drawable.assetswifi__range_4b);
                break;
        }

        return convertView;
    }


    static class ViewHolder {
        ImageView wifiStrength;
        TextView wifiName;
    }
}
