package com.buxiaohui.fastclickjavaassist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class FirstFragment extends Fragment {
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        ImageView imageView = view.findViewById(R.id.image);
        ImageView imageView1 = view.findViewById(R.id.image1);
        ImageView imageView2 = view.findViewById(R.id.image2);
        String url =
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb.zol-img.com"
                        + ".cn%2Fdesk%2Fbizhi%2Fimage%2F1%2F1680x1050%2F1349289433496.jpg&refer=http%3A%2F%2Fb"
                        + ".zol-img.com.cn&app=2002&size=f9999,"
                        + "10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1617880337&t=1abd69c17b61f493913cf652714c9b5c";
        final String url_1 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg"
                + ".com%2Fnewsapp_match%2F0%2F11212600837%2F0.jpg&refer=http%3A%2F%2Finews.gtimg"
                + ".com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1618573295&t"
                + "=5a43eafe571840551a86f1483b4afba1";
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
        Glide.with(this).load(R.drawable.test_img).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView1);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url1 = new URL(url_1);
                    HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(2000);
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.connect();
                    Log.e(TAG, "urlConnection.getResponseCode():" + urlConnection.getResponseCode());
                    Log.e(TAG, "url_1:" + url_1);
                    Log.e(TAG, "len:" + urlConnection.getContentLength());
                    InputStream stream = urlConnection.getInputStream();
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(stream);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "bitmap e:" + e);
                    }
                    Log.e(TAG, "bitmap:" + bitmap);
                    if (bitmap != null) {
                        Log.e(TAG, "bitmap(w*h) :" + bitmap.getWidth() + "*" + bitmap.getHeight());
                    }
                    Bitmap bitmap1 = null;
                    try {
                        bitmap1 = decodeBitmapFromBytes(readStream(stream));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "bitmap1:" + bitmap1);
                    if (bitmap1 != null) {
                        Log.e(TAG, "bitmap1(w*h) :" + bitmap1.getWidth() + "*" + bitmap1.getHeight());
                    }
                    stream.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "e:"+e);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "e:"+e);
                }
            }
        }.start();

    }
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
//        inStream.close();
        return data;
    }
    public static Bitmap decodeBitmapFromBytes(byte[] ib) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bm = BitmapFactory.decodeByteArray(ib, 0, ib.length, options);
        return bm;
    }



    public static final String TAG = "FirstFragment";
}