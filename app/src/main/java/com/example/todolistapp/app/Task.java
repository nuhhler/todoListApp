package com.example.todolistapp.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sdv on 11.06.14.
 */
public class Task implements Parcelable {

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
    public Task()
    {
        setId( INCORRECT_ID );
        setPriority( Priority.INCORRECT );
    }

    public Task(String theName, String theDescription, Priority thePriority)
    {
        this();
        setName( theName );
        setDescription( theDescription );
        setPriority( thePriority );
    }

    public Task(long theId, String theName, String theDescription, Priority thePriority, boolean theIsDone)
    {
        this( theName, theDescription, thePriority );
        setId( theId );
        setDone( theIsDone );
    }

    public Task(long theId, String theName, String theDescription, int thePriority, boolean theIsDone)
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
    public Task(Parcel theInput)
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

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>()
    {
        public Task createFromParcel( Parcel theInput )
        {
            return new Task( theInput );
        }

        public Task[] newArray( int theSize )
        {
            return new Task[theSize];
        }
    };

    /* ===================== data exchange ===================== */
    static class Adapter extends HashMap<String, Object> {
        Adapter( Task theTask )
        {
            if ( Task.isValid(theTask) )
            {
                put( LABEL_ID, theTask.getId() );
                put( LABEL_NAME, theTask.getName() );
                put( LABEL_DESCRIPTION, theTask.getDescription() );
                put( LABEL_PRIORITY, theTask.getPriority() );
                put( LABEL_IS_DONE, theTask.isDone() );
            }
            else
            {
                put(LABEL_ID, Task.INCORRECT_ID );
            }
        }

        // @override
        public boolean equals( Object o )
        {
            if ( this == o ) return true;
            if ( o == null ) return false;

            if( getClass() == o.getClass() ) {
                Adapter adapter = (Adapter) o;
                return get(LABEL_ID) == adapter.get(LABEL_ID);
            }
            else if( o instanceof Task)
            {
                Task aData = (Task) o;
                return (Long)get(LABEL_ID) == aData.getId();
            }
            else
                return false;
        }
    }

    Task(Adapter theAdapter)
    {
        this();
        if( Task.isValid(theAdapter) )
        {
            setId( (Long) theAdapter.get(LABEL_ID) );
            setName( (String) theAdapter.get(LABEL_NAME) );
            setDescription( (String) theAdapter.get(LABEL_DESCRIPTION) );
            setPriority( (Priority) theAdapter.get(LABEL_PRIORITY) );
            setDone( (Boolean) theAdapter.get(LABEL_IS_DONE) );
        }
    }

    public static ArrayList<Adapter> toTaskAdapterList(ArrayList<Task> theTasks)
    {
        ArrayList<Adapter> res = new ArrayList<Adapter>();
        for( Task aTask: theTasks )
            res.add( new Adapter( aTask) );
        return res;
    }

    /* ===================== general methods ===================== */

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null ) return false;

        if( getClass() == o.getClass() )
        {
            Task aData = (Task) o;
            return getId() == aData.getId();
        }
        else if( o instanceof Adapter)
        {
            Adapter anAdapter = (Adapter) o;
            return (Long)anAdapter.get(LABEL_ID) == getId();
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public static boolean isValid( Task theTask )
    {
        return theTask != null && theTask.getId() != INCORRECT_ID;
    }

    public static boolean isValid( Adapter theAdapter)
    {
        return theAdapter != null && (Long) theAdapter.get(LABEL_ID) != INCORRECT_ID;
    }

    /* ===================== private fields ===================== */
    private long id;
    private String name;
    private String description;
    private Priority priority;
    private boolean isDone; // todo it would be better to make execution status of the task, make enum
}
