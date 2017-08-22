package com.example.t_robop.matomemo;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/*
 * fromHtmlメソッドのOSバージョン管理クラス
 */

public class HtmlCompat {
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static Spanned fromHtml(String source){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return Html.fromHtml(source,Html.FROM_HTML_MODE_LEGACY);
        }else {
            return Html.fromHtml(source);
        }
    }
}
