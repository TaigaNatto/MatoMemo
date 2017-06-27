package com.example.t_robop.matomemo;

import android.app.Application;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;

/**
 * Created by taiga on 2017/06/26.
 */

public class RealmApplication extends Application {

    public void onCreate() {
        super.onCreate();
        // Realm設定ここから
        Realm.init(this);
    }

}
