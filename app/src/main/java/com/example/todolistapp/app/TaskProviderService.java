package com.example.todolistapp.app;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SimpleAdapter;

import java.util.ArrayList;

public class TaskProviderService extends Service
{
    /* ===================== static fields ===================== */
    private static final String DB_NAME = "todoListApp_DB";
    private static final String TABLE_NAME = "todoList_Table";

    /* ===================== service methods ===================== */
    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    public void onCreate()
    {
        super.onCreate();
        binder = new MyBinder();

        myDBHelper = new DBHelper( getApplicationContext() );
        myDataBase = myDBHelper.getWritableDatabase();

        // get indexes of columns
        Cursor c = myDataBase.query( TABLE_NAME, null, null, null, null, null, null );
        myIdColIndex = c.getColumnIndex( Task.LABEL_ID );
        myNameColIndex = c.getColumnIndex( Task.LABEL_NAME );
        myDescriptionColIndex = c.getColumnIndex( Task.LABEL_DESCRIPTION );
        myPriorityColIndex = c.getColumnIndex( Task.LABEL_PRIORITY );
        myIsDoneColIndex = c.getColumnIndex( Task.LABEL_IS_DONE );
        c.close();

        myData = getTasks();

        String[] from = new String[] { Task.LABEL_NAME, Task.LABEL_DESCRIPTION };
        int[] to = new int[] { R.id.tvItemTaskName, R.id.tvItemTaskDesc };

        myAdapter = new SimpleAdapter( this, myData, R.layout.task_item_list, from, to );
    }

    public void onDestroy()
    {
        super.onDestroy();
        myDBHelper.close();
    }
    /* ===================== methods for work with database ===================== */
    private long insert( Task theTask )
    {
        ContentValues aCV = new ContentValues();
        if( theTask.getId() != Task.INCORRECT_ID )
        {
            aCV.put( Task.LABEL_ID, theTask.getId() );
        }
        aCV.put( Task.LABEL_NAME, theTask.getName() );
        aCV.put( Task.LABEL_DESCRIPTION, theTask.getDescription() );
        aCV.put( Task.LABEL_PRIORITY, theTask.getPriority().ordinal() );
        aCV.put( Task.LABEL_IS_DONE, theTask.isDone() ? 1 : 0 );

        return myDataBase.insert( TABLE_NAME, null, aCV );
    }

    private int update( Task theTask )
    {
        ContentValues aCV = new ContentValues();
        aCV.put( Task.LABEL_NAME, theTask.getName() );
        aCV.put( Task.LABEL_DESCRIPTION, theTask.getDescription() );
        aCV.put( Task.LABEL_PRIORITY, theTask.getPriority().ordinal() );
        aCV.put( Task.LABEL_IS_DONE, theTask.isDone() ? 1 : 0 );

        String aSelection = Task.LABEL_ID + " = ?";
        String[]aSelectionArgs = new String[] { String.valueOf( theTask.getId() ) };

        return myDataBase.update(TABLE_NAME, aCV, aSelection, aSelectionArgs);
    }
    private ArrayList<Task> getTasks()
    {
        ArrayList<Task> res = new ArrayList<Task>();
        Cursor aCursor = myDataBase.query( TABLE_NAME, null, null, null, null, null, null );
        if ( aCursor.moveToFirst() )
        {
            do
            {
                res.add( readTask( aCursor ) );
            }
            while ( aCursor.moveToNext() );
        }
        aCursor.close();
        return res;
    }

    private int removeTasks()
    {
        return myDataBase.delete(TABLE_NAME, null, null);
    }

    private ArrayList<Task> getTasks( String theColumn, String[] theValues)
    {
        ArrayList<Task> res = new ArrayList<Task>();
        String aSelection = theColumn + " = ?";
        Cursor aCursor = myDataBase.query( TABLE_NAME, null, aSelection, theValues, null, null, null );
        if ( aCursor.moveToFirst() )
        {
            do
            {
                res.add( readTask( aCursor ) );
            }
            while ( aCursor.moveToNext() );
        }
        aCursor.close();
        return res;
    }

