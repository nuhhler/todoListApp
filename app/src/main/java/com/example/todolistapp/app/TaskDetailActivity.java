package com.example.todolistapp.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


public class TaskDetailActivity extends ActionBarActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    public static final int RESULT_NEW = 100;
    public static final int RESULT_EDITED = 101;

    public static final int DIALOG_CONFIRM = 1;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_task_detail);

        myContentProvider = ContentProvider.getInstance();

        // find Views
        etTaskName = (EditText)findViewById(R.id.etTaskName);
        etTaskDescription = (EditText)findViewById(R.id.etTaskDescription);
        btnOk = (Button)findViewById(R.id.btnOk);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        rbgPriority = (RadioGroup)findViewById(R.id.rbgPriority);

        // set listeners
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        rbgPriority.setOnClickListener( this );

        // restore values if the user edits existing task
        Intent anIntent = getIntent();
        myTaskId = anIntent.getLongExtra( TaskData.LABEL_ID, TaskData.INCORRECT_ID );
        if( myTaskId != TaskData.INCORRECT_ID )
        {
            TaskData aTask = myContentProvider.getTask( myTaskId );
            etTaskName.setText(aTask.getName());
            etTaskDescription.setText( aTask.getDescription() );
            switch ( aTask.getPriority() )
            {
                case TaskData.PRIORITY_LOW:
                    rbgPriority.check(R.id.rbtnLowPriority);
                    break;
                case TaskData.PRIORITY_NORMAL:
                    rbgPriority.check( R.id.rbtnNormalPriority );
                    break;
                case TaskData.PRIORITY_HIGH:
                    rbgPriority.check(R.id.rbtnHighPriority );
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick( View theView )
    {
        switch ( theView.getId() ) {
            case R.id.btnOk :
            {
                String aName = etTaskName.getText().toString();
                if ( aName.isEmpty() )
                {
                    Toast.makeText( this, R.string.emptyTaskNameText, Toast.LENGTH_SHORT );
                    return;
                }

                int nmb = myContentProvider.getNmbTasks( TaskData.LABEL_NAME, aName );
                if( nmb == 0 ||
                    nmb == 1 && isEditMode() && myContentProvider.getTask( myTaskId ).getName().equals( aName) )
                {
                    executeOperation();
                }
                else
                {
                    showDialog(DIALOG_CONFIRM);
                }
                break;
            }
            case R.id.btnCancel:
            {
                setResult( RESULT_CANCELED, new Intent() );
                finish();
                break;
            }
        }
    }

    public void onClick( DialogInterface dialog, int which ) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
            {
                executeOperation();
                break;
            }
            case Dialog.BUTTON_NEGATIVE:
            {
                //finish();
                break;
            }
        }
    }

    protected Dialog onCreateDialog( int theId ) {
        if( theId == DIALOG_CONFIRM )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder( this );

            builder.setTitle( R.string.dlg_title );
            builder.setMessage( R.string.dlg_text_confirm );
            builder.setIcon( android.R.drawable.ic_dialog_info );

            builder.setPositiveButton( R.string.btnOkText, this );
            builder.setNegativeButton( R.string.btnCancelText, this );

            return builder.create();
        }
        return super.onCreateDialog( theId );
    }

    // internal methods
    private int getPriority()
    {
        int res = -1;
        switch ( rbgPriority.getCheckedRadioButtonId() )
        {
            case R.id.rbtnLowPriority:
                res = TaskData.PRIORITY_LOW;
                break;
            case R.id.rbtnNormalPriority:
                res = TaskData.PRIORITY_NORMAL;
                break;
            case R.id.rbtnHighPriority:
                res = TaskData.PRIORITY_HIGH;
                break;
        }
        return res;
    }

    private boolean isEditMode()
    {
        return myTaskId != TaskData.INCORRECT_ID;
    }

    private void executeOperation()
    {
        Intent anIntent = new Intent();
        int result;

        // insert new task to database
        TaskData aNewTask = new TaskData( TaskData.INCORRECT_ID,
                etTaskName.getText().toString(),
                etTaskDescription.getText().toString(),
                getPriority(),
                false );

        if( !isEditMode() )
        {
            aNewTask.setId( myContentProvider.insert(aNewTask) );
            myTaskId = aNewTask.getId();
            result = RESULT_NEW;
        }
        else
        {
            aNewTask.setId( myTaskId );
            myContentProvider.update( aNewTask );
            result = RESULT_EDITED;
        }

        // send result
        anIntent.putExtra( TaskData.LABEL_ID, myTaskId );
        setResult( result, anIntent );
        finish();
    }

    // private fields
    private Button btnOk;
    private Button btnCancel;
    private RadioGroup rbgPriority;
    private EditText etTaskName;
    private EditText etTaskDescription;
    private ContentProvider myContentProvider;
    private long myTaskId;

}
