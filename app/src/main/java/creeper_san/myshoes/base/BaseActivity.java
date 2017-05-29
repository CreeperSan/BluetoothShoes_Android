package creeper_san.myshoes.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;
import creeper_san.myshoes.R;

public abstract class BaseActivity extends AppCompatActivity {
    public String TAG = getClass().getSimpleName();
    protected Context context = this;
    protected LayoutInflater layoutInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutID = getLayoutID();
        if (layoutID!=0){
            setContentView(layoutID);
        }
        layoutInflater = LayoutInflater.from(context);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    protected abstract int getLayoutID();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringCommandEvent(String command){};

    public <T> void postEvent(T event){
        EventBus.getDefault().post(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     *      服务相关
     */
    protected void toStartServer(Class serviceCls){
        Intent intent = new Intent(this,serviceCls);
        startService(intent);
    }
    protected void toBindServer(Class serviceCls, ServiceConnection connection){
        Intent intent = new Intent(this,serviceCls);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }
    protected void toStartBindServer(Class serviceCls, ServiceConnection connection){
        Intent intent = new Intent(this,serviceCls);
        startService(intent);
        try {
            bindService(intent,connection,BIND_AUTO_CREATE);
        } catch (Exception e) {
        }
    }

    /**
     *      系统版本
     */
    protected boolean checkSystemVersionOver(int systemVersion){
        return Build.VERSION.SDK_INT > systemVersion;
    }
    protected boolean checkSystemVersionOverOrEqual(int systemVersion){
        return Build.VERSION.SDK_INT >= systemVersion;
    }
    protected boolean checkSystemVersionBelow(int systemVersion){
        return Build.VERSION.SDK_INT < systemVersion;
    }
    protected boolean checkSystemVersionBelowOrEqual(int systemVersion){
        return Build.VERSION.SDK_INT <= systemVersion;
    }
    protected boolean checkSystemVersionEqual(int systemVersion){
        return Build.VERSION.SDK_INT == systemVersion;
    }

    /**
     *      Dialog相关
     */

    protected void dialogSimple(String title,String content,String posText,@Nullable DialogInterface.OnClickListener posListener,
                                String negText,@Nullable DialogInterface.OnClickListener negListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton(posText, posListener);
        builder.setNegativeButton(negText,negListener);
        builder.show();
    }
    protected void dialogView(String title, View view, String posText, @Nullable DialogInterface.OnClickListener posListener,
                              String negText, @Nullable DialogInterface.OnClickListener negListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(view);
        builder.setPositiveButton(posText, posListener);
        builder.setNegativeButton(negText,negListener);
        builder.show();
    }
    protected void dialogSimpleInput(String title, String hint, int maxLines, int inputType,String posText,
                                     @Nullable final OnDialogInputListener onDialogListener, String negText,
                                     @Nullable DialogInterface.OnClickListener negListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        View textInputView = layoutInflater.inflate(R.layout.dialog_simple_text_input,null);
        final TextInputEditText editText = (TextInputEditText) textInputView.findViewById(R.id.dialogSimpleTextInputText);
        editText.setHint(hint);
        editText.setMaxLines(maxLines);
        editText.setInputType(inputType);
        builder.setView(textInputView);
        builder.setPositiveButton(posText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onDialogListener!=null){
                    onDialogListener.onClick(editText,dialog,which);
                }
            }
        });
        builder.setNegativeButton(negText,negListener);
        builder.show();
    }
    protected void dialogSimpleInput(String title, String hint,String preText, int maxLines, int inputType,String posText,
                                     @Nullable final OnDialogInputListener onDialogListener, String negText,
                                     @Nullable DialogInterface.OnClickListener negListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        View textInputView = layoutInflater.inflate(R.layout.dialog_simple_text_input,null);
        final TextInputEditText editText = (TextInputEditText) textInputView.findViewById(R.id.dialogSimpleTextInputText);
        final TextInputLayout editTextLayout = (TextInputLayout) textInputView.findViewById(R.id.dialogSimpleTextInputLayout);
        editTextLayout.setHint(hint);
        editText.setMaxLines(maxLines);
        editText.setInputType(inputType);
        editText.setText(preText);
        builder.setView(textInputView);
        builder.setPositiveButton(posText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onDialogListener!=null){
                    onDialogListener.onClick(editText,dialog,which);
                }
            }
        });
        builder.setNegativeButton(negText,negListener);
        builder.show();
    }
    protected void dialogList(String title, @ArrayRes int itemId,DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(itemId, listener);
        builder.show();
    }
    public interface OnDialogInputListener{
        void onClick(TextInputEditText editText, DialogInterface dialog, int which);
    }

    /**
     *      权限相关
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected boolean checkHasPermission(String permission){
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void requestPermission(String permission, int requestCode){
        requestPermissions(new String[]{permission},requestCode);
    }

    /**
     *      Activity跳转相关
     */

    private void checkFinish(boolean isFinish){
        if (isFinish){
            finish();
        }
    }
    protected void toNextActivity(Class cls, boolean isFinish){
        Intent intent = new Intent(this,cls);
        startActivity(intent);
        checkFinish(isFinish);
    }
    protected void toNextActivity(Class cls, String extraKey, String extraValue, boolean isFinish){
        Intent intent = new Intent(this,cls);
        intent.putExtra(extraKey,extraValue);
        startActivity(intent);
        checkFinish(isFinish);
    }
    protected void toNextActivity(Class cls, String[] extraKeys, String[] extraValues,boolean isFinish){
        Intent intent = new Intent(this,cls);
        for (int i=0;i<extraKeys.length;i++){
            intent.putExtra(extraKeys[i],extraValues[i]);
        }
        startActivity(intent);
        checkFinish(isFinish);
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
}
