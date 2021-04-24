package com.tuya.dev.iotos.devices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.dev.common.utils.LogUtils;
import com.tuya.dev.devices.bean.AssetDeviceBean;
import com.tuya.dev.devices.bean.AssetDeviceListBean;
import com.tuya.dev.devices.business.DeviceBusiness;
import com.tuya.dev.iotos.R;
import com.tuya.dev.iotos.devices.adapter.DevicesAdapter;
import com.tuya.dev.iotos.env.Constant;
import com.tuya.dev.network.business.BusinessResponse;
import com.tuya.dev.network.request.ResultListener;

/**
 * DevicesInAssetActivity
 *
 * @author xiaoxiao <a href="mailto:developer@tuya.com"/>
 * @since 2021/3/22 7:46 PM
 */
public class DevicesInAssetActivity extends AppCompatActivity implements DevicesAdapter.OnRecyclerItemClickListener {

    private Context mContext;
    private String countryCode;
    private String assetId;

    private RecyclerView mRcList;
    private DevicesAdapter mAdapter;
    private DeviceBusiness deviceBusiness;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_in_asset);
        initView();
        mContext = this;

        Intent intent = getIntent();
        if (intent != null) {
            countryCode = intent.getStringExtra(Constant.INTENT_KEY_COUNTRY_CODE);
            assetId = intent.getStringExtra(Constant.INTENT_KEY_ASSET_ID);
        }

        deviceBusiness = new DeviceBusiness(countryCode);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRcList.setLayoutManager(layoutManager);
        mAdapter = new DevicesAdapter(mContext);
        mAdapter.setListener(this);
        mRcList.setAdapter(mAdapter);
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        loadData();
    }

    private void initView() {
        mRcList = (RecyclerView) findViewById(R.id.rc_list);
        Toolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    private void loadData() {
        deviceBusiness.queryDevicesByAssetId(assetId, new ResultListener<AssetDeviceListBean>() {
            @Override
            public void onFailure(BusinessResponse bizResponse, AssetDeviceListBean bizResult, String apiName) {
                Toast.makeText(mContext, bizResponse.getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(BusinessResponse bizResponse, AssetDeviceListBean bizResult, String apiName) {
                if (mAdapter != null) {
                    mAdapter.setData(bizResult);
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, AssetDeviceBean deviceBean) {

    }

    @Override
    public void onItemLongClick(View view, AssetDeviceBean deviceBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirm to remove the Device?")
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDevice(deviceBean);
                    }
                })
                .setNegativeButton("cancel", null);
        builder.show();
    }

    private void deleteDevice(AssetDeviceBean deviceBean) {
        deviceBusiness.deleteDeviceId(deviceBean.getDevice_id(), new ResultListener<Boolean>() {
            @Override
            public void onFailure(BusinessResponse bizResponse, Boolean bizResult, String apiName) {
                LogUtils.d("delete device", String.valueOf(bizResponse));
            }

            @Override
            public void onSuccess(BusinessResponse bizResponse, Boolean bizResult, String apiName) {
                Toast.makeText(mContext, "delete success", Toast.LENGTH_SHORT).show();
                loadData();
            }
        });
    }
}
