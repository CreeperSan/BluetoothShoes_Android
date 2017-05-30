package creeper_san.myshoes.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import creeper_san.myshoes.event.WeatherEvent;


public abstract class BaseFragment extends Fragment {
    public String TAG = getClass().getSimpleName();
    public Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        int layoutID = getLayoutID();
        if (layoutID!=0){
            View view = inflater.inflate(layoutID,container,false);
            ButterKnife.bind(this,view);
            onViewInflate();
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringCommandEvent(String command){};

    public <T> void postEvent(T event){
        EventBus.getDefault().post(event);
    }
    public <T> void postStickyEvent(T event){
        EventBus.getDefault().postSticky(event);
    }

    protected void onViewInflate(){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSelf();
    }

    protected void initSelf() {
        postEvent(new WeatherEvent(true));
    }

    protected void onViewCreated(){

    }

    /**
     *      Toast相关
     */
    protected void toast(String content){
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }
    protected void toastLong(String content){
        Toast.makeText(context,content,Toast.LENGTH_LONG).show();
    }

    /**
     *      Log相关
     */
    protected void log(String content){
        Log.i(TAG,content);
    }
    protected void logv(String content){
        Log.v(TAG,content);
    }
    protected void logd(String content){
        Log.d(TAG,content);
    }
    protected void logw(String content){
        Log.w(TAG,content);
    }
    protected void loge(String content){
        Log.e(TAG,content);
    }

    protected abstract int getLayoutID();

}
