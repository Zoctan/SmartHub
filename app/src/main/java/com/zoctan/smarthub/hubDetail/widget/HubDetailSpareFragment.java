package com.zoctan.smarthub.hubDetail.widget;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.wang.avi.AVLoadingIndicatorView;
import com.zoctan.smarthub.App;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.hubDetail.presenter.HubDetailSparePresenter;
import com.zoctan.smarthub.hubDetail.view.HubDetailSpareView;
import com.zoctan.smarthub.utils.AlerterUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;

import static com.blankj.utilcode.util.TimeUtils.getNowString;

public class HubDetailSpareFragment extends BaseFragment implements HubDetailSpareView {
    @BindView(R.id.TextView_hub_detail_spare_electrical_degree)
    TextView mTvDegree;
    @BindView(R.id.TextView_hub_detail_spare_electrical_bill)
    TextView mTvBill;
    @BindView(R.id.LineChart_hub_detail_spare)
    LineChart mLineChart;
    @BindView(R.id.GridLayout_hub_detail_spare)
    GridLayout mGridLayout;
    @BindView(R.id.ProgressBar_hub_detail_spare)
    AVLoadingIndicatorView mProgressBar;
    private final HubDetailSparePresenter mHubDetailSparePresenter = new HubDetailSparePresenter(this);

    public static HubDetailSpareFragment newInstance() {
        return new HubDetailSpareFragment();
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_detail_spare;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        initLineChart();
        loadLineChartData();
    }

    private void loadLineChartData() {
        Map<String, String> params = new HashMap<>();
        String[] keys = new String[]{
                "start",
                "end",
                "duration",
                "limit"};
        // "2017-12-01T08:00:35"
        String[] values = new String[]{
                getNowString(new SimpleDateFormat(
                        "yyyy-MM-dd", Locale.getDefault())) + "T00:00",
                getNowString(new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm", Locale.getDefault())),
                "3600",
                "24"};
        for (int i = 0; i < 4; i++) {
            params.put(keys[i], values[i]);
        }
        mHubDetailSparePresenter.loadHubSpareList(
                App.mSPUtil.getString("hub_onenet_id"),
                "W",
                params);
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
    public void setLineChartData(String[] x, ArrayList<Entry> y) {
        // LineDataSet每一个对象就是一条连接线
        LineDataSet lineDataSet;
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
            lineDataSet = new LineDataSet(y, "电度");
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
                @SuppressWarnings("ConstantConditions") Drawable drawable = ContextCompat.getDrawable(getContext(), R.color.danger);
                lineDataSet.setFillDrawable(drawable);//设置范围背景填充
                lineDataSet.setFillColor(Color.YELLOW);
            } else {
                lineDataSet.setFillColor(Color.BLACK);
            }

            // 保存LineDataSet集合
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            // 添加数据源
            dataSets.add(lineDataSet);
            // 创建LineData对象 属于LineChart折线图的数据集合
            LineData data = new LineData(dataSets);
            // 添加到图表中
            mLineChart.setData(data);
            // 绘制图表
            mLineChart.invalidate();
            Matrix m = new Matrix();
            m.postScale(1.5f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
            mLineChart.getViewPortHandler().refresh(m, mLineChart, false);//将图表动画显示之前进行缩放
            mLineChart.animateX(1000); // 立即执行的动画,x轴
            // 图例
            Legend legend = mLineChart.getLegend();
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
        mProgressBar.smoothToShow();
    }

    @Override
    public void hideLoading() {
        mProgressBar.smoothToHide();
    }

    @Override
    public void showFailedMsg(String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }

    private static class MyValueFormatter implements IAxisValueFormatter {
        private final String[] x;

        MyValueFormatter(String[] x) {
            this.x = x;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return this.x[(int) value % this.x.length];
        }
    }
}
