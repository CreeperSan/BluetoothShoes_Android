package creeper_san.myshoes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import creeper_san.myshoes.base.BaseFragment;
import creeper_san.myshoes.bean.SocialBean;
import creeper_san.myshoes.helper.TimeStampHelper;


public class MainSocialFragment extends BaseFragment {
    @BindView(R.id.fragmentSocialList)RecyclerView recyclerView;
    @BindView(R.id.fragmentSocialLayout)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fragmentSocialCreate)FloatingActionButton floatingActionButton;

    private List<SocialBean> socialBeanList;
    private SocialAdapter adapter;
    private OnStartRefreshListener listener;
    private OnSendSocialListener sendSocialListener;
    private OnSocialClickListener socialClickListener;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main_social;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendSocialListener!=null){
                    sendSocialListener.onSend();
                }
            }
        });
    }

    public void notifyNewSocialInsert(String content,String userName,String nickName,String timeStamp,String pic){
        SocialBean bean = new SocialBean(content,pic,userName,nickName,timeStamp);
        socialBeanList.add(0,bean);
        adapter.notifyItemInserted(0);
    }

    @Override
    protected void onViewInflate() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (listener!=null){
                    listener.onRefresh(swipeRefreshLayout);
                }
            }
        });
    }

    public void setRefreshing(boolean status){
        swipeRefreshLayout.setRefreshing(status);
    }

    public void setDataAndFresh(List<SocialBean> socialBeanList){
        this.socialBeanList = socialBeanList;
        adapter = new SocialAdapter();
        recyclerView.setAdapter(adapter);
    }



    public void setSwipeListener(final OnStartRefreshListener listener){
        this.listener = listener;
    }
    public void setSendSocialListener(OnSendSocialListener sendSocialListener) {
        this.sendSocialListener = sendSocialListener;
    }
    public void setSocialClickListener(OnSocialClickListener socialClickListener) {
        this.socialClickListener = socialClickListener;
    }

    class SocialAdapter extends RecyclerView.Adapter<SocialViewHolder>{
        @Override
        public SocialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SocialViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_social,parent,false));
        }
        @Override
        public void onBindViewHolder(SocialViewHolder holder, int position) {
            final SocialBean bean = socialBeanList.get(position);
            holder.nickName.setText(bean.getNickName());
            holder.userName.setText("@"+bean.getUserName());
            holder.time.setText(TimeStampHelper.getUpToNowTimeString(bean.getTimeStamp()));
            holder.content.setText(bean.getContent());
            if (bean.isHaveImage()){
                holder.pic.setVisibility(View.GONE);
            }else {
                holder.pic.setVisibility(View.GONE);
            }
            if (socialClickListener!=null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        socialClickListener.onClick(bean);
                    }
                });
            }
        }
        @Override
        public int getItemCount() {
            return (socialBeanList==null)?0:socialBeanList.size();
        }
    }
    class SocialViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.beanSocialName) TextView nickName;
        @BindView(R.id.beanSocialUserName) TextView userName;
        @BindView(R.id.beanSocialTime) TextView time;
        @BindView(R.id.beanSocialContent) TextView content;
        @BindView(R.id.beanSocialImage)ImageView pic;

        public SocialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    interface OnStartRefreshListener{
        public void onRefresh(SwipeRefreshLayout self);
    }
    interface OnSendSocialListener{
        public void onSend();
    }
    interface OnSocialClickListener{
        public void onClick(SocialBean bean);
    }
}
