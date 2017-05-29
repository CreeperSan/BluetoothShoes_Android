package creeper_san.myshoes;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import creeper_san.myshoes.base.BaseFragment;
import creeper_san.myshoes.event.VibrateEvent;
import creeper_san.myshoes.event.VibrateResultEvent;

public class MainVibrationFragment extends BaseFragment {
    @BindView(R.id.mainVibrateImage)ImageView imageView;
    @BindView(R.id.mainVibrateHintText)TextView textView;
    @BindView(R.id.mainVibrateSeekBar)SeekBar seekBar;

    private int level = 0;
    private int levelTemp = -1;
    private int levelSend = -1;
    private OnVibrationChangeListener listener;

    public void setLevel(int level){
        int oldLevel = this.level;
        this.level = level;
        freshImage();
        if (listener!=null){
            listener.onChange(oldLevel,level,false);
        }
    }

    public void reset(){

    }

    public void setLevelWithoutListener(int level){
        int oldLevel = this.level;
        this.level = level;
        freshImage();
    }

    private void freshImage(){
        if (level==0){
            imageView.setImageResource(R.drawable.vibrate_shoes_none);
        }else if (level==1){
            imageView.setImageResource(R.drawable.vibrate_shoes_low);
        }else if (level==2){
            imageView.setImageResource(R.drawable.vibrate_shoes_normal);
        }else {
            imageView.setImageResource(R.drawable.vibrate_shoes_high);
        }
    }

    public int getLevel() {
        return level;
    }

    public OnVibrationChangeListener getListener() {
        return listener;
    }

    public void setListener(OnVibrationChangeListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onViewInflate() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldLevel = level;
                level++;
                level%=4;
                freshImage();
                if (listener!=null){
                    listener.onChange(oldLevel,level,true);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setHintText(progress);
                levelSend = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (levelTemp==-1){
                    levelTemp = seekBar.getProgress();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                postEvent(new VibrateEvent(levelSend,levelTemp));
                levelTemp = -1;
                levelSend = -1;
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVibrateResultEvent(VibrateResultEvent event){
        if (!event.isSuccess()){
            seekBar.setProgress(event.getOrigin());
            setHintText(event.getOrigin());
            toast("尚未连接到鞋子噢");
        }
    }

    public void setHintText(int value){
        switch (value){
            case 0:textView.setText("关");break;
            case 1:textView.setText("弱");break;
            case 2:textView.setText("稍弱");break;
            case 3:textView.setText("中");break;
            case 4:textView.setText("强");break;
            case 5:textView.setText("更强");break;
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main_vibration;
    }

    interface OnVibrationChangeListener{
        public void onChange(int oldLevel,int level,boolean isByUser);
    }
}
