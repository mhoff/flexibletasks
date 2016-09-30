package net.mhoff.flexibletasks.system;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.GridLayout;

import net.mhoff.flexibletasks.R;
import net.mhoff.flexibletasks.model.Task;

import java.util.Observable;
import java.util.Observer;

public class TaskEditor extends GridLayout {

    private EditText labelInput;
    private PeriodPicker periodPicker;

    private boolean valid = false;

    private Observable validStateChangeObservable = new Observable() {
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
        }
    };

    public TaskEditor(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public TaskEditor(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addView(((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.task_editor, this, false));

        labelInput = (EditText) findViewById(R.id.input_label);
        periodPicker = (PeriodPicker) findViewById(R.id.input_interval);

        labelInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateValidity();
            }
        });

        periodPicker.addOnChangeObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                updateValidity();
            }
        });

        updateValidity();
    }

    private void updateValidity() {
        boolean validStateChanged = isInputValid() != valid;
        if (validStateChanged) {
            valid = !valid;
            validStateChangeObservable.notifyObservers();
        }
    }

    public boolean isInputValid() {
        return labelInput.length() > 0 && periodPicker.isInputValid();
    }

    public Task getTask() {
        if (isInputValid()) {
            return new Task(labelInput.getText().toString(), periodPicker.getPeriod());
        } else {
            throw new IllegalStateException("Input not valid");
        }
    }

    public Observable getValidStateChangeObservable() {
        return validStateChangeObservable;
    }
}
