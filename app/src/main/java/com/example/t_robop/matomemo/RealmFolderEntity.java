package com.example.t_robop.matomemo;

import io.realm.RealmObject;

/**
 * Created by taiga on 2017/06/27.
 */

//フォルダ保存用
public class RealmFolderEntity extends RealmObject {

    private String folderName;
    private String roomName;//教室名
    private String teacherName;//教員名
    private int startTime;//開始時間(一限：9:20➝0920)
    private int endTime;//終了時間

    public void setFolderName(String folderName){
        this.folderName=folderName;
    }
    public String getFolderName(){
        return this.folderName;
    }

    public void setRoomName(String roomName){
        this.roomName=roomName;
    }
    public String getRoomName(){
        return this.roomName;
    }

    public void setTeacherName(String teacherName){
        this.teacherName=teacherName;
    }
    public String getTeacherName(){
        return this.teacherName;
    }

    public void setStartTime(int startTime){
        this.startTime=startTime;
    }
    public int getStartTime(){
        return this.startTime;
    }

    public void setEndTime(int endTime){
        this.endTime=endTime;
    }
    public int getEndTime(){
        return this.endTime;
    }
}
