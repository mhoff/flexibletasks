package net.mhoff.flexibletasks.utils;

import org.joda.time.DateTimeConstants;
import org.joda.time.Period;

public class JodaUtils {

    public static long toMillis(Period period) {
        return period.getMillis() + period.getSeconds() * DateTimeConstants.MILLIS_PER_SECOND
                + period.getMinutes() * DateTimeConstants.MILLIS_PER_MINUTE
                + period.getHours() * DateTimeConstants.MILLIS_PER_HOUR
                + period.getDays() * DateTimeConstants.MILLIS_PER_DAY
                + period.getWeeks() * DateTimeConstants.MILLIS_PER_WEEK
                + (period.getYears() * 12 + period.getMonths()) * 30 * DateTimeConstants.MILLIS_PER_DAY;
    }

}
