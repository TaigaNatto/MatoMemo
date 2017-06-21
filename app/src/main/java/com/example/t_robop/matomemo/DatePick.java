package com.example.t_robop.matomemo;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class DatePick extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    int year;
    int month;
    int day;

    public DatePick(int year,int month,int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

            @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Calendar c = Calendar.getInstance();
//                int year = c.get(day);
//                int month = c.get(Calendar.MONTH);
//                int day = c.get(Calendar.DAY_OF_MONTH);


                return new DatePickerDialog(getActivity(), (FolderCreateActivity)getActivity(),year, month, day);
            }

            @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            }

        }