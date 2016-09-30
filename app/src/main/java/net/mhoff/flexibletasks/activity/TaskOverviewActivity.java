package net.mhoff.flexibletasks.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import net.mhoff.flexibletasks.R;
import net.mhoff.flexibletasks.TaskListAdapter;
import net.mhoff.flexibletasks.model.Task;
import net.mhoff.flexibletasks.model.TaskManager;
import net.mhoff.flexibletasks.system.App;
import net.mhoff.flexibletasks.system.PeriodPicker;
import net.mhoff.flexibletasks.system.RefreshingAppCompatActivity;
import net.mhoff.flexibletasks.system.TaskEditor;
import net.mhoff.flexibletasks.utils.AndroidUtils;

import org.joda.time.Period;

import java.util.Observable;
import java.util.Observer;

public class TaskOverviewActivity extends RefreshingAppCompatActivity {

    private TaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptNewTask();
            }
        });

        taskManager = ((App) getApplication()).getTaskManager();

        final ListView taskView = (ListView) findViewById(R.id.viewTasks);

        final TaskListAdapter adapter = new TaskListAdapter(this, taskManager.getTasks());

        taskView.setAdapter(adapter);
        taskManager.registerObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                adapter.notifyDataSetChanged();
            }
        });

        taskView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                adapter.resetExpanded();
            }
        });

        taskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.toggleExpanded(position);
            }
        });

        adapter.getTaskDoneObservable().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                taskManager.executeTask((Task) o);
            }
        });

        adapter.getTaskEnableObservable().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                taskManager.toggleTaskEnabled((Task) o);
            }
        });

        adapter.getTaskDisableObservable().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                taskManager.toggleTaskEnabled((Task) o);
            }
        });

        adapter.getTaskPostponeObservable().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                promptPostponeTask((Task) o);
            }
        });

        adapter.getTaskDeleteObservable().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                promptDeleteTask((Task) o);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onRefresh() {
        ((ArrayAdapter<?>) ((ListView) findViewById(R.id.viewTasks)).getAdapter()).notifyDataSetChanged();
    }

    private void addNewTask(Task task) {
        taskManager.addTask(task);
    }

    private void postponeTask(Task task, Period period, boolean permanent) {
        taskManager.postponeTask(task, period, permanent);
    }

    private void promptNewTask() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.task_editor_dialog, AndroidUtils.getEmptyViewGroup());

        final TaskEditor taskEditor = (TaskEditor) view.findViewById(R.id.task_editor);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_task_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.add_task_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addNewTask(taskEditor.getTask());
                    }
                })
                .setNegativeButton(R.string.add_task_dialog_cancel, null)
                .create();

        taskEditor.getValidStateChangeObservable().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(taskEditor.isInputValid());
            }
        });

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(taskEditor.isInputValid());
    }

    private void promptPostponeTask(final Task task) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.task_postpone_dialog, AndroidUtils.getEmptyViewGroup());

        final PeriodPicker periodPicker = (PeriodPicker) view.findViewById(R.id.task_postpone_period);
        final CheckBox permanentCheckBox = (CheckBox) view.findViewById(R.id.task_postpone_permanent);
        final TextView taskLabel = (TextView) view.findViewById(R.id.task_postpone_label);

        taskLabel.setText(String.format(getString(R.string.task_postpone_label), task.getLabel()));

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.postpone_task_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.postpone_task_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        postponeTask(task, periodPicker.getPeriod(), permanentCheckBox.isChecked());
                    }
                })
                .setNegativeButton(R.string.postpone_task_dialog_cancel, null)
                .create();

        periodPicker.addOnChangeObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(periodPicker.isInputValid());
            }
        });

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(periodPicker.isInputValid());
    }

    private void promptDeleteTask(final Task task) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.task_delete_dialog_title)
                .setMessage(String.format(getString(R.string.task_delete_dialog_label), task.getLabel()))
                .setPositiveButton(R.string.task_delete_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskManager.removeTask(task);
                    }
                })
                .setNegativeButton(R.string.task_delete_dialog_cancel, null)
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            default:
                break;
        }

        return true;
    }
}