    private int removeTasks( String theColumn, String[] theValues)
    {
        String aSelection = theColumn + " = ?";
        return myDataBase.delete( TABLE_NAME, aSelection, theValues );
    }

    private int getNmbTasks( String theColumn, String[] theValues)
    {
        String aSelection = theColumn + " = ?";
        Cursor aCursor = myDataBase.query(TABLE_NAME, null, aSelection, theValues, null, null, null);
        int res = aCursor.getCount();
        aCursor.close();
        return res;
    }

    private int removeTaskById( long theId )
    {
        return removeTasks(Task.LABEL_ID, new String[]{String.valueOf(theId)});
    }

    private int removeTask( Task theTask )
    {
        return removeTaskById(theTask.getId());
    }

    /* ===================== methods for work with activities ===================== */
    public boolean AddNewTask( Task theTask )
    {
        // update database
        long anId = insert( theTask );
        theTask.setId( anId );

        // update view
        myData.add( theTask );
        myAdapter.notifyDataSetChanged();

        return true;
    }

    public boolean UpdateTask( int position, Task theTask )
    {
        // update database
        theTask.setId( (Long)myData.get( position).get(Task.LABEL_ID) );
        update( theTask );

        // update view
        myData.set( position, theTask );
        myAdapter.notifyDataSetChanged();

        return true;
    }

    public boolean RemoveTaskOnPosition(int position)
    {
        // update view
        long id = (Long) myData.remove( position ).get(Task.LABEL_ID);
        myAdapter.notifyDataSetChanged();

        // remove from database
        removeTaskById( id );

        return true;
    }

    public boolean RemoveAll()
    {
        if( removeTasks() != 0 )
        {
            myData.clear();
            myAdapter.notifyDataSetChanged();
        }
        return true;
    }

    public Task GetTaskById( long theId )
    {
        ArrayList<Task> ret = getTasks( Task.LABEL_ID, new String[] { String.valueOf( theId ) } );
        if( ret == null || ret.isEmpty() )
            return Task.getInvalid();

        return ret.get( 0 );
    }

    public Task GetTaskByPosition( int position )
    {
        return myData.get( position );
    }

    public int GetNmbTaskWithName(String theName)
    {
        return getNmbTasks(Task.LABEL_NAME, new String[]{theName});
    }

    /* ===================== getters and setters ===================== */
    SimpleAdapter getAdapter()
    {
        return myAdapter;
    }

    /* ===================== internal private methods ===================== */
    private Task readTask( Cursor theCursor )
    {
        return  new Task( theCursor.getLong( myIdColIndex ),
                          theCursor.getString( myNameColIndex ),
                          theCursor.getString( myDescriptionColIndex ),
                          theCursor.getInt( myPriorityColIndex ),
                          theCursor.getInt( myIsDoneColIndex ) == 1 );
    }
    /* ===================== private fields ===================== */
    MyBinder binder;

    // database fields
    private DBHelper myDBHelper;
    private SQLiteDatabase myDataBase;

    // column indexes
    private int myIdColIndex;
    private int myNameColIndex;
    private int myDescriptionColIndex;
    private int myPriorityColIndex;
    private int myIsDoneColIndex;

    // data for adapter
    private ArrayList<Task> myData;
    private SimpleAdapter myAdapter;

    /* ===================== internal classes ===================== */
    class MyBinder extends Binder {
        TaskProviderService getService() {
            return TaskProviderService.this;
        }
    }

    class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper( Context theContext )
        {
            super( theContext, DB_NAME, null, 1 );
        }

        public void onCreate( SQLiteDatabase db )
        {
            String anInitStr = "CREATE TABLE " + TABLE_NAME + " ("
                    + Task.LABEL_ID + " integer primary key autoincrement,"
                    + Task.LABEL_NAME + " text,"
                    + Task.LABEL_DESCRIPTION + " text,"
                    + Task.LABEL_PRIORITY + " integer,"
                    + Task.LABEL_IS_DONE + " integer"
                    + ");";

            db.execSQL( anInitStr );
        }

        public void onUpgrade(SQLiteDatabase db, int thOldVersion, int theNewVersion)
        {
            // todo
        }
    }
}
