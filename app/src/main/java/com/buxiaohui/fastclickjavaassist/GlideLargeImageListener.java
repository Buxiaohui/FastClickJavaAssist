package com.buxiaohui.fastclickjavaassist;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;

public class GlideLargeImageListener<R> implements RequestListener<R> {
    public static final String TAG = "GlideLargeImageListener";

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<R> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(R resource, Object model, Target<R> target, DataSource dataSource,
                                   boolean isFirstResource) {
        int width = 0;
        int heigh = 0;
        if (target instanceof ViewTarget) {
            View view = ((ViewTarget) target).getView();
            width = view.getWidth();
            heigh = view.getHeight();
        }
        Log.e(TAG, "onResourceReady,resource:" + resource + "\n"
                + ",model:" + model + "\n"
                + ", w*h :" + width + "*" + heigh + "\n"
                + ",dataSource:" + dataSource);
        return false;
    }
}
