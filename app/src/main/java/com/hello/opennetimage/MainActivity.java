package com.hello.opennetimage;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 访问网络图片，
 * 添加网络访问权限
 */
public class MainActivity extends AppCompatActivity {

    private ImageView iv;

    //Handler消息处理器
    Handler handler = new Handler() {

        //此方法在主线程中调用，可以用来刷新UI
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:

                    //把位图对象显示到ImageView
                    iv.setImageBitmap((Bitmap) msg.obj);
                    break;

                case 0:
                    Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView iv = (ImageView) findViewById(R.id.iv);

        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //下载图片
                new Thread() {
                    @Override
                    public void run() {

                        //1 确定网址
                        String path = "http://d02.res.meilishuo.net/pic/l/1d/42/94ed817aeb9f8ef007c7ab762812_600_800.jpg";


                        try {
                            // 2 把网址封装成URL
                            URL url = new URL(path);

                            // 3获取连接对象
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            //4 对连接对象进行初始化
                            //设置请求方法，大写
                            conn.setRequestMethod("GET");

                            //设置连接超时
                            conn.setConnectTimeout(5000);

                            //设置读取超时
                            conn.setReadTimeout(5000);

                            //5 发送请求，与服务器建立连接
                            conn.connect();

                            //如果响应码为200，说明请求成功
                            if (conn.getResponseCode() == 200) {

                                //获取服务器响应头中的流，流里的数据就是客户端请求的数据
                                InputStream is = conn.getInputStream();

                                //读取出流里的数据，并构造成位图对象
                                Bitmap bm = BitmapFactory.decodeStream(is);

                                //更新Ui 必须在主线程的Handler.handleMessage方法里执行
//                                ImageView iv = (ImageView) findViewById(R.id.iv);
//                                //把位图对象显示到ImageView
//                                iv.setImageBitmap(bm);

                                //把消息发送至主线程的消息队列
                                Message msg = new Message();
                                //消息可以携带任何数据被发送出去,
                                //消息携带位图对象被发送到主线的消息队列，
                                //然后由消息looper(轮询器)转发发消息传给Handler,
                                //最后由handleMessage来处理消息
                                msg.obj = bm;

                                //1 表示消息发送成功
                                msg.what = 1;
                                handler.sendMessage(msg);


                            } else {

                                Message msg = handler.obtainMessage();

                                //0 表示消息发送失败
                                msg.what = 0;
                                handler.sendMessage(msg);


                            }

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }.start();
//


            }
        });

    }
}
