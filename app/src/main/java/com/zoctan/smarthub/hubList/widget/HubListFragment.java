package com.zoctan.smarthub.hubList.widget;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vansuita.library.Icon;
import com.zoctan.smarthub.App;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hubList.presenter.HubListPresenter;
import com.zoctan.smarthub.hubList.view.HubListView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;
import com.zoctan.smarthub.zxing.activity.CaptureActivity;

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

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_list;
    }

    @Override
    protected void initView(View view, final Bundle savedInstanceState) {
        // 固定RecyclerView大小
        mRecyclerView.setHasFixedSize(true);
        // 设置布局管理器
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置item动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new HubListAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);

        final HubListFragment self = this;
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HubListFragmentPermissionsDispatcher.openScannerWithPermissionCheck(self);
            }
        });

        setSmartRefreshListener();
        refreshHubList();
    }

    private final HubListAdapter.OnItemClickListener mOnItemClickListener = new HubListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(String action, View view, int position) {
            if (mData.size() <= 0) {
                return;
            }
            HubBean hub = mAdapter.getItem(position);
            switch (action) {
                case "detail":
                    App.mSPUtil.put("hub_name", hub.getName());
                    App.mSPUtil.put("hub_onenet_id", hub.getOnenet_id());
                    App.mSPUtil.put("hub_online", hub.getOnline());
                    Intent intent = new Intent("hub_detail");
                    intent.addCategory("hub");
                    startActivity(intent);
                    getHoldingActivity().overridePendingTransition(android.R.anim.slide_in_left, 0);
                    break;
                case "on":
                case "off":
                    mHubListPresenter.hubOpenClose(
                            App.mSPUtil.getString("hubOneNetId"), action);
                    //ToastUtils.showShort(action);
                    break;
                case "update":
                    showUpdateDialog(action, hub);
                    //ToastUtils.showShort(action);
                    break;
                case "delete":
                    showDeleteDialog(hub);
                    //ToastUtils.showShort(action);
                    break;
            }
        }
    };

    private void showDeleteDialog(final HubBean hub) {
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_delete,
                        R.string.hub_delete_msg,
                        R.drawable.ic_alert,
                        R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHubListPresenter.doHub("delete",
                                App.mSPUtil.getString("user_password"),
                                hub);
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showUpdateDialog(final String action, final HubBean hub) {
        @SuppressLint("InflateParams") final View view = this.getLayoutInflater().inflate(R.layout.dialog_edit_hub, null);
        final TextInputEditText mEtHubName = view.findViewById(R.id.EditText_hub_name);
        final TextInputLayout mLayoutHubName = view.findViewById(R.id.TextInputLayout_hub_name);
        mEtHubName.setText(hub.getName());
        mEtHubName.setSelection(mEtHubName.getText().length());
        mEtHubName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 12) {
                    mLayoutHubName.setErrorEnabled(true);
                    mLayoutHubName.setError(getString(R.string.all_max_name));
                } else {
                    mLayoutHubName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.all_edit,
                        null,
                        R.drawable.ic_edit,
                        R.string.all_modify);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEtHubName.getText().length() > 0
                                && mLayoutHubName.getError() == null) {
                            hub.setName(mEtHubName.getText().toString());
                            mHubListPresenter.doHub(action,
                                    App.mSPUtil.getString("user_password"),
                                    hub);
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    public void refreshHubList() {
        if (mData != null) {
            mData.clear();
        }
        mHubListPresenter.loadHubList(
                App.mSPUtil.getString("user_password"));
    }

    private void setSmartRefreshListener() {
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshHubList();
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HubListFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void openScanner() {
        Intent intent = new Intent(getContext(), CaptureActivity.class);
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
                    public void onClick(View v) {
                        dialog.dismiss();
                        request.proceed();//继续执行请求
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String scanResult = bundle.getString("qr_scan_result");
                // 扫描出的信息
                if (scanResult != null) {
                    ToastUtils.showShort(scanResult);
                    /*
                    String[] result = scanResult.split(" ");
                    HubBean hub = new HubBean();
                    hub.setOnenet_id(result[0]);
                    hub.setMac(result[1]);
                    showAddDialog(hub);
                    */
                }
            }
        }
    }

    private void showAddDialog(final HubBean hub) {
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_add,
                        R.string.hub_add_msg,
                        R.drawable.ic_hub_list,
                        R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHubListPresenter.doHub("add",
                                App.mSPUtil.getString("user_password"),
                                hub);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void loadHubList(List<HubBean> hubList) {
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
    public void showSuccessMsg(String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
        refreshHubList();
    }

    @Override
    public void showFailedMsg(String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }
}

class HubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HubBean> mData;
    private OnItemClickListener mOnItemClickListener;

    public void setData(List<HubBean> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hub, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            HubBean hub = mData.get(position);
            if (hub == null) {
                return;
            }
            ((ItemViewHolder) holder).mTvHubName.setText((hub.getName()));
            ((ItemViewHolder) holder).mTvHubMac.setText((hub.getMac()));
            String text;
            Boolean checked;
            if (hub.getOnline()) {
                text = "在线";
                checked = true;
                Icon.on(((ItemViewHolder) holder).mIvHub).color(R.color.accent).icon(R.drawable.ic_hub).put();
            } else {
                text = "离线";
                checked = false;
            }
            ((ItemViewHolder) holder).mTvHubOnline.setText(text);
            ((ItemViewHolder) holder).mSwitchOpenClose.setChecked(checked);
        }
    }

    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    HubBean getItem(int position) {
        return mData.get(position);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
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
        @BindView(R.id.TextView_hub_online)
        TextView mTvHubOnline;
        @BindView(R.id.Switch_hub_open_close)
        Switch mSwitchOpenClose;
        @BindView(R.id.Button_hub_edit)
        Button mBtnHubEdit;
        @BindView(R.id.Button_hub_delete)
        Button mBtnHubDelete;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.Switch_hub_open_close, R.id.GridLayout_hub, R.id.Button_hub_edit, R.id.Button_hub_delete})
        public void onClick(View view) {
            if (mOnItemClickListener == null) {
                return;
            }
            String action = null;
            switch (view.getId()) {
                case R.id.Switch_hub_open_close:
                    action = mTvHubOnline.getText().equals("离线") ? "on" : "off";
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
            mOnItemClickListener.onItemClick(action, view, this.getLayoutPosition());
        }
    }
}