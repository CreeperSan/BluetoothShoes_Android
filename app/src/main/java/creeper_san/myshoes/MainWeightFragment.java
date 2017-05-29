package creeper_san.myshoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anderson.dashboardview.view.DashboardView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import creeper_san.myshoes.base.BaseFragment;
import creeper_san.myshoes.event.WeightEvent;

public class MainWeightFragment extends BaseFragment {
    @BindView(R.id.mainTemperatureMeter)
    DashboardView mainTemperatureMeter;
    @BindView(R.id.mainWeightRecordBtn)
    Button recordBtn;

    private OnRecordListener onRecordListener;


    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main_weight;
    }


    public void setWeight(int weight) {
        mainTemperatureMeter.setPercent(weight);
    }

    @OnClick(R.id.mainWeightRecordBtn)
    public void onClick() {
        if (onRecordListener!=null){
            onRecordListener.onRecord();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWeightEvent(WeightEvent event){
        mainTemperatureMeter.setPercent((int) event.getWeight());
    }

    public OnRecordListener getOnRecordListener() {
        return onRecordListener;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    interface OnRecordListener {
        public void onRecord();
    }
}
