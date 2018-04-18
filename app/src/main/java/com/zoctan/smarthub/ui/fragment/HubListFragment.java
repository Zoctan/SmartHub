package com.zoctan.smarthub.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.model.bean.smart.OrderBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.HubListPresenter;
import com.zoctan.smarthub.ui.activity.ScannerActivity;
import com.zoctan.smarthub.ui.adapter.HubListAdapter;
import com.zoctan.smarthub.ui.base.BaseFragment;
import com.zoctan.smarthub.ui.custom.MyTextWatcher;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;
import com.zyao89.view.zloading.ZLoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class HubListFragment extends BaseFragment implements FragmentUtils.OnBackClickListener {
    @BindView(R.id.RecyclerView_hub_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.SmartRefreshLayout_hub_list)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.ZLoadingView_hub_list)
    ZLoadingView zLoadingView;
    private HubListAdapter mAdapter;
    private List<HubBean> mData;
    private final HubListPresenter mPresenter = new HubListPresenter(this);

    public static HubListFragment newInstance() {
        final Bundle args = new Bundle();
        final HubListFragment fragment = new HubListFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
                    showFailedMsg("插座已离线");
                    break;
                case "on":
                case "off":
                    final int status = action.equals("on") ? 1 : 0;
                    mPresenter.openCloseHub(new OrderBean(hub.getOnenet_id(), "turn", status));
                    break;
                case "update":
                    showUpdateDialog(hub);
                    break;
                case "delete":
                    showDeleteDialog(hub);
                    break;
            }
        }
    };

    private void showAddDialog(final HubBean hub) {
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .setIcon(R.drawable.ic_hub)
                .setTitle(R.string.hub_add)
                .setMessage(R.string.hub_add_msg)
                .setButton1Text(R.string.all_ensure);
        dialog.setButton1Click(v -> {
            mPresenter.crudHub(hub, "add");
            dialog.dismiss();
        }).show();
    }

    private void showDeleteDialog(final HubBean hub) {
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .setIcon(R.drawable.ic_alert)
                .setTitle(R.string.hub_delete)
                .setMessage(R.string.msg_hub_delete)
                .setButton1Text(R.string.all_ensure);
        dialog.setButton1Click(v -> {
            mPresenter.crudHub(hub, "delete");
            dialog.dismiss();
        }).show();
    }

    private void showUpdateDialog(final HubBean hub) {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_edit_hub, null);
        final TextInputEditText mEtHubName = view.findViewById(R.id.EditText_hub_name);
        final TextInputLayout mLayoutHubName = view.findViewById(R.id.TextInputLayout_hub_name);
        mEtHubName.setText(hub.getName());
        mEtHubName.setSelection(mEtHubName.getText().length());
        mEtHubName.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (s.length() > 12) {
                    mLayoutHubName.setErrorEnabled(true);
                    mLayoutHubName.setError(getString(R.string.all_max_name));
                } else {
                    mLayoutHubName.setError(null);
                }
            }
        });

        final NiftyDialog dialog = new NiftyDialogUtil()
                .setView(view, getHoldingActivity())
                .setIcon(R.drawable.ic_edit)
                .setTitle(R.string.all_edit)
                .setMessage(null)
                .setButton1Text(R.string.all_update);
        dialog.setButton1Click(v -> {
            String name = mEtHubName.getText().toString();
            if (StringUtils.isEmpty(name)) {
                this.showFailedMsg("请输入插座名称");
                return;
            }
            if (mLayoutHubName.getError() == null) {
                getHoldingActivity().hideSoftKeyBoard(mEtHubName, getContext());
                HubBean hubBean = new HubBean();
                hubBean.setName(name);
                mPresenter.crudHub(hubBean, "update");
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

    @OnClick(R.id.FloatingActionButton_scanner)
    public void openScanner() {
        // 先检查有没有相机权限
        if (!PermissionUtils.isGranted(Manifest.permission.CAMERA)) {
            this.showFailedMsg("没有相机权限");
            return;
        }
        final IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(ScannerActivity.class);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false); // 扫描成功的「哔」声，默认开启
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == REQUEST_CODE) {
            // 获取解析结果
            final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            String scanResult = null;
            if (resultCode == RESULT_OK) {
                scanResult = result.getContents();
            } else if (resultCode == ScannerActivity.RESULT_CODE_PICK_IMAGE) {
                // 从本地图片二维码获取
                //todo
                if (intent != null) {
                    scanResult = intent.getStringExtra("result");
                }
            }
            if (!StringUtils.isEmpty(scanResult)) {
                boolean isValidateQR = true;
                LogUtils.i(scanResult);
                try {
                    // 二维码base64解密
                    final String[] decode = new String(EncodeUtils.base64Decode(scanResult), "utf-8").split(" ");
                    final String onenetId = decode[0];
                    final String mac = decode[1]; // aa:bb:cc:dd:ee:cc
                    // 扫描出的字符串只有两个信息
                    if (decode.length != 2) {
                        isValidateQR = false;
                    } else {
                        this.showSuccessMsg(String.format("Onenet:%s\nMac:%s", onenetId, mac));
                        // onenet的id是一串数字，如果扫出来前半段不是数字会抛错
                        //noinspection ResultOfMethodCallIgnored
                        Long.parseLong(onenetId);
                        // 正则匹配后半段mac地址，如果不对也是无法扫描
                        if (!mac.matches("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}")) {
                            isValidateQR = false;
                        } else {
                            final HubBean hubBean = new HubBean.Builder()
                                    .onenet_id(onenetId)
                                    .mac(mac)
                                    .build();
                            showAddDialog(hubBean);
                        }
                    }
                } catch (final Exception e) {
                    isValidateQR = false;
                }
                if (!isValidateQR) {
                    this.showFailedMsg("请扫描正确的插座二维码哦~");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    public void loadHubList(final List<HubBean> hubList) {
        mData = new ArrayList<>();
        if (hubList != null) {
            mData.addAll(hubList);
            mAdapter.setData(mData);
        } else {
            this.showFailedMsg("添加一个「插座」吧~");
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

    @Override
    public boolean onBackClick() {
        return false;
    }
}