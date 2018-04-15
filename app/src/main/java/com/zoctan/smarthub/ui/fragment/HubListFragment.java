package com.zoctan.smarthub.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.blankj.utilcode.util.EncodeUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.model.bean.smart.OrderBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.HubListPresenter;
import com.zoctan.smarthub.ui.adapter.HubListAdapter;
import com.zoctan.smarthub.ui.base.BaseFragment;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;
import com.zoctan.smarthub.zxing.activity.CaptureActivity;
import com.zyao89.view.zloading.ZLoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class HubListFragment extends BaseFragment {
    @BindView(R.id.RecyclerView_hub_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout_hub_list)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.FloatingActionButton_hub_list)
    FloatingActionButton mFloatingButton;
    @BindView(R.id.ZLoadingView_hub_list)
    ZLoadingView zLoadingView;
    private HubListAdapter mAdapter;
    private List<HubBean> mData;
    //打开扫描界面请求码
    private final static int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private final static int RESULT_OK = 0xA1;
    private final HubListPresenter mPresenter = new HubListPresenter(this);

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_list;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return mPresenter;
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
        mFloatingButton.setOnClickListener(v -> HubListFragmentPermissionsDispatcher.openScannerWithPermissionCheck(self));

        setSmartRefreshListener();
        refreshHubList();
    }

    private final HubListAdapter.OnItemClickListener mOnItemClickListener = new HubListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final String action, final View view, final int position) {
            if (mData.size() <= 0) {
                return;
            }
            final HubBean hub = mAdapter.getItem(position);
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
                    mPresenter.openCloseHub(new OrderBean(hub.getOnenet_id(), "turn", 1));
                    break;
                case "off":
                    mPresenter.openCloseHub(new OrderBean(hub.getOnenet_id(), "turn", 0));
                    break;
                case "update":
                    showUpdateDialog(hub);
                    break;
                case "delete":
                    showAddOrDeleteDialog(hub, "delete");
                    break;
            }
        }
    };

    private void showAddOrDeleteDialog(final HubBean hub, final String action) {
        int title = R.string.hub_add;
        int msg = R.string.hub_add_msg;
        int icon = R.drawable.ic_hub_list;
        if (action.equals("delete")) {
            title = R.string.hub_delete;
            msg = R.string.msg_hub_delete;
            icon = R.drawable.ic_alert;
        }
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity()).init(title, msg, icon, R.string.all_ensure);
        dialog
                .setButton1Click(v -> {
                    mPresenter.crudHub(hub, action);
                    dialog.dismiss();
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
                .setButton1Click(v -> {
                    if (mEtHubName.getText().length() > 0
                            && mLayoutHubName.getError() == null) {
                        getHoldingActivity().hideSoftKeyBoard(mEtHubName, getContext());
                        hub.setName(mEtHubName.getText().toString());
                        mPresenter.crudHub(hub, "update");
                        dialog.dismiss();
                    }
                }).show();
    }

    public void refreshHubList() {
        if (mData != null) {
            mData.clear();
        }
        mPresenter.listHub();
    }

    private void setSmartRefreshListener() {
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshHubList();
            refreshLayout.finishRefresh(4000/*,false*/);//传入false表示刷新失败
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> refreshLayout.finishLoadMore(4000/*,false*/));//传入false表示加载失败
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
                .setButton1Click(v -> {
                    dialog.dismiss();
                    request.proceed();//继续执行请求
                })
                .setButton2Click(v -> {
                    dialog.dismiss();
                    request.cancel();//取消执行请求
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

    public void showLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        zLoadingView.setVisibility(View.GONE);
    }

    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }
}