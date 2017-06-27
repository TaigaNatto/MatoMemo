package com.example.t_robop.matomemo;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.DatePickerDialog.OnDateSetListener;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;


public class FolderCreateActivity extends FragmentActivity implements OnDateSetListener {

    private TextView textViewfront;
    private TextView textViewrear;
    DatePick newFragment;
    DatePick secondFragment;
    int flag=0;


    final Calendar c = Calendar.getInstance();
    int gYear = c.get(Calendar.YEAR);
    int gMonth = c.get(Calendar.MONTH);
    int gDay = c.get(Calendar.DAY_OF_MONTH);

    int rearYear = c.get(Calendar.YEAR);
    int rearMonth = c.get(Calendar.MONTH);
    int rearDay = c.get(Calendar.DAY_OF_MONTH);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_create);

        textViewfront = (TextView) findViewById(R.id.button1);
        textViewrear = (TextView) findViewById(R.id.button2);

        newFragment = new DatePick(2017,1,1);
        secondFragment=new DatePick(2017,1,1);



    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        switch (flag) {
            case 1:
                textViewfront.setText(String.valueOf(year) + "/ " + String.valueOf(monthOfYear + 1) + "/ " + String.valueOf(dayOfMonth));
                Log.d("date",String.valueOf(year));
                Log.d("date",String.valueOf(monthOfYear));
                Log.d("date",String.valueOf(dayOfMonth));
                gYear = year;
                gMonth = monthOfYear;
                gDay = dayOfMonth;
                break;
            case 2:
                textViewrear.setText(String.valueOf(year) + "/ " + String.valueOf(monthOfYear + 1) + "/ " + String.valueOf(dayOfMonth));
                Log.d("date",String.valueOf(year));
                Log.d("date",String.valueOf(monthOfYear));
                Log.d("date",String.valueOf(dayOfMonth));
                rearYear = year;
                rearMonth = monthOfYear;
                rearDay = dayOfMonth;
                break;
        }
    }

    public void showDatePickerDialog(View v) {
        switch (v.getId()) {
            case R.id.button1:
                newFragment = new DatePick(gYear,gMonth,gDay);
                newFragment.show(getSupportFragmentManager(), "datePicker");
                flag=1;
                break;

            case R.id.button2:
                secondFragment = new DatePick(rearYear,rearMonth,rearDay);
                secondFragment.show(getSupportFragmentManager(), "datePicker");
                flag=2;
                break;
        }
    }

}
