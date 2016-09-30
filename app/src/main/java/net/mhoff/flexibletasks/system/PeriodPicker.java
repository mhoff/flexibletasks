package net.mhoff.flexibletasks.system;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.mhoff.flexibletasks.R;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PeriodPicker extends LinearLayout {

    private static final int DEFAULT_VALUE = 1;
    private static final List<Unit> UNITS;

    static {
        UNITS = new ArrayList<>();
        UNITS.add(new Unit(Minutes.ONE.toStandardSeconds().getSeconds()) {
            @Override
            public Period toPeriod(int value) {
                return new Period(0, 0, 0, 0, 0, 0, value, 0);
            }
        });
        UNITS.add(new Unit(Hours.ONE.toStandardMinutes().getMinutes()) {
            @Override
            public Period toPeriod(int value) {
                return new Period(0, 0, 0, 0, 0, value, 0, 0);
            }
        });
        UNITS.add(new Unit(Days.ONE.toStandardHours().getHours()) {
            @Override
            public Period toPeriod(int value) {
                return new Period(0, 0, 0, 0, value, 0, 0, 0);
            }
        });
        UNITS.add(new Unit(Weeks.ONE.toStandardDays().getDays()) {
            @Override
            public Period toPeriod(int value) {
                return new Period(0, 0, 0, value, 0, 0, 0, 0);
            }
        });
        UNITS.add(new Unit(24) {
            @Override
            public Period toPeriod(int value) {
                return new Period(0, 0, value, 0, 0, 0, 0, 0);
            }
        });
        UNITS.add(new Unit(36) {
            @Override
            public Period toPeriod(int value) {
                return new Period(0, value, 0, 0, 0, 0, 0, 0);
            }
        });
        UNITS.add(new Unit() {
            @Override
            public Period toPeriod(int value) {
                return new Period(value, 0, 0, 0, 0, 0, 0, 0);
            }
        });
    }

    private EditText valueEdit;

    private Unit currentUnit;

    private Observable onChangeObservable = new Observable() {
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
        }
    };

    public void addOnChangeObserver(Observer observer) {
        onChangeObservable.addObserver(observer);
    }

    public PeriodPicker(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public PeriodPicker(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addView(((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.period_picker, this, false));

        valueEdit = (EditText) findViewById(R.id.period_picker_value);
        Spinner unitPicker = (Spinner) findViewById(R.id.period_picker_unit);

        valueEdit.setText(String.valueOf(DEFAULT_VALUE));

        unitPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                currentUnit = UNITS.get(position);
                onChangeObservable.notifyObservers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                throw new IllegalStateException("No unit selected");
            }
        });

        valueEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                onChangeObservable.notifyObservers();
            }
        });

        currentUnit = UNITS.get(0);
    }

    public boolean isInputValid() {
        // TODO and lower than max val
        return valueEdit.length() > 0 && Integer.parseInt(valueEdit.getText().toString()) > 0;
    }

    public Period getPeriod() {
        return currentUnit.toPeriod(Integer.parseInt(valueEdit.getText().toString()));
    }

    private static abstract class Unit {

        private final int maxValue;

        public Unit(int maxValue) {
            this.maxValue = maxValue;
        }

        public Unit() {
            this(0);
        }

        public int getCorrectedValue(int value) {
            if (value <= 0) {
                return 1;
            } else if (value > maxValue) {
                return maxValue;
            }
            return value;
        }

        public abstract Period toPeriod(int value);

    }

}
