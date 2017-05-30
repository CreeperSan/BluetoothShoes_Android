package creeper_san.myshoes;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anderson.dashboardview.view.DashboardView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import creeper_san.myshoes.base.BaseFragment;
import creeper_san.myshoes.event.WeightDataEvent;
import creeper_san.myshoes.event.WeightDataResultEvent;
import creeper_san.myshoes.event.WeightEvent;

public class MainWeightFragment extends BaseFragment {
    @BindView(R.id.mainTemperatureMeter)
    DashboardView mainTemperatureMeter;
    @BindView(R.id.mainWeightRecordBtn)
    Button recordBtn;
    @BindView(R.id.mainWeightList)
    RecyclerView recyclerView;

    private OnRecordListener onRecordListener;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;

    @Override
    protected void onViewInflate() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemAdapter = new ItemAdapter();
        recyclerView.setAdapter(itemAdapter);
        postStickyEvent(new WeightDataEvent());
        itemList = new ArrayList<>();
    }

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

    @Subscribe()
    public void onWeightDataResultEvent(WeightDataResultEvent event){
        log("收到回应！");
        itemList = event.getList();
        if (itemList.size() > 0){
            itemAdapter.notifyDataSetChanged();
        }
    }

    public OnRecordListener getOnRecordListener() {
        return onRecordListener;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    public static class Item{
        private int year;
        private int month;
        private int day;
        private int weight;

        public Item(int year, int month, int day, int weight) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.weight = weight;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder>  {

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(getActivity().getLayoutInflater().inflate(R.layout.item_weight,parent,false));
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            if (itemList.size() == 1){
                holder.imageView.setImageResource(R.drawable.point_point);
            }else {
                if (position==0){
                    holder.imageView.setImageResource(R.drawable.point_down);
                }else if (position == itemList.size()-1){
                    holder.imageView.setImageResource(R.drawable.point_up);
                }else {
                    holder.imageView.setImageResource(R.drawable.point_all);
                }
            }
            Item item = itemList.get(position);
            holder.weight.setText(item.getWeight()+"Kg");
            holder.time.setText(item.getYear()+" - "+item.getMonth()+" - "+item.getDay());
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }
    class ItemHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.itemPoint)ImageView imageView;
        @BindView(R.id.itemWeight)TextView weight;
        @BindView(R.id.itemTime)TextView time;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    interface OnRecordListener {
        public void onRecord();
    }
}
