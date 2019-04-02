package com.example.administrator.wlandirect;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DataServerAsyncTask extends AsyncTask<Void,Void,String>
{
    private TextView statusText;
    private Main_Activity activity;
    //定义变量

    public DataServerAsyncTask(Main_Activity activity,View statusText)
    {
        this.statusText=(TextView)statusText;
        this.activity=activity;
        //赋值
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            Log.i("xyz","data doinback");
            ServerSocket serverSocket=new ServerSocket(8888);
            //监听
            Log.i("xyz","串口创建完成");
            Socket client=serverSocket.accept();
            //等待连接
            Log.i("xyz","阻塞已取消");
            InputStream inputstream=client.getInputStream();
            //输入
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            int i;
            while((i=inputstream.read())!=-1)
            {
                baos.write(i);
                //读取
            }
            String str=baos.toString();
            serverSocket.close();
            //关闭
            return str;
        }catch (IOException e)
        {
            Log.e("xyz",e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        Log.i("xyz", "data onpost");
        Toast.makeText(activity,"receive"+result,Toast.LENGTH_SHORT).show();
        if (result != null) {
            statusText.setText("RESULT IS "+result);
        }
        //显示

    }

    @Override
    protected void onPreExecute()
    {

    }
}



