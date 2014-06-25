package com.example.todolistapp.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdv on 11.06.14.
 */
public class TaskData implements Parcelable {

    public static final String LABEL_ID = "id"; // todo it is necessary to rename to "_id" if you want to use cursor adapter
    public static final String LABEL_NAME = "name";
    public static final String LABEL_DESCRIPTION = "description";
    public static final String LABEL_PRIORITY = "priority";
    public static final String LABEL_IS_DONE = "isDone";

    public static final int INCORRECT_ID = -1;

    public enum Priority
    {
        LOW, NORMAL, HIGH, INCORRECT;

        public static Priority valueOf( int theValue )
        {
            if( theValue == LOW.ordinal() )
                return LOW;
            else if( theValue == NORMAL.ordinal() )
                return NORMAL;
            else if( theValue == HIGH.ordinal() )
                return HIGH;
            else
                return INCORRECT;
        }

        public static Priority valueOf( byte theValue )
        {
            return valueOf( theValue );
        }

        public byte toByte()
        {
            return (byte) ordinal();
        }
    }

    /* ===================== constructors ===================== */
    public TaskData()
    {
        setId( INCORRECT_ID );
        setPriority( Priority.INCORRECT );
    }

    public TaskData( String theName, String theDescription, Priority thePriority )
    {
        this();
        setName( theName );
        setDescription( theDescription );
        setPriority( thePriority );
    }

    public TaskData( String theName, String theDescription, int thePriority)
    {
        this( theName, theDescription, Priority.valueOf( thePriority ) );
    }

    public TaskData( long theId, String theName, String theDescription, Priority thePriority, boolean theIsDone )
    {
        this( theName, theDescription, thePriority );
        setId( theId );
        setDone( theIsDone );
    }

    public TaskData( long theId, String theName, String theDescription, int thePriority, boolean theIsDone )
    {
        this( theId, theName, theDescription, Priority.valueOf( thePriority ), theIsDone );
    }

    /* ===================== getters & setters ===================== */
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Priority getPriority()
    {
        return priority;
    }

    public void setPriority( Priority priority ) {
        this.priority = priority;
    }

    public void setPriority( int priority ) {
        setPriority( Priority.valueOf( priority ) );
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone( boolean isDone ) {
        this.isDone = isDone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }


    /* ===================== implement Parcelable ===================== */
    public TaskData( Parcel theInput )
    {
        setId( theInput.readLong() );
        setName( theInput.readString() );
        setDescription( theInput.readString() );
        setPriority( (int) theInput.readByte() );
        setDone( theInput.readByte() == 1 );
    }

    public void writeToParcel( Parcel theOutput, int flags )
    {
        theOutput.writeLong( getId() );
        theOutput.writeString( getName() );
        theOutput.writeString( getDescription() );
        theOutput.writeByte( getPriority().toByte() );
        theOutput.writeByte( (byte) (isDone() ? 1 : 0) );
    }

    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator<TaskData> CREATOR = new Parcelable.Creator<TaskData>()
    {
        public TaskData createFromParcel( Parcel theInput )
        {
            return new TaskData( theInput );
        }

        public TaskData[] newArray( int theSize )
        {
            return new TaskData[theSize];
        }
    };

    /* ===================== data exchange ===================== */
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> aDataMap = new HashMap <String, Object>();
        aDataMap.put( LABEL_ID, getId() );
        aDataMap.put( LABEL_NAME, getName() );
        aDataMap.put( LABEL_DESCRIPTION, getDescription() );
        aDataMap.put( LABEL_PRIORITY, getPriority() );
        aDataMap.put( LABEL_IS_DONE, isDone() );
        return aDataMap;
    }

    public static TaskData valueOf( Map<String, Object> aDataMap )
    {
        return new TaskData( (Long) aDataMap.get( LABEL_ID ),
                             (String) aDataMap.get( LABEL_NAME ),
                             (String) aDataMap.get( LABEL_DESCRIPTION ),
                             (Priority) aDataMap.get( LABEL_PRIORITY ),
                             (Boolean) aDataMap.get( LABEL_IS_DONE ) );
    }

    public static ArrayList<Map<String, Object>> toArrayListOfMap( ArrayList<TaskData> theTasks )
    {
        ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        for( TaskData aTask: theTasks )
        {
            res.add( aTask.toMap() );
        }
        return res;
    }

    /* ===================== general methods ===================== */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskData taskData = (TaskData) o;
        return id == taskData.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    boolean isValid()
    {
        return getId() != INCORRECT_ID;
    }

    public static boolean isValid( TaskData theTask )
    {
        return theTask != null && theTask.isValid();
    }

    public static boolean isValidId( long theId )
    {
        return theId != INCORRECT_ID;
    }


    /* ===================== private fields ===================== */
    private long id;
    private String name;
    private String description;
    private Priority priority;
    private boolean isDone; // todo it would be better to make execution status of the task, make enum
}
