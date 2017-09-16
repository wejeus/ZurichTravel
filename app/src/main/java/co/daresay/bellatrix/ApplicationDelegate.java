package co.daresay.bellatrix;

import android.app.Application;

import com.isalldigital.reclaim.AdapterDelegatesManager;

public class ApplicationDelegate extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AdapterDelegatesManager.init();
    }
}
