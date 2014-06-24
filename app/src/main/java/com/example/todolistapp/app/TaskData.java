package com.example.todolistapp.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdv on 11.06.14.
 */
public class TaskData { // todo make parcelable

    public static final String LABEL_ID = "id"; // todo it is necessary to rename to "_id" if you want to use cursor adapter
    public static final String LABEL_NAME = "name";
    public static final String LABEL_DESCRIPTION = "description";
    public static final String LABEL_PRIORITY = "priority";
    public static final String LABEL_IS_DONE = "isDone";

    public static final int INCORRECT_ID = -1;

    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_NORMAL = 2;
    public static final int PRIORITY_HIGH = 3;

    public TaskData( long theId, String theName, String theDescription, int thePriority, boolean theIsDone)
    {
        setId( theId );
        setName( theName );
        setDescription( theDescription );
        setPriority( thePriority );
        setDone( theIsDone );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    private long id;
    private String name;
    private String description;
    private int priority;
    private boolean isDone;
}
