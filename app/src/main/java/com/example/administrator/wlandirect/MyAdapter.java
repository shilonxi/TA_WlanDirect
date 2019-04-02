package com.example.administrator.wlandirect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder>
{

    public OnItemClickListener mOnItemClickListener;
    private List<HashMap<String, String>> mList;
    //定义变量

    public interface OnItemClickListener
    {
        void OnItemClick(View view,int position);
        void OnItemLongClick(View view,int position);
    }

    public void SetOnItemClickListener(OnItemClickListener listener)
    {
        this.mOnItemClickListener=listener;
    }
    //为RecyclerView添加Item的点击监听

    public MyAdapter(List<HashMap<String, String>> list)
    {
        super();
        this.mList=list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.card_item,parent,false);
        MyHolder myHolder=new MyHolder(view);
        //view是Item的根布局
        return myHolder;
    }
    //创建一个ViewHolder并返回

    @Override
    public void onBindViewHolder(final MyHolder holder,final int position)
    {
        holder.tvname.setText(mList.get(position).get("name"));
        holder.tvaddress.setText(mList.get(position).get("address"));
        if(mOnItemClickListener!=null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickListener.OnItemClick(holder.itemView, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    mOnItemClickListener.OnItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }
    //适配渲染数据到View中，增加点击事件

    public void addData(int position,HashMap map)
    {
        mList.add(position,map);
        notifyItemInserted(position);
    }

    public void removeData(int position)
    {
        mList.remove(position);
        notifyItemRemoved(position);
    }
    //增减数据

    public void RefreshView()
    {
        for(int i=0;i<getItemCount();i++)
            removeData(i);
        for(int i=0;i<getItemCount();i++)
            addData(i,mList.get(i));
    }
    //刷新

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
    //计数

    class MyHolder extends RecyclerView.ViewHolder
    {
        public TextView tvname;
        public TextView tvaddress;
        //定义变量

        public MyHolder(View View)
        {
            super(View);
            tvname=(TextView)View.findViewById(R.id.tv_name);
            tvaddress=(TextView)View.findViewById(R.id.tv_address);
            //获取实例
        }
    }
    //继承与初始化
}