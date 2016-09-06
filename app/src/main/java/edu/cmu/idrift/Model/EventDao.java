package edu.cmu.idrift.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sophiejeong on 4/2/14.
 */
public class EventDao {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_name, SQLiteHelper.COLUMN_dateTime, SQLiteHelper.COLUMN_location };



    public EventDao(Context context) {
        dbHelper = new SQLiteHelper (context, SQLiteHelper.DATABASE_NAME, null, SQLiteHelper.DATABASE_VERSION);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertEvent(String name,  String dateTime, String location)
    {
        ContentValues newEvent = new ContentValues();
        newEvent.put("name", name);
        newEvent.put("dateTime", dateTime);
        newEvent.put("location", location);

        open();
        database.insert(SQLiteHelper.TABLE_Event, null, newEvent);
        close();
    }


    public void updateEvent(long id, String name,  String dateTime, String location)
    {
        ContentValues editEvent = new ContentValues();
        editEvent.put("name", name);
        editEvent.put("dateTime", dateTime);
        editEvent.put("location", location);

        open();
        database.update(SQLiteHelper.TABLE_Event, editEvent, "_id=" + id, null);
        close();
    }

    public Cursor getAllEvents()
    {
        return database.query(SQLiteHelper.TABLE_Event, new String[] {"_id", "name"}, null, null, null, null, "name");
    }

    public List<Event> getAllEventLists() {
        List<Event> events = new ArrayList<Event>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_Event,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Event Event = cursorToEvent(cursor);
            events.add(Event);
            cursor.moveToNext();
        } // make sure to close the cursor
        cursor.close();
        return events;
    }
    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();

        event.setName(cursor.getString(1));
        return event;
    }

    public Cursor getOneEvent(long id)
    {
        return database.query(SQLiteHelper.TABLE_Event, null, "_id=" + id, null, null, null, null);
    }

    public void deleteEvent(long id)
    {
        open();
        database.delete(SQLiteHelper.TABLE_Event, "_id=" + id, null);
        close();
    }
}
