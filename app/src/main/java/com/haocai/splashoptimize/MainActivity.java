package com.haocai.splashoptimize;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewStub;

import java.lang.ref.WeakReference;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private SplashFragment splashFragment;
    private ViewStub viewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashFragment = new SplashFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, splashFragment);
        transaction.commit();

        /**
         *这么写不好判断视图有没有加载完
         */
        //		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mProgressBar.setVisibility(View.GONE);
//				iv.setVisibility(View.VISIBLE);
//			}
//		}, 2500);

        viewStub = (ViewStub) findViewById(R.id.content_viewstub);

        //1.判断当窗体加载完毕的时候,立马再加载真正的布局进来
        getWindow().getDecorView().post(new Runnable() {

            @Override
            public void run() {
                // 开启延迟加载
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        //将viewstub加载进来
                        viewStub.inflate();
                    }
                });
            }
        });

        //2.判断当窗体加载完毕的时候执行,延迟一段时间做动画。
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                // 开启延迟加载,也可以不用延迟可以立马执行（我这里延迟是为了实现fragment里面的动画效果的耗时）
                mHandler.postDelayed(new DelayRunnable(MainActivity.this, splashFragment), 2000);
            }
        });
    }


    static class DelayRunnable implements Runnable {

        private WeakReference<Context> contextWeakReference;
        private WeakReference<SplashFragment> splashFragmentWeakReference;

        public DelayRunnable(Context context, SplashFragment f) {
            contextWeakReference = new WeakReference<Context>(context);
            splashFragmentWeakReference = new WeakReference<SplashFragment>(f);
        }

        @Override
        public void run() {
            //移除Fragment
            if (contextWeakReference != null) {
                SplashFragment splashFragment = splashFragmentWeakReference.get();
                if (splashFragment == null) {
                    return;
                }
                FragmentActivity activity = (FragmentActivity) contextWeakReference.get();
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.remove(splashFragment);
                transaction.commit();
            }

        }
    }
}

