package com.example.t_robop.matomemo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by taiga on 2017/06/26.
 */

public class RealmEntity extends RealmObject {

    private String memo;

    @Ignore
    private int sessionId;

    public void setMemo(String memo){
        this.memo=memo;
    }
    public String getMemo(){
        return this.memo;
    }

    public int    getSessionId() { return sessionId; }
    public void   setSessionId(int sessionId) { this.sessionId = sessionId; }
}
