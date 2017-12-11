package com.zoctan.smarthub.hub.widget;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.zoctan.smarthub.R;

import java.util.ArrayList;

public class HubDetailSpareFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    public static HubDetailSpareFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HubDetailSpareFragment pageFragment = new HubDetailSpareFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_hub_detail_spare, container, false);
        LineChart mLineChart = view.findViewById(R.id.mLineChart);
        //创建描述信息
        Description description = new Description();
        description.setText("V-A图表");
        description.setTextColor(Color.RED);
        description.setTextSize(20);
        mLineChart.setDescription(description);//设置图表描述信息
        mLineChart.setNoDataText("没有数据");//没有数据时显示的文字
        mLineChart.setNoDataTextColor(Color.BLUE);//没有数据时显示文字的颜色
        mLineChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
        mLineChart.setDrawBorders(false);//禁止绘制图表边框的线
        //mLineChart.setBorderColor(); //设置 chart 边框线的颜色。
        //mLineChart.setBorderWidth(); //设置 chart 边界线的宽度，单位 dp。
        //mLineChart.setLogEnabled(true);//打印日志
        //mLineChart.notifyDataSetChanged();//刷新数据
        //mLineChart.invalidate();//重绘
        /**
         * Entry 坐标点对象  构造函数 第一个参数为x点坐标 第二个为y点
         */
        ArrayList<Entry> values1 = new ArrayList<>();

        values1.add(new Entry(4, 10));
        values1.add(new Entry(6, 15));
        values1.add(new Entry(9, 20));
        values1.add(new Entry(12, 5));
        values1.add(new Entry(15, 30));

        //LineDataSet每一个对象就是一条连接线
        LineDataSet set1;

        //判断图表中原来是否有数据
        if (mLineChart.getData() != null &&
                mLineChart.getData().getDataSetCount() > 0) {
            //获取数据1
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(values1);
            //刷新数据
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            //设置数据1  参数1：数据源 参数2：图例名称
            set1 = new LineDataSet(values1, "test");
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);//设置线宽
            set1.setCircleRadius(3f);//设置焦点圆心的大小
            set1.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
            set1.setHighlightLineWidth(2f);//设置点击交点后显示高亮线宽
            set1.setHighlightEnabled(true);//是否禁用点击高亮线
            set1.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
            set1.setValueTextSize(9f);//设置显示值的文字大小
            set1.setDrawFilled(false);//设置禁用范围背景填充

            //格式化显示数据
            final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return mFormat.format(value);
                }
            });
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.color.danger);
                set1.setFillDrawable(drawable);//设置范围背景填充
            } else {
                set1.setFillColor(Color.BLACK);
            }

            //保存LineDataSet集合
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets
            //创建LineData对象 属于LineChart折线图的数据集合
            LineData data = new LineData(dataSets);
            // 添加到图表中
            mLineChart.setData(data);
            //绘制图表
            mLineChart.invalidate();
            mLineChart.setTouchEnabled(true); // 设置是否可以触摸
            mLineChart.setDragEnabled(true);// 是否可以拖拽
            mLineChart.setScaleEnabled(false);// 是否可以缩放 x和y轴, 默认是true
            mLineChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
            mLineChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
            mLineChart.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
            mLineChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
            mLineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
            mLineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
            mLineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。
            Legend l = mLineChart.getLegend();//图例
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);//设置图例的位置
            l.setTextSize(10f);//设置文字大小
            l.setForm(Legend.LegendForm.CIRCLE);//正方形，圆形或线
            l.setFormSize(10f); // 设置Form的大小
            l.setWordWrapEnabled(true);//是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
            l.setFormLineWidth(10f);//设置Form的宽度
        }
        return view;
    }
}
