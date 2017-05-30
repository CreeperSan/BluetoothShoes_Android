package creeper_san.myshoes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import creeper_san.myshoes.Exception.JsonDecodeException;
import creeper_san.myshoes.base.BaseFragment;
import creeper_san.myshoes.event.NetworkEvent;
import creeper_san.myshoes.event.WeatherEvent;
import creeper_san.myshoes.helper.NetworkHelper;
import creeper_san.myshoes.helper.UrlHelper;
import creeper_san.myshoes.json.WeatherJson;
import creeper_san.myshoes.view.HoloCircularProgress;

public class MainStepFragment extends BaseFragment {
    @BindView(R.id.stepFragmentMainProgress)
    HoloCircularProgress roundProgressView;
    @BindView(R.id.stepFragmentStepNumber)
    TextView stepNumberView;
    @BindView(R.id.stepFragmentEnergyHint)
    TextView energyHintView;
    @BindView(R.id.stepFragmentDistanceHint)
    TextView distanceHintView;
    @BindView(R.id.stepFragmentWeather)
    TextView txtWeather;
    @BindView(R.id.stepFragmentAQI)
    TextView txtAQI;
    @BindView(R.id.stepFragmentPM25)
    TextView txtPM25;
    @BindView(R.id.stepFragmentTrav)
    TextView txtTrav;
    @BindView(R.id.stepFragmentTravT)
    TextView txtTravT;
    @BindView(R.id.stepFragmentSport)
    TextView txtSport;
    @BindView(R.id.stepFragmentSportT)
    TextView txtSportT;
    @BindView(R.id.stepFragmentUv)
    TextView txtUV;
    @BindView(R.id.stepFragmentUvT)
    TextView txtUvT;
    @BindView(R.id.stepFragmentDrsg)
    TextView txtDrsg;
    @BindView(R.id.stepFragmentDrsgT)
    TextView txtDrsgT;
    @BindView(R.id.stepFragmentGrid)
    GridLayout gridLayout;
    @BindView(R.id.stepFragmentLoading)
    LinearLayout loadingLayout;
    @BindView(R.id.stepFragmentLoadingProgress)
    ProgressBar loadingProgress;
    @BindView(R.id.stepFragmentLoadingTextView)
    TextView loadingText;
    @BindView(R.id.stepFragmentAQIT)
    TextView aQITitleText;

    private OnRoundProgressClickListener listener;


    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main_step;
    }

    @Override
    protected void initSelf() {
        gridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNetworkEvent();
            }
        });
        aQITitleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("空气污染指数");
                builder.setMessage("0-50   " +
                        "1级 优\n参加户外活动呼吸清新空气\n\n" +
                        "50-100   " +
                        "2级 良\n可以正常进行室外活动\n\n" +
                        "101-150   " +
                        "3级 轻度\n敏感人群减少体力消耗大的户外活动\n\n" +
                        "151-200   " +
                        "4级 中度\n对敏感人群影响较大\n\n" +
                        "201-300   " +
                        "5级 重度\n所有人应适当减少室外活动\n\n" +
                        ">300   " +
                        "6级 严重\n尽量不要留在室外");
                builder.setPositiveButton("我知道了", null);
                builder.show();
            }
        });
        showLoading();
        postNetworkEvent();
        log(UrlHelper.generateWeatherUrl("shenzhen"));
    }
    private void postNetworkEvent(){
        postStickyEvent(new NetworkEvent(UrlHelper.generateWeatherUrl("shenzhen"), new NetworkHelper.NetworkHandler() {
            @Override
            public void onResponse(boolean isSuccess, @NotNull String response) {
                if (isSuccess){
                    try {
                        WeatherJson json=new WeatherJson(response);
                        txtWeather.setText(json.getTxt(0));
                        txtAQI.setText(json.getAqi(0));
                        txtPM25.setText(json.getPm25(0));
                        txtTrav.setText(json.getTxtTrav(0));
                        txtSport.setText(json.getTxtSport(0));
                        txtDrsg.setText(json.getTxtDrsg(0));
                        txtUV.setText(json.getTxtUv(0));
                        txtTravT.setText("出行 "+json.getBrfTrav(0));
                        txtDrsgT.setText("穿衣 "+json.getBrfDrsg(0));
                        txtSportT.setText("运动 "+json.getBrfTrav(0));
                        txtUvT.setText("紫外线 "+json.getBrfUv(0));
                    } catch (JSONException | JsonDecodeException e) {
                        e.printStackTrace();
                    }
                    hideLoading();
                }else {
                    showLoadingFail();
                }
            }
        }));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        roundProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onClick();
                }
            }
        });
    }

    /**
     *      供Activity调用的方法
     */

    private void showLoading(){
        loadingProgress.setVisibility(View.VISIBLE);
        gridLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        loadingText.setText("正在努力加载数据");
        loadingText.setClickable(false);
    }
    private void hideLoading(){
        gridLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }
    private void showLoadingFail(){
        gridLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.GONE);
        loadingText.setClickable(true);
        loadingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNetworkEvent();
            }
        });
        loadingText.setText("加载失败了，点击重试");
    }

    public void setTodaySteps(int step){
        stepNumberView.setText(String.valueOf(step));
    }
    public void setTodayProgress(float progress){
        roundProgressView.setProgress(progress);
    }
    public void setEnergyHintText(int calorie){
        energyHintView.setText("消耗了"+calorie+"卡路里热量");
    }
    public void setDistanceHintText(double distance){
        DecimalFormat format = new DecimalFormat("0.00");
        String distanceStr = format.format(distance);
        distanceHintView.setText("行走了"+distanceStr+"KM路程");
    }


    public OnRoundProgressClickListener getListener() {
        return listener;
    }

    public void setListener(OnRoundProgressClickListener listener) {
        this.listener = listener;
    }

    interface OnRoundProgressClickListener{
        public void onClick();
    }

}
