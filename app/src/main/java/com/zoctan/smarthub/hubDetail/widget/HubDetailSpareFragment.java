package com.zoctan.smarthub.hubDetail.widget;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hubDetail.presenter.HubDetailSparePresenter;
import com.zoctan.smarthub.hubDetail.view.HubDetailSpareView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zyao89.view.zloading.ZLoadingView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;

public class HubDetailSpareFragment extends BaseFragment implements HubDetailSpareView {
    @BindView(R.id.TextView_hub_detail_spare_electrical_degree)
    TextView mTvDegree;
    @BindView(R.id.TextView_hub_detail_spare_electrical_bill_tip)
    TextView mTvBillTip;
    @BindView(R.id.TextView_hub_detail_spare_electrical_bill)
    TextView mTvBill;
    @BindView(R.id.LineChart_hub_detail_spare)
    LineChart mLineChart;
    @BindView(R.id.GridLayout_hub_detail_spare)
    GridLayout mGridLayout;
    @BindView(R.id.ZLoadingView_hub_detail_spare)
    ZLoadingView zLoadingView;
    private final Handler handler = new Handler();
    private final HubDetailSparePresenter mPresenter = new HubDetailSparePresenter(this);
    protected HubBean hubBean = new HubBean();

    public static HubDetailSpareFragment newInstance() {
        return new HubDetailSpareFragment();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hubBean.setName(getArguments().getString("hub_name"));
            hubBean.setOnenet_id(getArguments().getString("hub_onenet_id"));
            hubBean.setIs_electric(getArguments().getBoolean("hub_is_electric"));
            hubBean.setConnected(getArguments().getBoolean("hub_connected"));
        }
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_detail_spare;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        initLineChart();
        // 插座在线即查询实时数据
        if (hubBean.getConnected()) {
            handler.postDelayed(runnable, 1000);
        } else {
            setLineChart();
        }
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setLineChart();
            // 间隔5分钟
            handler.postDelayed(this, 5 * 60 * 1000);
        }
    };

    private void setLineChart() {
        mPresenter.loadHubSpareList(hubBean.getOnenet_id(), userToken);
    }

    private void initLineChart() {
        // 创建描述信息
        //Description description = new Description();
        //description.setText("V-A图表");
        //description.setTextColor(Color.RED);
        //description.setTextSize(20);
        // 设置图表描述信息
        //mLineChart.setDescription(description);
        // 不设置描述
        mLineChart.getDescription().setEnabled(false);
        // 没有数据时显示的文字
        mLineChart.setNoDataText("暂无数据");
        // 没有数据时显示文字的颜色
        mLineChart.setNoDataTextColor(R.color.primary_text);
        // 绘图区后面的背景矩形将绘制
        mLineChart.setDrawGridBackground(false);
        // 是否在折线图上添加边框
        mLineChart.setDrawBorders(false);
        // X轴设在下方
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        // 隐藏Y轴右边轴线，此时标签数字也隐藏
        mLineChart.getAxisRight().setEnabled(false);
        // 设置是否可以触摸
        mLineChart.setTouchEnabled(true);
        // 是否可以拖拽
        mLineChart.setDragEnabled(true);
        // 是否可以缩放x和y轴, 默认true
        mLineChart.setScaleEnabled(false);
        // 是否可以缩放 仅x轴
        mLineChart.setScaleXEnabled(false);
        // 是否可以缩放 仅y轴
        mLineChart.setScaleYEnabled(false);
        // 设置x轴和y轴能否同时缩放。默认false
        mLineChart.setPinchZoom(false);
        // 设置是否可以通过双击屏幕放大图表。默认true
        mLineChart.setDoubleTapToZoomEnabled(false);
        // 能否拖拽高亮线(数据点与坐标的提示线)，默认true
        mLineChart.setHighlightPerDragEnabled(false);
        // 拖拽滚动时，手放开是否会持续滚动，默认true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        mLineChart.setDragDecelerationEnabled(true);
        // 与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止
        mLineChart.setDragDecelerationFrictionCoef(0.99f);
        // 设置 chart 边框线的颜色。
        //mLineChart.setBorderColor();
        // 设置 chart 边界线的宽度，单位 dp
        //mLineChart.setBorderWidth();
        // 打印日志
        //mLineChart.setLogEnabled(true);
    }

    @Override
    public void setSpareData(final String electricalDegree, final String electricalBill, final int currentMonth) {
        mTvDegree.setText(electricalDegree);
        final String billTip = String.format(Locale.CHINA, "%d月份%s", currentMonth, getString(R.string.hub_detail_spare_electrical_bill));
        mTvBillTip.setText(billTip);
        mTvBill.setText(electricalBill);
    }

    @Override
    public void setLineChartData(final String[] x, final ArrayList<Entry> y) {
        // LineDataSet每一个对象就是一条连接线
        final LineDataSet lineDataSet;
        // 判断图表中原来是否有数据
        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() > 0) {
            // 获取数据
            lineDataSet = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(y);
            // 刷新数据
            mLineChart.getXAxis().setValueFormatter(new MyValueFormatter(x));
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            mLineChart.getXAxis().setValueFormatter(new MyValueFormatter(x));
            // y轴数据集  参数1：数据源 参数2：数据曲线名称
            lineDataSet = new LineDataSet(y, "瓦数");
            lineDataSet.setColor(R.color.primary);
            lineDataSet.setCircleColor(R.color.accent);
            // 改变折线样式，用曲线
            // mLineDataSet.setDrawCubic(true);
            // 默认是直线
            // 曲线的平滑度，值越大越平滑
            // mLineDataSet.setCubicIntensity(0.2f);
            // 设置线宽
            lineDataSet.setLineWidth(1f);
            // 设置焦点圆心的大小
            lineDataSet.setCircleRadius(3f);
            // 是否禁用点击高亮线
            lineDataSet.setHighlightEnabled(false);
            // 点击后的高亮线的显示样式
            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
            // 设置点击交点后显示高亮线宽
            lineDataSet.setHighlightLineWidth(2f);
            // 设置点击交点后显示交高亮线的颜色
            lineDataSet.setHighLightColor(Color.RED);
            // 设置显示值的文字大小
            lineDataSet.setValueTextSize(9f);
            // 设置禁用范围背景填充
            lineDataSet.setDrawFilled(false);

            if (Utils.getSDKInt() >= 18) {
                // 填充背景只支持SDK18以上
                @SuppressWarnings("ConstantConditions") final Drawable drawable = ContextCompat.getDrawable(getContext(), R.color.danger);
                lineDataSet.setFillDrawable(drawable);//设置范围背景填充
                lineDataSet.setFillColor(Color.YELLOW);
            } else {
                lineDataSet.setFillColor(Color.BLACK);
            }

            // 保存LineDataSet集合
            final ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            // 添加数据源
            dataSets.add(lineDataSet);
            // 创建LineData对象 属于LineChart折线图的数据集合
            final LineData data = new LineData(dataSets);
            // 添加到图表中
            mLineChart.setData(data);
            // 绘制图表
            mLineChart.invalidate();
            final Matrix m = new Matrix();
            m.postScale(1.5f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
            mLineChart.getViewPortHandler().refresh(m, mLineChart, false);//将图表动画显示之前进行缩放
            mLineChart.animateX(1000); // 立即执行的动画,x轴
            // 图例
            final Legend legend = mLineChart.getLegend();
            // 设置文字大小
            legend.setTextSize(10f);
            // 正方形，圆形或线
            legend.setForm(Legend.LegendForm.CIRCLE);
            // 设置Form的大小
            legend.setFormSize(10f);
            // 是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
            legend.setWordWrapEnabled(true);
            // 设置Form的宽度
            legend.setFormLineWidth(10f);
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        // 停止刷新
        handler.removeCallbacks(runnable);
    }

    @Override
    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }

    private static class MyValueFormatter implements IAxisValueFormatter {
        private final String[] x;

        MyValueFormatter(final String[] x) {
            this.x = x;
        }

        @Override
        public String getFormattedValue(final float value, final AxisBase axis) {
            return this.x[(int) value % this.x.length];
        }
    }
}
