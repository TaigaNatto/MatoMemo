package com.example.t_robop.matomemo;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by user on 2017/06/28.
 */

public class MenuSetting extends Activity {

    //メニューバーの作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    //メニューが選択されたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //addしたときのIDで識別
        switch(item.getItemId())
        { case R.id.action_settings:
            Log.d("test", "Settings Selected!");
            break;
        }
        return true;
    }

}
