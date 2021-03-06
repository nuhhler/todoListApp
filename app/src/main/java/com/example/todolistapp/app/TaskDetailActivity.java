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

 /* todo
  * it would be good to implement listeners for editable View for identifying fields which was really changed
  * it allow to edit existed task more effective
  */

public class TaskDetailActivity extends ActionBarActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    public static final int RESULT_NEW = 100;
    public static final int RESULT_EDITED = 101;

    private static final int DIALOG_CONFIRM = 1;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_task_detail );

        myTaskProvider = TaskProvider.getInstance();

        // get Views
        etTaskName = (EditText)findViewById(R.id.etTaskName);
        etTaskDescription = (EditText)findViewById(R.id.etTaskDescription);
        Button aBtnOk = (Button)findViewById(R.id.btnOk);
        Button aBtnCancel = (Button)findViewById(R.id.btnCancel);
        rbgPriority = (RadioGroup)findViewById(R.id.rbgPriority);

        // set listeners
        aBtnOk.setOnClickListener(this);
        aBtnCancel.setOnClickListener(this);
        rbgPriority.setOnClickListener( this );

        // restore values if user edits the existing task
        myTask = getIntent().getParcelableExtra(Task.class.getCanonicalName());
        if( Task.isValid(myTask) )
        {
            etTaskName.setText( myTask.getName() );
            etTaskDescription.setText( myTask.getDescription() );
            switch ( myTask.getPriority() )
            {
                case LOW:
                    rbgPriority.check( R.id.rbtnLowPriority );
                    break;
                case NORMAL:
                    rbgPriority.check( R.id.rbtnNormalPriority );
                    break;
                case HIGH:
                    rbgPriority.check( R.id.rbtnHighPriority );
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
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
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

                int nmb = myTaskProvider.getNmbTasksWithName( aName );
                if( nmb == 0 ||
                    nmb == 1 && isEditMode() && myTask.getName().equals( aName ) )
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

            builder.setPositiveButton( android.R.string.ok, this );
            builder.setNegativeButton( android.R.string.cancel, this );

            return builder.create();
        }
        return super.onCreateDialog( theId );
    }

    /* ===================== internal methods ===================== */
    private Task.Priority getPriority()
    {
        Task.Priority res = Task.Priority.INCORRECT;
        switch ( rbgPriority.getCheckedRadioButtonId() )
        {
            case R.id.rbtnLowPriority:
                res = Task.Priority.LOW;
                break;
            case R.id.rbtnNormalPriority:
                res = Task.Priority.NORMAL;
                break;
            case R.id.rbtnHighPriority:
                res = Task.Priority.HIGH;
                break;
        }
        return res;
    }

    private boolean isEditMode()
    {
        return Task.isValid(myTask);
    }

    private void executeOperation()
    {
        Intent anIntent = new Intent();
        int result;


        if ( isEditMode() )
        {
            result = RESULT_EDITED;
            myTask.setName( etTaskName.getText().toString() );
            myTask.setDescription( etTaskDescription.getText().toString() );
            myTask.setPriority( getPriority() );
            myTaskProvider.update( myTask );
        }
        else
        {
            result = RESULT_NEW;
            myTask = new Task( etTaskName.getText().toString(),
                                   etTaskDescription.getText().toString(),
                                   getPriority() );
            long anId = myTaskProvider.insert( myTask );
            myTask.setId( anId );
        }

        // send result
        anIntent.putExtra( Task.class.getCanonicalName(), myTask );
        setResult( result, anIntent );
        myTask = null;
        finish();
    }

    /* ===================== private fields ===================== */
    private RadioGroup rbgPriority;
    private EditText etTaskName;
    private EditText etTaskDescription;
    private TaskProvider myTaskProvider;
    private Task myTask;

}
