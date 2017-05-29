package creeper_san.myshoes;

import android.content.ComponentName;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import creeper_san.myshoes.base.BaseActivity;
import creeper_san.myshoes.bean.ChatUserBean;
import creeper_san.myshoes.flag.IntentKey;
import creeper_san.myshoes.helper.TimeStampHelper;

public class MessageActivity extends BaseActivity implements ServiceConnection {
    @BindView(R.id.messageRecyclerList)
    RecyclerView recyclerView;

    private ShoesService shoesService;
    private MessageAdapter adapter;
    private List<ChatUserBean> chatUserBeanList = new ArrayList<>();

    @Override
    protected int getLayoutID() {
        return R.layout.activity_message;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toStartBindServer(ShoesService.class,this);
        initActionBar();
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shoesService!=null){
            if (shoesService.isMessageActivityNeedFresh()){
                chatUserBeanList = shoesService.getChatUserBeanList();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("消息");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
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
        shoesService.getUnreadMessage(shoesService.getUserName(), new ShoesService.OnInterServerResultListener() {
            @Override
            public void onResult(String resultStr) {
                chatUserBeanList = shoesService.getChatUserBeanList();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String reason) {
                log("获取失败");
            }
        });
        chatUserBeanList = shoesService.getChatUserBeanList();
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder>{

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MessageViewHolder(layoutInflater.inflate(R.layout.item_messsage_list,parent,false));
        }

        @Override
        public void onBindViewHolder(final MessageViewHolder holder, int position) {
            ChatUserBean bean = chatUserBeanList.get(position);
            holder.userName.setText("@"+bean.getUserName());
            holder.nickName.setText(bean.getNickName());
            holder.content.setText(bean.getContent());
            holder.time.setText(TimeStampHelper.getUpToNowTimeString(bean.getTimeStamp()));
            if (bean.getNeedRead().equals("1")){
                holder.point.setVisibility(View.VISIBLE);
            }else {
                holder.point.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    if (shoesService!=null){
                        ChatUserBean tempBean = chatUserBeanList.get(pos);
                        shoesService.readMessage(tempBean.getUserName());
                        tempBean.setNeedRead("0");
                        adapter.notifyItemChanged(pos);
                        toNextActivity(ChatActivity.class,
                                new String[]{IntentKey.KEY_USER_NAME,IntentKey.KEY_USER_NICK_NAME},
                                new String[]{tempBean.getUserName(),tempBean.getNickName()},false);
                    }else {
                        toast("还在准备中，请稍等");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return (chatUserBeanList==null)?0:chatUserBeanList.size();
        }
    }
    class MessageViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.itemMessageHead)ImageView head;
        @BindView(R.id.itemMessageUnread)ImageView point;
        @BindView(R.id.itemMessageUserName)TextView userName;
        @BindView(R.id.itemMessageTime)TextView time;
        @BindView(R.id.itemMessageNickName)TextView nickName;
        @BindView(R.id.itemMessageContent)TextView content;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
