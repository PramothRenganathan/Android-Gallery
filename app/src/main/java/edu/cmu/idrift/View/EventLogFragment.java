package edu.cmu.idrift.View;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import android.support.v4.app.Fragment;

import edu.cmu.idrift.Model.EventDao;
import edu.cmu.idrift.R;
import edu.cmu.idrift.ViewActivity.ViewEventActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventLogFragment extends Fragment implements AbsListView.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static final String ROW_ID = "row_id";
    private ListView eventListView;
    private CursorAdapter eventAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment EventLogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventLogFragment newInstance() {
        EventLogFragment fragment = new EventLogFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EventLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // TODO: Change Adapter to display your content


        //new GetEventsTask().execute((Object[]) null);
    }

     public void onResume()
    {
        super.onResume();
        new GetEventsTask().execute((Object[]) null);
    }

    @Override
     public void onStop()
    {
        Cursor cursor = eventAdapter.getCursor();
        if (cursor != null) cursor.deactivate();
        eventAdapter.changeCursor(null);

        super.onStop();
    }
    private class GetEventsTask extends AsyncTask<Object, Object, Cursor>
    {

        EventDao dbConnector = new EventDao(getContext());
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        String[] from = new String[] { "name"};
        int[] to = new int[] { R.id.eventTextView};
        eventAdapter = new SimpleCursorAdapter(view.getContext(), R.layout.activity_event_list, null, from, to,0);
        //setListAdapter(eventAdapter);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(eventAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);


        return view;
        //return inflater.inflate(R.layout.fragment_event_log, container, false);
    }
/*
    AdapterView.OnItemClickListener viewEventListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
        {

            Intent viewEvent = new Intent(getActivity(), ViewEventActivity.class);
            viewEvent.putExtra(ROW_ID, arg3);
            Log.d("adapterViewOnItem", String.valueOf(arg3));
            startActivity(viewEvent);
        }
    };
    */
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String id) {
        if (mListener != null) {
            mListener.onFragmentInteraction(id);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    //public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       // if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

            //mListener.onFragmentInteraction(eventList.get(position));
       // }
    //}

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3){


        Intent viewEvent = new Intent(getActivity(), ViewEventActivity.class);
        viewEvent.putExtra(ROW_ID, arg3);
        Log.d("EventListOnClick", String.valueOf(arg3));
        startActivity(viewEvent);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

}
