package com.example.todolistapp.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sdv on 11.06.14.
 */
public class Task extends HashMap<String, Object> implements Parcelable
{
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
    static Task getInvalid()
    {
        return new Task();
    }

    private Task()
    {
        super();
    }

    public Task(String theName, String theDescription, Priority thePriority)
    {
        this();
        setId( INCORRECT_ID );
        setDone( false );
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
        return (String) get(LABEL_NAME);
    }

    public void setName( String name ) {
        super.put(LABEL_NAME, name );
    }

    public Priority getPriority()
    {
        return (Priority) get(LABEL_PRIORITY);
    }

    public void setPriority( Priority priority )
    {
        super.put( LABEL_PRIORITY, priority );
    }

    public void setPriority( int priority ) {
        setPriority( Priority.valueOf( priority ) );
    }

    public boolean isDone() {
        return (Boolean) get(LABEL_IS_DONE);
    }

    public void setDone( boolean isDone ) {
        super.put( LABEL_IS_DONE, isDone );
    }

    public String getDescription() {
        return (String) get(LABEL_DESCRIPTION);
    }

    public void setDescription( String description ) {
        super.put( LABEL_DESCRIPTION, description);
    }

    public long getId() {
        return (Long) get(LABEL_ID);
    }

    public void setId( long id ) {
        super.put(LABEL_ID, id);
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
        else
            return false;
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public static boolean isValid( Task theTask )
    {
        return theTask != null && !theTask.isEmpty() && theTask.getId() != INCORRECT_ID;
    }

    /* ===================== hide methods ===================== */
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("not supported");
    }

    public void putAll( HashMap<String, Object> map) {
        throw new UnsupportedOperationException("not supported");
    }

    public Object remove(java.lang.Object key) {
        throw new UnsupportedOperationException("not supported");
    }

    public void clear() {
        throw new UnsupportedOperationException("not supported");
    }

}
