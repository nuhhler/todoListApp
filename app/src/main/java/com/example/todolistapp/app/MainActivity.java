package com.example.todolistapp.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // get Views
        Button aBtnNewTask = (Button)findViewById( R.id.btnNewTask );
        ListView aLVTaskList = (ListView)findViewById( R.id.lvTaskList );

        myIntent = new Intent( this, TaskProviderService.class );
        myConnection = new ServiceConnection()
        {
            public void onServiceConnected( ComponentName name, IBinder binder )
            {
                ListView aLVTaskList = (ListView)findViewById( R.id.lvTaskList );
                myTaskProvider = ((TaskProviderService.MyBinder) binder).getService();
                aLVTaskList.setAdapter(myTaskProvider.getAdapter());
            }

            public void onServiceDisconnected(ComponentName name)
            {
                // todo process the error
            }
        };
        startService( myIntent );

        // register events
        aBtnNewTask.setOnClickListener( this );
        registerForContextMenu( aLVTaskList );
    }

    protected void onDestroy()
    {
        super.onDestroy();
        myTaskProvider.stopSelf();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        bindService( myIntent, myConnection, 0 );
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unbindService( myConnection );
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
        if ( id == R.id.action_removeAll )
        {
            return myTaskProvider.RemoveAll();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick( View theView )
    {
        if( theView.getId() == R.id.btnNewTask )
        {
            Intent anIntent = new Intent( this, TaskDetailActivity.class );
            anIntent.putExtra( "POSITION", -1 ); // todo remove magic nambers
            startActivity( anIntent );
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
                anIntent.putExtra( "POSITION", info.position ); // todo remove magic nambers
                startActivity( anIntent );
                return true;
            }
            case R.id.cm_item_list_delete:
            {
                return myTaskProvider.RemoveTaskOnPosition(info.position);
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    /* ===================== private fields ===================== */
    private ServiceConnection myConnection;
    Intent myIntent;
    private TaskProviderService myTaskProvider;
}
