package com.zoctan.smarthub.hubList.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.EncodeUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vansuita.library.Icon;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hubList.presenter.HubListPresenter;
import com.zoctan.smarthub.hubList.view.HubListView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;
import com.zoctan.smarthub.zxing.activity.CaptureActivity;
import com.zyao89.view.zloading.ZLoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class HubListFragment extends BaseFragment implements HubListView {

    private final HubListPresenter mHubListPresenter = new HubListPresenter(this);
    @BindView(R.id.RecyclerView_hub_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout_hub_list)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.FloatingActionButton_hub_list)
    FloatingActionButton mFloatingButton;
    private HubListAdapter mAdapter;
    private List<HubBean> mData;
    //打开扫描界面请求码
    private final static int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private final static int RESULT_OK = 0xA1;
    @BindView(R.id.ZLoadingView_hub_list)
    ZLoadingView zLoadingView;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_list;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        // 固定RecyclerView大小
        mRecyclerView.setHasFixedSize(true);
        // 设置布局管理器
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getHoldingActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置item动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new HubListAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);

        final HubListFragment self = this;
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                HubListFragmentPermissionsDispatcher.openScannerWithPermissionCheck(self);
            }
        });

        setSmartRefreshListener();
        refreshHubList(true);
    }

    private final HubListAdapter.OnItemClickListener mOnItemClickListener = new HubListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final String action, final View view, final int position) {
            if (mData.size() <= 0) {
                return;
            }
            final HubBean hub = mAdapter.getItem(position);
            hub.setAction(action);
            switch (action) {
                case "detail":
                    final Intent intent = new Intent("hub_detail");
                    // 设置启动Activity时携带的参数信息 的Intent
                    final Bundle bundle = new Bundle();
                    bundle.putString("hub_name", hub.getName());
                    bundle.putString("hub_onenet_id", hub.getOnenet_id());
                    bundle.putBoolean("hub_is_electric", hub.getIs_electric());
                    bundle.putBoolean("hub_connected", hub.getConnected());
                    intent.putExtras(bundle);
                    intent.addCategory("hub");
                    startActivity(intent);
                    getHoldingActivity().overridePendingTransition(android.R.anim.slide_in_left, 0);
                    break;
                case "noConnected":
                    AlerterUtil.showDanger(getHoldingActivity(), R.string.hub_msg_not_connected);
                    break;
                case "on":
                case "off":
                    mHubListPresenter.hubOpenClose(hub, userToken);
                    break;
                case "update":
                    showUpdateDialog(hub);
                    break;
                case "delete":
                    showAddOrDeleteDialog(hub, action);
                    break;
            }
        }
    };

    private void showAddOrDeleteDialog(final HubBean hub, final String which) {
        int title = R.string.hub_add;
        int msg = R.string.hub_add_msg;
        int icon = R.drawable.ic_hub_list;
        if (which.equals("delete")) {
            title = R.string.hub_delete;
            msg = R.string.msg_hub_delete;
            icon = R.drawable.ic_alert;
        }
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity()).init(title, msg, icon, R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mHubListPresenter.doHub(hub, userToken);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showUpdateDialog(final HubBean hub) {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_edit_hub, null);
        final TextInputEditText mEtHubName = view.findViewById(R.id.EditText_hub_name);
        final TextInputLayout mLayoutHubName = view.findViewById(R.id.TextInputLayout_hub_name);
        mEtHubName.setText(hub.getName());
        mEtHubName.setSelection(mEtHubName.getText().length());
        mEtHubName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (s.length() > 12) {
                    mLayoutHubName.setErrorEnabled(true);
                    mLayoutHubName.setError(getString(R.string.all_max_name));
                } else {
                    mLayoutHubName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
            }
        });

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.all_edit,
                        null,
                        R.drawable.ic_edit,
                        R.string.all_update);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mEtHubName.getText().length() > 0
                                && mLayoutHubName.getError() == null) {
                            getHoldingActivity().hideSoftKeyBoard(mEtHubName, getContext());
                            hub.setName(mEtHubName.getText().toString());
                            mHubListPresenter.doHub(hub, userToken);
                            dialog.dismiss();
                        }
                    }
                }).show();
    }

    public void refreshHubList(final boolean isShowLoading) {
        if (mData != null) {
            mData.clear();
        }
        mHubListPresenter.loadHubList(userToken, isShowLoading);
    }

    private void setSmartRefreshListener() {
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshHubList(false);
                refreshlayout.finishRefresh(4000/*,false*/);//传入false表示刷新失败
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(4000/*,false*/);//传入false表示加载失败
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HubListFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void openScanner() {
        final Intent intent = new Intent(getContext(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
        getHoldingActivity().overridePendingTransition(android.R.anim.slide_in_left, 0);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    public void showRationale(final PermissionRequest request) {
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.permission_why,
                        R.string.permission_camera,
                        R.drawable.ic_alert,
                        R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        dialog.dismiss();
                        request.proceed();//继续执行请求
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        dialog.dismiss();
                        request.cancel();//取消执行请求
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    public void permissionDenied() {
        AlerterUtil.showDanger(getHoldingActivity(), R.string.permission_denied);
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    public void permissionDeniedNeverAsk() {
        AlerterUtil.showDanger(getHoldingActivity(), R.string.permission_denied_never_ask);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            final Bundle bundle = data.getExtras();
            if (bundle != null) {
                final String scanResult = bundle.getString("qr_scan_result");
                // 扫描出的信息
                if (scanResult != null) {
                    boolean isValidateQR = true;
                    try {
                        // 二维码base64解密
                        final byte[] decodeBytes = EncodeUtils.base64Decode(scanResult);
                        final String decode = new String(decodeBytes, "utf-8");
                        final String[] result = decode.split(" ");
                        final String onenetId = result[0];
                        final String mac = result[1]; // aa:bb:cc:dd:ee:cc
                        // 扫描出的字符串只有两个信息
                        if (result.length != 2) {
                            isValidateQR = false;
                        } else {
                            //AlerterUtil.showInfo(getHoldingActivity(), String.format("Onenet:%s\nMac:%s", onenetId, mac));
                            // onenet的id是一串数字，如果扫出来前半段不是数字会抛错
                            //noinspection ResultOfMethodCallIgnored
                            Long.parseLong(onenetId);
                            // 正则匹配后半段mac地址，如果不对也是无法扫描
                            final String trueMacAddress = "([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}";
                            if (!mac.matches(trueMacAddress)) {
                                isValidateQR = false;
                            } else {
                                final HubBean hub = new HubBean();
                                hub.setOnenet_id(onenetId);
                                hub.setMac(mac);
                                showAddOrDeleteDialog(hub, "add");
                            }
                        }
                    } catch (final Exception e) {
                        isValidateQR = false;
                    }
                    if (!isValidateQR) {
                        AlerterUtil.showDanger(getHoldingActivity(), "请扫描正确的插座二维码哦~");
                    }
                }
            }
        }
    }

    @Override
    public void loadHubList(final List<HubBean> hubList) {
        mData = new ArrayList<>();
        if (hubList != null) {
            mData.addAll(hubList);
            mAdapter.setData(mData);
        } else {
            AlerterUtil.showInfo(getHoldingActivity(), R.string.tip_add_hub);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        zLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showSuccessMsg(final String msg) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshHubList(true);
                AlerterUtil.showInfo(getHoldingActivity(), msg);
            }
        }, 2000);
    }

    @Override
    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }
}

class HubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HubBean> mData;
    private OnItemClickListener mOnItemClickListener;

    public void setData(final List<HubBean> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hub, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final HubBean hub = mData.get(position);
            if (hub == null) {
                return;
            }
            ((ItemViewHolder) holder).mTvHubName.setText((hub.getName()));
            ((ItemViewHolder) holder).mTvHubMac.setText((hub.getMac()));
            final int text1;
            final int text2;
            final Boolean checked;
            if (hub.getIs_electric()) {
                text2 = R.string.hub_is_electric;
                checked = true;
                Icon.on(((ItemViewHolder) holder).mIvHub).color(R.color.accent).icon(R.drawable.ic_hub).put();
            } else {
                text2 = R.string.hub_not_electric;
                Icon.on(((ItemViewHolder) holder).mIvHub).color(R.color.secondary_text).icon(R.drawable.ic_hub).put();
                checked = false;
            }
            if (hub.getConnected()) {
                text1 = R.string.hub_is_connected;
            } else {
                text1 = R.string.hub_not_connected;
            }
            ((ItemViewHolder) holder).mTvHubConnected.setText(text1);
            ((ItemViewHolder) holder).mTvHubElectric.setText(text2);
            ((ItemViewHolder) holder).mSwitchOpenClose.setChecked(checked);
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    HubBean getItem(final int position) {
        return mData.get(position);
    }

    void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String action, View view, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.GridLayout_hub)
        GridLayout mLayoutHub;
        @BindView(R.id.ImageView_hub)
        ImageView mIvHub;
        @BindView(R.id.TextView_hub_name)
        TextView mTvHubName;
        @BindView(R.id.TextView_hub_mac)
        TextView mTvHubMac;
        @BindView(R.id.TextView_hub_connected)
        TextView mTvHubConnected;
        @BindView(R.id.TextView_hub_electric)
        TextView mTvHubElectric;
        @BindView(R.id.Switch_hub_open_close)
        Switch mSwitchOpenClose;

        ItemViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.Switch_hub_open_close, R.id.GridLayout_hub, R.id.Button_hub_edit, R.id.Button_hub_delete})
        public void onClick(final View view) {
            if (mOnItemClickListener == null) {
                return;
            }
            String action = null;
            switch (view.getId()) {
                case R.id.Switch_hub_open_close:
                    if (mTvHubConnected.getText().equals("在线")) {
                        action = mTvHubElectric.getText().equals("已通电") ? "off" : "on";
                    } else {
                        action = "noConnected";
                        // 插座不在线，点开关也没用
                        mSwitchOpenClose.setChecked(!mSwitchOpenClose.isChecked());
                    }
                    break;
                case R.id.GridLayout_hub:
                    action = "detail";
                    break;
                case R.id.Button_hub_edit:
                    action = "update";
                    break;
                case R.id.Button_hub_delete:
                    action = "delete";
                    break;
            }
            mOnItemClickListener.onItemClick(action, view, getLayoutPosition());
        }
    }
}
