package com.example.todolistapp.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by sdv on 11.06.14.
 */
public class ContentProvider
{
    private static final String DB_NAME = "todoListApp_DB";
    private static final String TABLE_NAME = "todoList_Table";

    private static ContentProvider ourInstance = null ;

    /* ===================== access and initialize methods ===================== */
    public static ContentProvider getInstance() {
        return ourInstance;
    }

    private ContentProvider( Context theContext )
    {
        myDBHelper = new DBHelper( theContext );
        myDataBase = myDBHelper.getWritableDatabase();

        // get indexes of columns
        Cursor c = myDataBase.query( TABLE_NAME, null, null, null, null, null, null );
        myIdColIndex = c.getColumnIndex( Task.LABEL_ID );
        myNameColIndex = c.getColumnIndex( Task.LABEL_NAME );
        myDescriptionColIndex = c.getColumnIndex( Task.LABEL_DESCRIPTION );
        myPriorityColIndex = c.getColumnIndex( Task.LABEL_PRIORITY );
        myIsDoneColIndex = c.getColumnIndex( Task.LABEL_IS_DONE );
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

    /* ===================== methods for work with database ===================== */
    public long insert( Task theTask )
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

    public int update( Task theTask )
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

    public ArrayList<Task> getTasks()
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

    public int getNmbTasksWithName( String theName )
    {
        return getNmbTasks(Task.LABEL_NAME, new String[]{theName});
    }

    public int removeTasks()
    {
        return myDataBase.delete(TABLE_NAME, null, null);
    }

    public int removeTaskById( long theId )
    {
        return removeTasks(Task.LABEL_ID, new String[]{String.valueOf(theId)});
    }

    public int removeTask( Task theTask )
    {
        return removeTaskById(theTask.getId());
    }

    /* ===================== internal protected methods ===================== */
    protected ArrayList<Task> getTasks( String theColumn, String[] theValues)
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

    protected int removeTasks( String theColumn, String[] theValues)
    {
        String aSelection = theColumn + " = ?";
        return myDataBase.delete( TABLE_NAME, aSelection, theValues );
    }

    protected int getNmbTasks( String theColumn, String[] theValues)
    {
        String aSelection = theColumn + " = ?";
        Cursor aCursor = myDataBase.query(TABLE_NAME, null, aSelection, theValues, null, null, null);
        int res = aCursor.getCount();
        aCursor.close();
        return res;
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
    private final DBHelper myDBHelper;
    private final SQLiteDatabase myDataBase;

    // column indexes
    private final int myIdColIndex;
    private final int myNameColIndex;
    private final int myDescriptionColIndex;
    private final int myPriorityColIndex;
    private final int myIsDoneColIndex;

    /* ===================== internal classes ===================== */
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
