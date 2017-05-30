package creeper_san.myshoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import creeper_san.myshoes.base.BaseFragment;
import creeper_san.myshoes.event.HumidityEvent;
import creeper_san.myshoes.event.TempConfigEvent;
import creeper_san.myshoes.event.TempConfigResultEvent;
import creeper_san.myshoes.event.TemperatureEvent;
import creeper_san.myshoes.event.WarmEvent;
import creeper_san.myshoes.view.Thermometer;

public class MainTemperatureFragment extends BaseFragment {
    @BindView(R.id.mainTemperaturePowerSwitch)
    Switch mainTemperaturePowerSwitch;
    @BindView(R.id.mainTemperatureHintText)
    TextView hintText;
    @BindView(R.id.mainTemperatureSeekbar)
    SeekBar seekBar;
    @BindView(R.id.mainTemperatureThermometer)
    Thermometer thermometer;
    @BindView(R.id.mainTemperatureHumidityText)
    TextView humidityText;
    @BindView(R.id.mainTemperatureTempText)
    TextView tempText;
    @BindView(R.id.mainTemperatureTempLayout)
    LinearLayout linearLayout;


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
                setSwitch(isChecked);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hintText.setText((progress+15)+"℃");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                postEvent(new WarmEvent(mainTemperaturePowerSwitch.isChecked(),seekBar.getProgress()));
            }
        });

        postStickyEvent(new TempConfigEvent());
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


    public void setSwitch(boolean state){
        mainTemperaturePowerSwitch.setChecked(state);
        if (state){
            postEvent(new WarmEvent(true,-100));
            linearLayout.setVisibility(View.VISIBLE);
        }else {
            postEvent(new WarmEvent(false,-100));
            linearLayout.setVisibility(View.GONE);
        }
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
    @Subscribe()
    public void onTempConfigResultEvent(TempConfigResultEvent event){
        log("收到回调 "+event.isState()+" "+event.getTemp());
        mainTemperaturePowerSwitch.setChecked(event.isState());
        seekBar.setProgress(event.getTemp());
    }

    /**
     *      一些接口
     */
    interface OnSeekResultListener{
        public void onResult(SeekBar seekBar);
    }
}
