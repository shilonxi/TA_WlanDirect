package com.example.administrator.wlandirect;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DataTransferService extends IntentService
{
    private static final int SOCKET_TIMEOUT=5000;
    public static final String ACTION_SEND_FILE="com.example.android.wlandirect.SEND_DATA";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS="sd_go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT="sd_go_port";
    public static final String STRING="null";
    //建立变量

    public DataTransferService(String name)
    {
        super(name);
    }

    public DataTransferService()
    {
        super("DataTransferService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Context context=getApplicationContext();
        if(intent.getAction().equals(ACTION_SEND_FILE))
        {
            String host=intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            int port=intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            Socket socket=new Socket();
            //定义Socket
            try
            {
                Log.d("xyz","Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host,port)),SOCKET_TIMEOUT);
                Log.d("xyz","Client socket - "+socket.isConnected());
                //连接建立
                OutputStream stream=socket.getOutputStream();
                //发送数据
                String clientip=socket.getLocalAddress().toString();
                stream.write((clientip+"send"+intent.getExtras().getString(STRING)+"to"+host).getBytes());
                //传输输入框数据
            }catch (IOException e)
            {
                Log.e("xyz", e.getMessage());
            }finally
            {
                if(socket!=null)
                {
                    if(socket.isConnected())
                    {
                        try
                        {
                            socket.close();
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //关闭资源
        }
    }
}
