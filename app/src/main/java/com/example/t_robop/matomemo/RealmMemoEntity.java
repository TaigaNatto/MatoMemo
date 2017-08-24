package com.example.t_robop.matomemo;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by taiga on 2017/06/26.
 */

//メモ保存用
public class RealmMemoEntity extends RealmObject {

    private int id;

    private String memo;
    private int date;//20170627(こんな感じで保存)
    private int time;//1816(18:16のときはこんなふうに)
    private String folder="未分類";
    private int folderId=-1;
    public RealmList<MatomeWord> words;//設定されてる単語リスト

    @Ignore
    private int sessionId;

    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id=id;
    }

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

    public void setFolderId(int folderId){
        this.folderId=folderId;
    }
    public int getFolderId(){
        return this.folderId;
    }

    public int    getSessionId() { return sessionId; }
    public void   setSessionId(int sessionId) { this.sessionId = sessionId; }

    public RealmList<MatomeWord> getWords(){
        return this.words;
    }
    public void setWords(RealmList<MatomeWord> words){
        this.words=words;
    }
}
