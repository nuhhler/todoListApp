package com.example.todolistapp.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdv on 11.06.14.
 */
public class ContentProvider
{
    public static final String DB_NAME = "todoListApp_DB";
    public static final String TABLE_NAME = "todoList_Table";

    private static ContentProvider ourInstance = null ;

    public static ContentProvider getInstance() {
        return ourInstance;
    }

    private ContentProvider( Context theContext )
    {
        myDBHelper = new DBHelper( theContext );
        myDataBase = myDBHelper.getWritableDatabase();

        // get indexes of columns
        Cursor c = myDataBase.query( TABLE_NAME, null, null, null, null, null, null );
        myIdColIndex = c.getColumnIndex( TaskData.LABEL_ID);
        myNameColIndex = c.getColumnIndex( TaskData.LABEL_NAME);
        myDescriptionColIndex = c.getColumnIndex( TaskData.LABEL_DESCRIPTION);
        myPriorityColIndex = c.getColumnIndex( TaskData.LABEL_PRIORITY);
        myIsDoneColIndex = c.getColumnIndex( TaskData.LABEL_IS_DONE);
        c.close();

    }

    public static void initialize( Context theContext )
    {
        if( ourInstance != null )
            return;

        ourInstance = new ContentProvider( theContext );
    }

    public void close()
    {
        myDBHelper.close();
    }

    public long insert( TaskData theTask )
    {
        ContentValues aCV = new ContentValues();
        if( theTask.getId() != TaskData.INCORRECT_ID )
        {
            aCV.put(TaskData.LABEL_ID, theTask.getId());
        }
        aCV.put(TaskData.LABEL_NAME, theTask.getName());
        aCV.put(TaskData.LABEL_DESCRIPTION, theTask.getDescription());
        aCV.put(TaskData.LABEL_PRIORITY, theTask.getPriority());
        aCV.put(TaskData.LABEL_IS_DONE, theTask.isDone() ? 1 : 0);

        return myDataBase.insert( TABLE_NAME, null, aCV );
    }

    public int update( TaskData theTask )
    {
        ContentValues aCV = new ContentValues();
        aCV.put( TaskData.LABEL_NAME, theTask.getName() );
        aCV.put( TaskData.LABEL_DESCRIPTION, theTask.getDescription() );
        aCV.put( TaskData.LABEL_PRIORITY, theTask.getPriority() );
        aCV.put( TaskData.LABEL_IS_DONE, theTask.isDone() ? 1 : 0 );

        String aSelection = TaskData.LABEL_ID + " = ?";
        String[]aSelectionArgs = new String[] { String.valueOf( theTask.getId() ) };

        return myDataBase.update(TABLE_NAME, aCV, aSelection, aSelectionArgs);
    }

    public ArrayList<TaskData> getTasks()
    {
        ArrayList<TaskData> res = new ArrayList<TaskData>();
        Cursor c = myDataBase.query( TABLE_NAME, null, null, null, null, null, null );
        if ( c.moveToFirst() )
        {
            do {
                TaskData aData = new TaskData( c.getLong( myIdColIndex ),
                                               c.getString( myNameColIndex ),
                                               c.getString( myDescriptionColIndex ),
                                               c.getInt( myPriorityColIndex ),
                                               c.getInt( myIsDoneColIndex ) == 1 ? true : false );
                res.add( aData );
            } while ( c.moveToNext() );
        }
        c.close();
        return res;
    }

    public int getNmbTasks( String theParamName, String theValue )
    {
        int res = 0;
        String aSelection =theParamName + " = ?";
        String[]aSelectionArgs = new String[] { theValue };
        Cursor c = myDataBase.query( TABLE_NAME, null, aSelection, aSelectionArgs, null, null, null );
        res = c.getCount();
        c.close();
        return res;
    }

    public TaskData getTask( long theId )
    {
        TaskData aData = null;
        String aSelection = TaskData.LABEL_ID + " = ?";
        String[]aSelectionArgs = new String[] { String.valueOf( theId ) };
        Cursor c = myDataBase.query( TABLE_NAME, null, aSelection, aSelectionArgs, null, null, null );
        if ( c.moveToFirst() )
        {
            do {
                aData = new TaskData( c.getLong( myIdColIndex ),
                                      c.getString( myNameColIndex ),
                                      c.getString( myDescriptionColIndex ),
                                      c.getInt( myPriorityColIndex ),
                                      c.getInt( myIsDoneColIndex ) == 1 ? true : false );
            } while ( c.moveToNext() );
        }
        c.close();
        return aData;
    }

    public int removeTasks()
    {
        return myDataBase.delete( TABLE_NAME, null, null );
    }

    public int removeTask( long theId )
    {
        String aSelection = TaskData.LABEL_ID + " = ?";
        String[]aSelectionArgs = new String[] { String.valueOf( theId ) };
        return myDataBase.delete( TABLE_NAME, aSelection, aSelectionArgs );
    }

    public int removeTask( TaskData theTask )
    {
        return removeTask( theTask.getId() );
    }


    // internal fields
    private DBHelper myDBHelper;
    private SQLiteDatabase myDataBase;

    // column indexes
    private int myIdColIndex;
    private int myNameColIndex;
    private int myDescriptionColIndex;
    private int myPriorityColIndex;
    private int myIsDoneColIndex;


    // internal classes

    class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper( Context theContext )
        {
            super( theContext, DB_NAME, null, 1 );
        }

        public void onCreate( SQLiteDatabase db )
        {
            String anInitStr = "CREATE TABLE " + TABLE_NAME + " ("
                    + TaskData.LABEL_ID + " integer primary key autoincrement,"
                    + TaskData.LABEL_NAME + " text,"
                    + TaskData.LABEL_DESCRIPTION + " text,"
                    + TaskData.LABEL_PRIORITY + " integer,"
                    + TaskData.LABEL_IS_DONE + " integer"
                    + ");";

            db.execSQL( anInitStr );
        }

        public void onUpgrade(SQLiteDatabase db, int thOldVersion, int theNewVersion)
        {
            // todo
        }
    }
}
