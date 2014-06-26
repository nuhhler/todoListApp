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

    public static final String LABEL_ID = "_id";
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
            return valueOf( (int) theValue );
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
    static class Info extends HashMap<String, Object> {
        Info( TaskData theTask )
        {
            if ( TaskData.isValid( theTask ) )
            {
                put( LABEL_ID, theTask.getId() );
                put( LABEL_NAME, theTask.getName() );
                put( LABEL_DESCRIPTION, theTask.getDescription() );
                put( LABEL_PRIORITY, theTask.getPriority() );
                put( LABEL_IS_DONE, theTask.isDone() );
            }
            else
            {
                put(LABEL_ID, TaskData.INCORRECT_ID );
            }
        }

        // @override
        public boolean equals( Object o )
        {
            if ( this == o ) return true;
            if ( o == null ) return false;

            if( getClass() == o.getClass() ) {
                Info info = (Info) o;
                return get(LABEL_ID) == info.get(LABEL_ID);
            }
            else if( o instanceof TaskData )
            {
                TaskData aData = (TaskData) o;
                return (Long)get(LABEL_ID) == aData.getId();
            }
            else
                return false;
        }
    };

    TaskData ( Info theInfo )
    {
        this();
        if( TaskData.isValid( theInfo ) )
        {
            setId( (Long) theInfo.get(LABEL_ID) );
            setName( (String) theInfo.get(LABEL_NAME) );
            setDescription( (String) theInfo.get(LABEL_DESCRIPTION) );
            setPriority( (Priority) theInfo.get(LABEL_PRIORITY) );
            setDone( (Boolean) theInfo.get(LABEL_IS_DONE) );
        }
    }

    public static ArrayList<TaskData.Info> toTaskInfoList( ArrayList<TaskData> theTasks )
    {
        ArrayList<TaskData.Info> res = new ArrayList<TaskData.Info>();
        for( TaskData aTask: theTasks )
            res.add( new Info( aTask) );
        return res;
    }

    /* ===================== general methods ===================== */

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null ) return false;

        if( getClass() == o.getClass() )
        {
            TaskData aData = (TaskData) o;
            return getId() == aData.getId();
        }
        else if( o instanceof Info )
        {
            Info info = (Info) o;
            return (Long)info.get(LABEL_ID) == getId();
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public static boolean isValid( TaskData theTask )
    {
        return theTask != null && theTask.getId() != INCORRECT_ID;
    }

    public static boolean isValid( Info theInfo )
    {
        return theInfo != null && (Long)theInfo.get(LABEL_ID) != INCORRECT_ID;
    }

    /* ===================== private fields ===================== */
    private long id;
    private String name;
    private String description;
    private Priority priority;
    private boolean isDone; // todo it would be better to make execution status of the task, make enum
}
