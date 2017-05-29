package creeper_san.myshoes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import creeper_san.myshoes.base.BaseFragment;
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

    private OnRoundProgressClickListener listener;


    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main_step;
    }

    @Override
    protected void initSelf() {

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
