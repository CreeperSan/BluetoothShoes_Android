package creeper_san.myshoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.anderson.dashboardview.view.DashboardView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import creeper_san.myshoes.base.BaseFragment;
import creeper_san.myshoes.event.HumidityEvent;
import creeper_san.myshoes.event.TemperatureEvent;
import creeper_san.myshoes.view.Thermometer;

public class MainTemperatureFragment extends BaseFragment {
    @BindView(R.id.mainTemperatureMeter)
    DashboardView mainTemperatureMeter;
    @BindView(R.id.mainTemperaturePowerSwitch)
    Switch mainTemperaturePowerSwitch;
    @BindView(R.id.mainTemperatureHintText)
    TextView hintText;
    @BindView(R.id.mainTemperatureSeekbar)
    SeekBar seekBar;
    @BindView(R.id.mainTemperatureThermometer)
    Thermometer thermometer;
    @BindView(R.id.mainTemperatureSuggestionText)
    TextView suggestionText;
    @BindView(R.id.mainTemperatureHumidityText)
    TextView humidityText;
    @BindView(R.id.mainTemperatureTempText)
    TextView tempText;


    private OnSeekResultListener listener;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main_temperature;
    }

    @Override
    protected void onViewInflate() {
        mainTemperaturePowerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener!=null){
                    listener.onSwitch(isChecked);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hintText.setText((progress+15)+"℃");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener!=null){
                    listener.onResult(seekBar);
                }
            }
        });
    }

    public OnSeekResultListener getListener() {
        return listener;
    }
    public void setSwitchState(boolean isOn){
        if (isOn){
            mainTemperaturePowerSwitch.setChecked(true);
        }else {
            mainTemperaturePowerSwitch.setChecked(false);
        }
    }

    public void setTemperature(float value){
        thermometer.setTemperature(value);
        tempText.setText(((int)value)+"℃");
    }
    public void setHumidity(int humidity){
        humidityText.setText(humidity+"%");
    }
    public void setSuggestion(String content){
        suggestionText.setText(content);
    }

    public void setListener(OnSeekResultListener listener) {
        this.listener = listener;
    }

    public void setTemperature(int temperature){
        mainTemperatureMeter.setPercent(temperature*2);
    }

    public void setSwitch(boolean state){
        mainTemperaturePowerSwitch.setChecked(state);
    }

    public void setSeekBarProgress(int progress){
        seekBar.setProgress(progress);
        hintText.setText((progress+15)+"℃");
    }

    /**
     *      EventBus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTemperatureEvent(TemperatureEvent event){
        thermometer.setTemperature(event.getTemperature());
        tempText.setText(event.getTemperature()+"℃");
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHumidityEvent(HumidityEvent event){
        humidityText.setText(event.getHumidity()+"%");
    }

    /**
     *      一些接口
     */
    interface OnSeekResultListener{
        public void onResult(SeekBar seekBar);
        public void onSwitch(boolean isChecked);
    }
}
