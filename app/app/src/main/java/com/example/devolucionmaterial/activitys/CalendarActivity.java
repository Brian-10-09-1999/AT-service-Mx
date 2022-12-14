
package com.example.devolucionmaterial.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.calendar.CustomCalendar;
import com.example.calendar.dao.EventData;
import com.example.calendar.dao.dataAboutDate;
import com.example.calendar.utils.CalendarUtils;
import com.example.devolucionmaterial.R;

import java.util.ArrayList;
import java.util.Random;

public class CalendarActivity extends AppCompatActivity {
    private CustomCalendar customCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        customCalendar = (CustomCalendar) findViewById(R.id.customCalendar);

        String[] arr = {"2016-06-10", "2016-06-11", "2016-06-15", "2016-06-16", "2016-12-29"};
        for (int i = 0; i < 5; i++) {
            int eventCount = 10;
            customCalendar.addAnEvent(arr[i], eventCount, getEventDataList(eventCount));
        }
    }


    public ArrayList<EventData> getEventDataList(int count) {
        ArrayList<EventData> eventDataList = new ArrayList();

        for (int i = 0; i < count; i++) {
            EventData dateData = new EventData();
            ArrayList<dataAboutDate> dataAboutDates = new ArrayList();

            dateData.setSection(CalendarUtils.getNAMES()[new Random().nextInt(CalendarUtils.getNAMES().length)]);
            dataAboutDate dataAboutDate = new dataAboutDate();

            int index = new Random().nextInt(CalendarUtils.getEVENTS().length);

            dataAboutDate.setTitle(CalendarUtils.getEVENTS()[index]);
            dataAboutDate.setSubject(CalendarUtils.getEventsDescription()[index]);
            dataAboutDates.add(dataAboutDate);

            dateData.setData(dataAboutDates);
            eventDataList.add(dateData);
        }

        return eventDataList;
    }
}
