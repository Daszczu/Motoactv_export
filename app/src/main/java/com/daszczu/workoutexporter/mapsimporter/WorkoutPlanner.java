package com.daszczu.workoutexporter.mapsimporter;

import com.daszczu.workoutexporter.managers.DatabaseManager;

import java.util.Date;

public class WorkoutPlanner {
    int dayConst = 2457950; //2017-07-15
    private DatabaseManager dbClient;

    public WorkoutPlanner(DatabaseManager dbClient) {
        this.dbClient = dbClient;
    }

    public void createEvent(Date date, String name) {
        long eventId = dbClient.saveEvent(name, date);


        String insertEventSql = "INSERT INTO EVENTS " +
        "(_ID, _SYNC_DIRTY, CALENDAR_ID, TITLE, EVENTLOCATION, DESCRIPTION, DTSTART, DTEND, EVENTTIMEZONE, DURATION, RRULE, LASTDATE, ORGANIZER)";
    }
}
