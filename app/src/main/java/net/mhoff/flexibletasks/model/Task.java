package net.mhoff.flexibletasks.model;

import android.support.annotation.NonNull;

import net.mhoff.flexibletasks.utils.JodaUtils;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.Serializable;

public class Task implements Comparable<Task>, Serializable {

    private String label;
    private Period interval;
    private long sigmaMillis;

    private boolean fixed;
    private boolean enabled;

    private DateTime nextOccurrence;

    public Task(String label, Period interval) {
        this.label = label;
        this.interval = interval;

        this.enabled = true;
        this.fixed = false;

        this.sigmaMillis = Math.round(JodaUtils.toMillis(interval) * 0.1);
    }

    public String getLabel() {
        return label;
    }

    public boolean isDue() {
        return nextOccurrence == null || nextOccurrence.isBeforeNow();
    }

    public long getSigmaMillis() {
        return sigmaMillis;
    }

    public float getDuePercentage() {
        if (nextOccurrence == null) {
            return 1;
        }
        DateTime begin = nextOccurrence.minus(sigmaMillis);
        if (fixed) {
            begin = begin.minus(sigmaMillis);
        }
        if (begin.isBeforeNow()) {
            float ratio = (float) (DateTime.now().getMillis() - begin.getMillis()) / (2 * sigmaMillis);
            if (ratio > 1) {
                return 1;
            } else {
                return ratio;
            }
        } else {
            return 0;
        }
    }

    public DateTime getNextOccurrence() {
        return nextOccurrence;
    }

    public void setDone(DateTime lastOccurrence) {
        nextOccurrence = lastOccurrence.plus(interval);
    }

    public void postponeOccurrence(Period period) {
        DateTime last;
        if (isDue() || nextOccurrence == null) {
            last = DateTime.now();
        } else {
            last = nextOccurrence;
        }
        nextOccurrence = last.plus(period);
    }

    public void extendInterval(Period period) {
        postponeOccurrence(period);
        interval = interval.plus(period);
    }

    public void setDone() {
        setDone(DateTime.now());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Period getInterval() {
        return interval;
    }

    @Override
    public int compareTo(@NonNull Task task) {
        if (isEnabled() && task.isEnabled()) {
            if (nextOccurrence != null && task.nextOccurrence != null) {
                return nextOccurrence.compareTo(task.nextOccurrence);
            } else if (nextOccurrence != null) {
                return nextOccurrence.compareTo(DateTime.now());
            } else if (task.nextOccurrence != null) {
                return DateTime.now().compareTo(task.nextOccurrence);
            } else {
                return 0;
            }
        } else {
            if (isEnabled()) {
                return -1;
            }
            if (task.isEnabled()) {
                return 1;
            }
            return 0;
        }
    }

}
