package com.example.todolistapp.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    final int REQUEST_CODE_TASK_DETAIL = 1;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ContentProvider.initialize( this );
        myContentProvider = ContentProvider.getInstance();


        // create internal data
        myTaskDetailIntent = new Intent( this, TaskDetailActivity.class );

        // get Views
        btnNewTask = (Button)findViewById( R.id.btnNewTask );
        lvTaskList = (ListView)findViewById( R.id.lvTaskList );

        // fill task list
        String[] from = new String[] { TaskData.LABEL_NAME, TaskData.LABEL_DESCRIPTION };
        int[] to = new int[] { R.id.tvItemTaskName, R.id.tvItemTaskDesc };

        myData = prepareData( myContentProvider.getTasks() );
        myAdapter = new SimpleAdapter( this, myData, R.layout.task_item_list, from, to );
        lvTaskList.setAdapter( myAdapter );

        // register events
        btnNewTask.setOnClickListener(this);
        registerForContextMenu(lvTaskList);

    }

    protected void onDestroy() {
        super.onDestroy();
        myContentProvider.close();
    }

    protected void onActivityResult(int theRequestCode, int theResultCode, Intent theData)
    {
        if ( theRequestCode == REQUEST_CODE_TASK_DETAIL && theResultCode == TaskDetailActivity.RESULT_NEW )
        {
            long anId = theData.getLongExtra( TaskData.LABEL_ID, TaskData.INCORRECT_ID );
            Map<String, Object> aNewTaskData = myContentProvider.getTask( anId ).toMap();
            myData.add( aNewTaskData );
            myAdapter.notifyDataSetChanged();
        }
        else if ( theRequestCode == REQUEST_CODE_TASK_DETAIL && theResultCode == TaskDetailActivity.RESULT_EDITED )
        {
            // todo can be optimized
            Collections.copy( myData, prepareData( myContentProvider.getTasks() ) );
            myAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if ( id == R.id.action_removeAll ) {
            if( myContentProvider.removeTasks() != 0 )
            {
                myData.clear();
                myAdapter.notifyDataSetChanged();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick( View theView )
    {
        if( theView.getId() == R.id.btnNewTask )
        {
            myTaskDetailIntent.putExtra( TaskData.LABEL_ID, TaskData.INCORRECT_ID );
            startActivityForResult(myTaskDetailIntent, REQUEST_CODE_TASK_DETAIL);
        }
    }

    public void onCreateContextMenu( ContextMenu tneMenu, View theView,
                                     ContextMenu.ContextMenuInfo theMenuInfo )
    {
        super.onCreateContextMenu(tneMenu, theView, theMenuInfo);

        if ( theView.getId() == R.id.lvTaskList ) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.cm_item_list, tneMenu);

//            TextView markExecuted = (TextView)findViewById( R.id.cm_item_list_set_executed);
//            long anId = lvTaskList.getSelectedItemId();
//            if( !myCurrentTask.get((int)anId).isDone() ) {
//                markExecuted.setText( R.string.cm_item_list_set_not_executed_text );
//            }
        }
    }

    public boolean onContextItemSelected( MenuItem item )
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long aTaskId =  (Long) myData.get( info.position ).get( TaskData.LABEL_ID );
        switch(item.getItemId()) {
            case R.id.cm_item_list_set_executed:
                // add stuff here

                Toast.makeText( this, "position: " + info.position + " " + info.id, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.cm_item_list_edit:
                myTaskDetailIntent.putExtra( TaskData.LABEL_ID, aTaskId );
                startActivityForResult( myTaskDetailIntent, REQUEST_CODE_TASK_DETAIL );
                return true;
            case R.id.cm_item_list_delete:
                myContentProvider.removeTask( aTaskId );
                myData.remove( info.position );
                myAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private ArrayList<Map<String, Object>> prepareData( ArrayList<TaskData> theTasks )
    {
        ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        for( TaskData aTask: theTasks )
        {
            res.add( aTask.toMap() );
        }
        return res;
    }

    // private fields:
    private Intent myTaskDetailIntent;
    private Button btnNewTask;
    private ListView lvTaskList;
    private ContentProvider myContentProvider;
    SimpleAdapter myAdapter;
    ArrayList<Map<String, Object>> myData;
}
