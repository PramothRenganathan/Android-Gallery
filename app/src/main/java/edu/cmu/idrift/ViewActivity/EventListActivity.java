package edu.cmu.idrift.ViewActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.cmu.idrift.Model.EventDao;
import edu.cmu.idrift.R;
import android.util.Log;

public class EventListActivity extends ListActivity {

    public static final String ROW_ID = "row_id";
    private ListView eventListView;
    private CursorAdapter eventAdapter;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // get action bar
        ActionBar actionBar = getActionBar();

        // Enabling Up / Back navigation
       // actionBar.setDisplayHomeAsUpEnabled(true);



        eventListView = getListView();
        eventListView.setOnItemClickListener(viewEventListener);

        String[] from = new String[] { "name"};
        int[] to = new int[] { R.id.eventTextView};
        eventAdapter = new SimpleCursorAdapter(EventListActivity.this, R.layout.activity_event_list, null, from, to,0);
        setListAdapter(eventAdapter);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        new GetEventsTask().execute((Object[]) null);
    }

    @Override
    protected void onStop()
    {
        Cursor cursor = eventAdapter.getCursor();
        if (cursor != null) cursor.deactivate();
        eventAdapter.changeCursor(null);

        super.onStop();
    }

    private class GetEventsTask extends AsyncTask<Object, Object, Cursor>
    {

        EventDao dbConnector = new EventDao(EventListActivity.this);
        @Override
        protected Cursor doInBackground(Object... params)
        {
            dbConnector.open();
            return dbConnector.getAllEvents();
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            eventAdapter.changeCursor(result);
            dbConnector.close();
        }
    }

    AdapterView.OnItemClickListener viewEventListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
        {

            Intent viewEvent = new Intent(EventListActivity.this, ViewEventActivity.class);
            viewEvent.putExtra(ROW_ID, arg3);
            Log.d("EventListOnClick", String.valueOf(arg3));
            startActivity(viewEvent);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // 1. Add action item (menu xml should be under menu folder )
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.event_list, menu);
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_list, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();
        //if (id == R.id.action_settings) {
         //   return true;
        //}


        switch (item.getItemId()) {



            case R.id.addEventItem:

                Intent listEvent = new Intent(EventListActivity.this, AddEventActivity.class);
                startActivity(listEvent);

                return true;
            case R.id.listEventItem:

                Intent itemList = new Intent(EventListActivity.this, ViewEventActivity.class);
                startActivity(itemList);

                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
