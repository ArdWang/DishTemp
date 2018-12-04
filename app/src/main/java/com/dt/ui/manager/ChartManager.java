package com.dt.ui.manager;

import android.content.Context;
import android.graphics.Color;

import com.dt.R;
import com.dt.utils.StringAxisValueFormatter;
import com.dt.widgets.MarkerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartManager {
    private LineChart lineChart;
    private YAxis leftAxis;   //左边Y轴
    private YAxis rightAxis;  //右边Y轴
    private XAxis xAxis;      //X轴
    private Context context;
    private String symbol;

    public ChartManager(Context context, LineChart mLineChart,String symbol) {
        this.lineChart = mLineChart;
        this.context = context;
        this.symbol = symbol;
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        initLineChart();
    }

    /**
     * 初始化LineChart
     */
    private void initLineChart() {
        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new MarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv); // Set the marker to the chart
        lineChart.setDrawGridBackground(false);
        //显示边界
        lineChart.setDrawBorders(false);
        //设置动画效果
        lineChart.animateY(1000, Easing.EasingOption.Linear);
        lineChart.animateX(1000, Easing.EasingOption.Linear);
        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        //XY轴的设置
        //X轴设置显示位置在底部
        lineChart.getAxisRight().setEnabled(false); // X轴右边隐藏掉
        xAxis.setGranularityEnabled(true); //不重复显示x轴坐标
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(2f);
        //设置坐标轴的颜色
        xAxis.setAxisLineColor(context.getResources().getColor(R.color.colorYaHei));
        //设置网格线的宽度
        xAxis.setGridLineWidth(1.0f);
        xAxis.enableGridDashedLine(5f,5f,0f);
        xAxis.setGridColor(context.getResources().getColor(R.color.colorYaHei));
        //是否绘制网格线
        xAxis.setDrawGridLines(true);
        //是否绘制轴线
        xAxis.setDrawAxisLine(true);
        //设置x轴线的宽度
        xAxis.setAxisLineWidth(2.0f);
        //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false);

        leftAxis.setGridLineWidth(1.0f);
        leftAxis.setGridColor(context.getResources().getColor(R.color.colorYaHei));
        leftAxis.enableGridDashedLine(5f, 5f, 0f);  //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期
        leftAxis.setGranularity(20f); // 网格线条间距
        leftAxis.setDrawGridLines(true);      //是否使用 Y轴网格线条
        //y轴线的宽度
        leftAxis.setAxisLineColor(context.getResources().getColor(R.color.colorYaHei));
        leftAxis.setAxisLineWidth(2.0f);
        //保证Y轴从0开始，不然会上移一点
        //rightAxis.setAxisMinimum(-65.0f);
        rightAxis.setEnabled(false);
        setChartChange(symbol);
    }

    /**
     * 图表的改变
     */
    public void setChartChange(String symbol){
        //当为摄氏度的时候
        if(symbol.equals("°C")) {
            leftAxis.setAxisMaximum(130.0f);
            leftAxis.setAxisMinimum(-65.0f);
        }
        //当为华氏度的时候
        else{
            leftAxis.setAxisMaximum(230.0f);
            leftAxis.setAxisMinimum(-65.0f);
        }
    }

    public void showLineChart(List<String> xValues, List<Float> yValues, int color, String display){
        ArrayList<Entry> entries = new ArrayList<>();
        xAxis.setValueFormatter(new StringAxisValueFormatter(xValues));
        for (int i = 0; i < xValues.size(); i++) {
            entries.add(new Entry( i, yValues.get(i)));
        }
        LineDataSet lineDataSet;

        if(lineChart.getData()!=null&&lineChart.getData().getDataSetCount() > 0){
            lineDataSet = (LineDataSet)lineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(entries);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();

        }else {
            // 每一个LineDataSet代表一条线
            lineDataSet = new LineDataSet(entries, display);
            initLineDataSet(lineDataSet, color, false);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet);
            LineData data = new LineData(dataSets);
            //设置X轴的刻度数
            xAxis.setLabelCount(6, true);
            lineChart.setData(data);
        }
    }

    /**
     * 初始化曲线 每一个LineDataSet代表一条线
     * @param lineDataSet
     * @param color
     * @param mode        折线图是否填充
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, boolean mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1.5f);
        //lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setValueTextSize(0.0f);
        //lineDataSet.setDrawCircleHole(true);
        //lineDataSet.setValueTextSize(9f);
        //设置折线图填充
        lineDataSet.setDrawFilled(mode);//填充曲线下方的区域
        lineDataSet.setFormLineWidth(1.5f);
        lineDataSet.setFormSize(15.f);

        //线模式为圆滑曲线（默认折线）
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }



}
