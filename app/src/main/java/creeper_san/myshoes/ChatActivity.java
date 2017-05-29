package creeper_san.myshoes;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import creeper_san.myshoes.base.BaseActivity;
import creeper_san.myshoes.bean.ChatBean;
import creeper_san.myshoes.bean.SocialBean;
import creeper_san.myshoes.flag.BroadcastKey;
import creeper_san.myshoes.flag.IntentKey;

public class ChatActivity extends BaseActivity implements ServiceConnection {
    @BindView(R.id.chatRecyclerView) RecyclerView recyclerView;
    @BindView(R.id.chatEdtInput) EditText edtInput;

    private static int TYPE_SELF = 1;
    private static int TYPE_OTHER = 0;

    private String userName;
    private String nickName;
    private ShoesService shoesService;
    private ChatAdapter chatAdapter;
    private List<ChatBean> chatBeanList = new ArrayList<>();
    private ChatMessageReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent();
        initReceiver();
        initActionbar();
        initRecyclerView();
        toStartBindServer(ShoesService.class,this);
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter(BroadcastKey.ACTION_NEW_CHAT_MESSAGE);
        receiver = new ChatMessageReceiver();
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shoesService.setChatting(false);
        shoesService.setChattingUserName("");
        unregisterReceiver(receiver);
        unbindService(this);
    }

    private void initIntent() {
        Intent intent = getIntent();
        userName = intent.getStringExtra(IntentKey.KEY_USER_NAME);
        nickName = intent.getStringExtra(IntentKey.KEY_USER_NICK_NAME);
        log("获得Intent "+userName+" "+nickName);
    }

    private void initActionbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle("@" + userName);
        }
        setTitle(nickName);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_chat;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        shoesService = ((ShoesService.ShoesServerBinder)binder).getService();
        log("查找记录 "+userName+" "+nickName);
        chatBeanList = shoesService.getUserChatList(userName);
        chatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(chatBeanList.size());//滚动到底部

        shoesService.setChatting(true);
        shoesService.setChattingUserName(userName);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @OnClick(R.id.chatBtnSend)
    public void onClick() {
        if (shoesService==null){
            toast("服务还没准备好，请稍等");
            return;
        }
        final String text = edtInput.getText().toString().trim();
        if (text.equals("")){
            toast("说点什么吧");
            return;
        }
        shoesService.sendMessage(userName, text, new ShoesService.OnInterServerResultListener() {
            @Override
            public void onResult(String resultStr) {
                String tempTimeStamp = String.valueOf(System.currentTimeMillis());
                chatBeanList.add(new ChatBean("1",text,tempTimeStamp));
                chatAdapter.notifyItemInserted(chatBeanList.size());
                recyclerView.smoothScrollToPosition(chatBeanList.size());//滚动到底部
                shoesService.insertMessageToUserTable(userName,tempTimeStamp,text);//插入到数据库,同时更新index
                edtInput.setText("");
            }

            @Override
            public void onFail(String reason) {
                toast("发送失败，请检查你的网络连接");
            }
        });
    }


    class ChatMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra(BroadcastKey.KEY_CONTENT);
            chatBeanList.add(new ChatBean("0",content,String.valueOf(System.currentTimeMillis())));
            chatAdapter.notifyItemInserted(chatBeanList.size());
            recyclerView.smoothScrollToPosition(chatBeanList.size());
        }
    }
    class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_SELF){
                return new ChatSelfViewHolder(layoutInflater.inflate(R.layout.item_chat_self_view_holder,parent,false));
            }else {
                return new ChatOtherViewHolder(layoutInflater.inflate(R.layout.item_chat_other_view_holder,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChatBean bean = chatBeanList.get(position);
            if (holder instanceof ChatOtherViewHolder){
                ((ChatOtherViewHolder)holder).content.setText(bean.getContent());
            }else {
                ((ChatSelfViewHolder)holder).content.setText(bean.getContent());
            }
        }

        @Override
        public int getItemCount() {
            return chatBeanList==null?0:chatBeanList.size();
        }

        @Override
        public int getItemViewType(int position) {
            ChatBean chatBean = chatBeanList.get(position);
            if (chatBean.isFromSelf().equals("0")){
                return TYPE_OTHER;
            }else {
                return TYPE_SELF;
            }
        }
    }
    class ChatSelfViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.itemChatSelfText)TextView content;

        public ChatSelfViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    class ChatOtherViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.itemChatOtherText)TextView content;

        public ChatOtherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
