package creeper_san.myshoes;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;

import butterknife.BindView;
import creeper_san.myshoes.base.BaseActivity;
import creeper_san.myshoes.flag.IntentKey;

public class LineChartActivity extends BaseActivity implements ServiceConnection{
    @BindView(R.id.stepCountChart)LineChart lineChart;

    private ShoesService shoesService;
    private LineData lineData;
    private String type;


    @Override
    protected int getLayoutID() {
        return R.layout.activity_step_count;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toStartBindServer(ShoesService.class,this);
        initIntent();
        initActionbar();
        initChart();
    }

    private void initIntent() {
        type = getIntent().getStringExtra(IntentKey.KEY_LINE_CHART_TYPE);
    }

    private void initActionbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (type.equals(IntentKey.VALUE_LINE_CHART_TYPE_STEP)){
            setTitle("步数记录");
        }else {
            setTitle("体重记录");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.step_count_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menuStepCount7:
                if (shoesService!=null){
                    if (type.equals(IntentKey.VALUE_LINE_CHART_TYPE_STEP)){
                        lineChart.setData(get7DayStepLineData());
                    }else {
                        lineChart.setData(get7DayWeightLineData());
                    }
                }
                break;
            case R.id.menuStepCount30:
                if (shoesService!=null){
                    if (type.equals(IntentKey.VALUE_LINE_CHART_TYPE_STEP)){
                        lineChart.setData(get30DayStepLineData());
                    }else {
                        lineChart.setData(get30DayWeightLineData());
                    }
                }
                break;
            case R.id.menuStepCount90:
                if (shoesService!=null){
                    if (type.equals(IntentKey.VALUE_LINE_CHART_TYPE_STEP)){
                        lineChart.setData(get90DayStepLineData());
                    }else {
                        lineChart.setData(get90DayWeightLineData());
                    }
                }
                break;
        }
        lineChart.invalidate();
        return super.onOptionsItemSelected(item);
    }

    private void initChart() {
        //去除描述文本
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        //去除坐标以及网格
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawGridLines(true);//去除网格线(横线)(需要当下面这个是关闭的情况下才起作用)
        YAxis lineRightYAxis = lineChart.getAxisRight();
        lineRightYAxis.setEnabled(false);   //不显示右侧坐标轴
    }



    public LineData get7DayStepLineData(){
        return shoesService.getDaysStepData(7);
    }
    public LineData get30DayStepLineData(){
        return shoesService.getDaysStepData(30);
    }
    public LineData get90DayStepLineData(){
        return shoesService.getDaysStepData(90);
    }


    public LineData get7DayWeightLineData(){
        return shoesService.getDaysWeightData(7);
    }
    public LineData get30DayWeightLineData(){
        return shoesService.getDaysWeightData(30);
    }
    public LineData get90DayWeightLineData(){
        return shoesService.getDaysWeightData(90);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        shoesService = ((ShoesService.ShoesServerBinder)binder).getService();
        if (type.equals(IntentKey.VALUE_LINE_CHART_TYPE_STEP)){
            lineChart.setData(get7DayStepLineData());
        }else {
            lineChart.setData(get7DayWeightLineData());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
