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
import java.util.Map;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_TASK_DETAIL = 1;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ContentProvider.initialize( this );
        myContentProvider = ContentProvider.getInstance();

        // get Views
        Button aBtnNewTask = (Button)findViewById( R.id.btnNewTask );
        ListView aLVTaskList = (ListView)findViewById( R.id.lvTaskList );

        // fill task list
        String[] from = new String[] { TaskData.LABEL_NAME, TaskData.LABEL_DESCRIPTION };
        int[] to = new int[] { R.id.tvItemTaskName, R.id.tvItemTaskDesc };

        myData = TaskData.toTaskInfoList(myContentProvider.getTasks());
        myAdapter = new SimpleAdapter( this, myData, R.layout.task_item_list, from, to );
        aLVTaskList.setAdapter( myAdapter );

        // register events
        aBtnNewTask.setOnClickListener( this );
        registerForContextMenu(aLVTaskList);

    }

    protected void onDestroy()
    {
        super.onDestroy();
        myContentProvider.close();
    }

    protected void onActivityResult(int theRequestCode, int theResultCode, Intent theData)
    {
        if ( theRequestCode == REQUEST_CODE_TASK_DETAIL && theResultCode == TaskDetailActivity.RESULT_NEW )
        {
            TaskData aTask = theData.getParcelableExtra( TaskData.class.getCanonicalName() );
            myData.add( new TaskData.Info( aTask ) );
            myAdapter.notifyDataSetChanged();
        }
        else if ( theRequestCode == REQUEST_CODE_TASK_DETAIL && theResultCode == TaskDetailActivity.RESULT_EDITED )
        {
            TaskData aTask = theData.getParcelableExtra( TaskData.class.getCanonicalName() );
            if( !TaskData.isValid( aTask ) )
            {
                // todo throw exception
                return;
            }

            TaskData.Info anInfo = new TaskData.Info( aTask );
            int idx = myData.indexOf( anInfo );
            if( idx != -1 )
            {
                myData.set( idx, anInfo );
            }
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
            Intent anIntent = new Intent( this, TaskDetailActivity.class );
            anIntent.putExtra( TaskData.class.getCanonicalName(), new TaskData() );
            startActivityForResult ( anIntent, REQUEST_CODE_TASK_DETAIL );
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
        TaskData aTask =  new TaskData( myData.get( info.position ) );
        switch( item.getItemId() ) {
            case R.id.cm_item_list_set_executed:
            {
                // add stuff here

                Toast.makeText( this, "position: " + info.position + " " + info.id, Toast.LENGTH_SHORT ).show();
                return true;
            }
            case R.id.cm_item_list_edit:
            {
                Intent anIntent = new Intent( this, TaskDetailActivity.class );
                anIntent.putExtra( TaskData.class.getCanonicalName(), aTask );
                startActivityForResult( anIntent, REQUEST_CODE_TASK_DETAIL );
                return true;
            }
            case R.id.cm_item_list_delete:
            {
                myContentProvider.removeTask( aTask );
                myData.remove( info.position );
                myAdapter.notifyDataSetChanged();
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    // private fields:
    private ContentProvider myContentProvider;
    private SimpleAdapter myAdapter;
    private ArrayList<TaskData.Info> myData;
}
