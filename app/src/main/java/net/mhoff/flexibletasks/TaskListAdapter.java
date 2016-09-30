package net.mhoff.flexibletasks;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.mhoff.flexibletasks.model.Task;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Observable;

import static java.lang.String.format;

public class TaskListAdapter extends ArrayAdapter<Task> {

    private final Context context;
    private final List<Task> tasks;
    private final PrettyTime pt = new PrettyTime();
    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private final ButtonClickRelay taskDoneObservable = new ButtonClickRelay();
    private final ButtonClickRelay taskPostponeObservable = new ButtonClickRelay();
    private final ButtonClickRelay taskDisableObservable = new ButtonClickRelay();
    private final ButtonClickRelay taskEnableObservable = new ButtonClickRelay();
    private final ButtonClickRelay taskDeleteObservable = new ButtonClickRelay();

    private int expandedPosition;

    public TaskListAdapter(Context context, List<Task> tasks) {
        super(context, -1, tasks);
        this.context = context;
        this.tasks = tasks;
        this.expandedPosition = -1;
    }

    @Override
    public
    @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row;
        Task task = tasks.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_task, parent, false);

            View controls = row.findViewById(R.id.task_controls);
            controls.findViewById(R.id.task_done).setOnClickListener(taskDoneObservable.onClickListener);
            controls.findViewById(R.id.task_postpone).setOnClickListener(taskPostponeObservable.onClickListener);
            controls.findViewById(R.id.task_disable).setOnClickListener(taskDisableObservable.onClickListener);
            controls.findViewById(R.id.task_enable).setOnClickListener(taskEnableObservable.onClickListener);
            controls.findViewById(R.id.task_delete).setOnClickListener(taskDeleteObservable.onClickListener);

            ((StatusIcon) row.findViewById(R.id.task_status)).setTask(task);
        } else {
            row = convertView;
        }

        ((StatusIcon) row.findViewById(R.id.task_status)).setTask(task);
        if (row.getTag() != task) {
            ((TextView) row.findViewById(R.id.task_label)).setText(task.getLabel());

            row.setTag(tasks.get(position));
        }

        String description;
        if (task.isEnabled()) {
            if (task.isDue()) {
                if (task.getNextOccurrence() == null) {
                    description = context.getString(R.string.task_list_row_description_initial);
                } else {
                    description = format(context.getString(R.string.task_list_row_description_after), pt.format(task.getNextOccurrence().toDate()));
                }
            } else {
                description = format(context.getString(R.string.task_list_row_description_before), pt.format(task.getNextOccurrence().toDate()));
            }
        } else {
            description = context.getString(R.string.task_list_item_disabled);
        }
        ((TextView) row.findViewById(R.id.task_description)).setText(description);

        View controls = row.findViewById(R.id.task_controls);
        controls.setVisibility(position == expandedPosition ? View.VISIBLE : View.GONE);
        if (!task.isEnabled())

        {
            controls.findViewById(R.id.task_disable).setVisibility(View.VISIBLE);
            controls.findViewById(R.id.task_enable).setVisibility(View.GONE);
        } else

        {
            controls.findViewById(R.id.task_disable).setVisibility(View.GONE);
            controls.findViewById(R.id.task_enable).setVisibility(View.VISIBLE);
        }

        return row;
    }

    public Observable getTaskDoneObservable() {
        return taskDoneObservable;
    }

    public Observable getTaskPostponeObservable() {
        return taskPostponeObservable;
    }

    public Observable getTaskDisableObservable() {
        return taskDisableObservable;
    }

    public Observable getTaskEnableObservable() {
        return taskEnableObservable;
    }

    public Observable getTaskDeleteObservable() {
        return taskDeleteObservable;
    }

    public void toggleExpanded(int position) {
        if (expandedPosition == position) {
            expandedPosition = -1;
        } else {
            expandedPosition = position;
        }
        notifyDataSetChanged();
    }

    public void resetExpanded() {
        expandedPosition = -1;
        notifyDataSetChanged();
    }

    private class ButtonClickRelay extends Observable {

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChanged();
                notifyObservers(tasks.get(expandedPosition));
                resetExpanded();
            }
        };

        private ButtonClickRelay() {
        }
    }

}
