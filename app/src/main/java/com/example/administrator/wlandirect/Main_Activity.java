package com.example.administrator.wlandirect;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Main_Activity extends AppCompatActivity
{
    private Button discover;
    private Button stopdiscover;
    private RecyclerView mRecyclerView;
    private Button stopconnect;
    private Button senddata;
    private EditText data;
    private TextView show;
    //定义各个组件

    private DataServerAsyncTask mDataTask;
    private MyAdapter mAdapter;
    private Utils utils;
    private BroadcastReceiver mReceiver;

    private List peers=new ArrayList();
    private List<HashMap<String, String>> peersshow=new ArrayList();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private IntentFilter mFilter;
    private WifiP2pInfo info;
    public static Main_Activity mactivity;
    public static Context mContext;
    //定义全局变量

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        TextView myname=(TextView)findViewById(R.id.my_name);
        //获取实例
        myname.setText(Build.MANUFACTURER+" "+Build.MODEL);
        //显示本机信息
        mactivity=this;
        mContext = this.getBaseContext();
        initView();
        //获取实例初始化
        initIntentFilter();
        //初始化IntentFilter
        initReceiver();
        //接收初始化
        initEvents();
        //点击监听初始化
    }
    private void initEvents()
    {
        discover.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DiscoverPeers();
            }
        });
        //点击监听

        stopdiscover.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                StopDiscoverPeers();
            }
        });
        //点击监听

        stopconnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                StopConnect();
            }
        });
        //点击监听

        senddata.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent serviceIntent=new Intent(Main_Activity.this,DataTransferService.class);
                serviceIntent.setAction(DataTransferService.ACTION_SEND_FILE);
                serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS,info.groupOwnerAddress.getHostAddress());
                Log.i("address","owenerip is "+info.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT,8888);
                serviceIntent.putExtra(DataTransferService.STRING,data.getText().toString());
                Main_Activity.this.startService(serviceIntent);
                //得到groupOwner的地址，发送数据
            }
        });
        //点击监听

        mAdapter.SetOnItemClickListener(new MyAdapter.OnItemClickListener()
        {
            @Override
            public void OnItemClick(View view,int position)
            {
                CreateConnect(peersshow.get(position).get("address"),peersshow.get(position).get("name"));
            }

            @Override
            public void OnItemLongClick(View view, int position)
            {

            }
        });
        //点击监听
    }

    private void DiscoverPeers()
    {
        mManager.discoverPeers(mChannel,new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onFailure(int reason)
            {

            }
        });
    }

    private void StopDiscoverPeers()
    {
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onFailure(int reason)
            {

            }
        });
    }

    private void StopConnect()
    {
        SetButtonGone();
        mManager.removeGroup(mChannel,new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {
                SetButtonGone();
            }

            @Override
            public void onFailure(int reason)
            {
                SetButtonGone();

            }
        });
    }

    private void SetButtonGone()
    {
        senddata.setVisibility(View.GONE);
        data.setVisibility(View.GONE);
    }

    private void CreateConnect(String address,final String name)
    {
        WifiP2pDevice device;
        WifiP2pConfig config=new WifiP2pConfig();
        Log.i("xyz", address);
        config.deviceAddress=address;
        //用mac地址来建立连接
        config.wps.setup=WpsInfo.PBC;
        Log.i("address","MAC IS "+address);
        Log.i("address","lingyige youxianji"+String.valueOf(config.groupOwnerIntent));
        mManager.connect(mChannel,config,new WifiP2pManager.ActionListener()
        {
            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onFailure(int reason)
            {

            }
        });
    }

    private void initView()
    {
        discover=(Button)findViewById(R.id.bt_discover);
        stopdiscover=(Button)findViewById(R.id.bt_stopdiscover);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        stopconnect=(Button)findViewById(R.id.bt_stopconnect);
        senddata=(Button)findViewById(R.id.bt_senddata);
        data=(EditText)findViewById(R.id.edit_send);
        show=(TextView)findViewById(R.id.show);
        //获取实例
        senddata.setVisibility(View.GONE);
        data.setVisibility(View.GONE);
        //暂时不可见
        mAdapter=new MyAdapter(peersshow);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
    }

    private void initIntentFilter()
    {
        mFilter=new IntentFilter();
        mFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void initReceiver()
    {
        mManager=(WifiP2pManager)getSystemService(WIFI_P2P_SERVICE);
        //获取实例
        mChannel=mManager.initialize(this,Looper.myLooper(),null);
        WifiP2pManager.PeerListListener mPeerListListerner=new WifiP2pManager.PeerListListener()
        {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList)
            {
                peers.clear();
                peersshow.clear();
                Collection<WifiP2pDevice> aList=peersList.getDeviceList();
                peers.addAll(aList);
                for(int i=0;i<aList.size();i++)
                {
                    WifiP2pDevice a=(WifiP2pDevice)peers.get(i);
                    HashMap<String, String> map=new HashMap<String, String>();
                    map.put("name",a.deviceName);
                    map.put("address",a.deviceAddress);
                    peersshow.add(map);
                }
                mAdapter=new MyAdapter(peersshow);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(Main_Activity.this));
                mAdapter.SetOnItemClickListener(new MyAdapter.OnItemClickListener()
                {
                    @Override
                    public void OnItemClick(View view, int position)
                    {
                        CreateConnect(peersshow.get(position).get("address"),peersshow.get(position).get("name"));
                    }

                    @Override
                    public void OnItemLongClick(View view, int position)
                    {

                    }
                });
            }
        };

        WifiP2pManager.ConnectionInfoListener mInfoListener=new WifiP2pManager.ConnectionInfoListener()
        {
            @Override
            public void onConnectionInfoAvailable(final WifiP2pInfo minfo)
            {
                Log.i("xyz","InfoAvailable is on");
                info=minfo;
                if(info.groupFormed && info.isGroupOwner)
                {
                    Log.i("xyz","owner start");
                    mDataTask=new DataServerAsyncTask(Main_Activity.this,show);
                    mDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }else if(info.groupFormed)
                {
                    SetButtonVisible();
                    //使按钮可见
                    show.setText("YOU SEND DATA");
                }
            }
        };
        mReceiver=new WifiDirectBroadcastReceiver(mManager,mChannel,this, mPeerListListerner,mInfoListener);
    }

    private void SetButtonVisible()
    {
        senddata.setVisibility(View.VISIBLE);
        data.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mReceiver,mFilter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.i("xyz","hehehehehe");
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        StopConnect();
    }

    public void ResetReceiver()
    {
        unregisterReceiver(mReceiver);
        registerReceiver(mReceiver, mFilter);
    }

}



