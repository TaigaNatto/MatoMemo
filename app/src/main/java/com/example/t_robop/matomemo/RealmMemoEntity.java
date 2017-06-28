package com.example.t_robop.matomemo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by taiga on 2017/06/26.
 */

//メモ保存用
public class RealmMemoEntity extends RealmObject {

    private String memo;
    private int date;//20170627(こんな感じで保存)
    private int time;//1816(18:16のときはこんなふうに)
    private String folder;

    @Ignore
    private int sessionId;

    public void setMemo(String memo){
        this.memo=memo;
    }
    public String getMemo(){
        return this.memo;
    }

    public void setDate(int date){
        this.date=date;
    }
    public int getDate(){
        return this.date;
    }

    public void setTime(int time){
        this.time=time;
    }
    public int getTime(){
        return this.time;
    }

    public void setFolder(String folder){
        this.folder=folder;
    }
    public String getFolder(){
        return this.folder;
    }

    public int    getSessionId() { return sessionId; }
    public void   setSessionId(int sessionId) { this.sessionId = sessionId; }
}
