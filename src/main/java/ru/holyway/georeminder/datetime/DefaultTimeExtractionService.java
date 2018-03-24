package ru.holyway.georeminder.datetime;

import org.springframework.stereotype.Component;
import ru.holyway.georeminder.datetime.tructure.DateMessageStructure;
import ru.holyway.georeminder.datetime.unit.DayOfWeek;
import ru.holyway.georeminder.datetime.unit.DayOffsets;
import ru.holyway.georeminder.datetime.unit.TimeOfDay;
import ru.holyway.georeminder.datetime.unit.TimeUnitsMapper;
import ru.holyway.georeminder.nlp.grapheme.GraphemeAnalyzer;
import ru.holyway.georeminder.service.TimeExtractionService;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Component
public class DefaultTimeExtractionService implements TimeExtractionService {

    private final TimeUnitsMapper timeUnitsMapper;
    private final GraphemeAnalyzer graphemeAnalyzer;

    public DefaultTimeExtractionService(TimeUnitsMapper timeUnitsMapper, GraphemeAnalyzer graphemeAnalyzer) {
        this.timeUnitsMapper = timeUnitsMapper;
        this.graphemeAnalyzer = graphemeAnalyzer;
    }

    @Override
    public Date extractTime(String message) {

        String[] graphemes = graphemeAnalyzer.extractGraphemes(message);
        DateMessageStructure structure = new DateMessageStructure();
        for (int i = 0; i < graphemes.length; i++) {
            TimeOfDay timeOfDay = timeUnitsMapper.resolveTimeOfDay(graphemes[i]);
            if (timeOfDay != null) {
                structure.setTimeOfDay(timeOfDay);
                structure.setTimeOfDayPos(i);
            }

            DayOfWeek daysOfWeek = timeUnitsMapper.resolveDaysOfWeek(graphemes[i]);
            if (daysOfWeek!= null) {
                structure.setDayOfWeek(daysOfWeek);
                structure.setDayOfWeekPos(i);
            }

            DayOffsets daysOffsets = timeUnitsMapper.resolveDaysOffsets(graphemes[i]);
            if (daysOffsets != null) {
                structure.setDayOffset(daysOffsets);
                structure.setDayOffsetPos(i);
            }
        }


        return resolve(structure);
    }

    private Date resolve(DateMessageStructure str) {
        //if (structure.get)

        //dayOffset + timeOfDay - завтра вечером
        if (
                str.getDayOffsetPos() != -1 &&
                        str.getTimeOfDayPos() != -1 &&
                        str.getDayOffsetPos() < str.getTimeOfDayPos()) {

            //Date date = new Date();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            calendar.add(Calendar.DAY_OF_YEAR, str.getDayOffset().getOffset());
            /*calendar.*/
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));

            calendar.add(Calendar.HOUR_OF_DAY, str.getTimeOfDay().getTime());
            //calendar.add(Calendar.);
            return calendar.getTime();

        }

        //timeOfDay + dayOfWeek - вечером в четверт
        else if (
                str.getTimeOfDayPos() != -1 &&
                str.getDayOfWeekPos() != -1) {

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            int offset = 7 - currentDayOfWeek + str.getDayOfWeek().getOffset();

            calendar.add(Calendar.DAY_OF_WEEK, offset);

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));
            calendar.add(Calendar.HOUR_OF_DAY, str.getTimeOfDay().getTime());
            //calendar.add(Calendar.);
            return calendar.getTime();

        }

        //dayOfWeek - в четверг
        else if (
                str.getDayOfWeekPos() != -1) {

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int offset = 7 - currentDayOfWeek + str.getDayOfWeek().getOffset();
            calendar.add(Calendar.DAY_OF_WEEK, offset);
            return calendar.getTime();
        }

        //dayOffset - вечером
        else if (
                str.getTimeOfDayPos() != -1) {

            //Date date = new Date();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));


            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));

            if (hours > str.getTimeOfDay().getTime()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            calendar.add(Calendar.HOUR_OF_DAY, str.getTimeOfDay().getTime());
            //calendar.add(Calendar.);
            return calendar.getTime();
        }

        //dayOffset
        else if (str.getDayOffsetPos() != -1) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+4:00"));

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.add(Calendar.HOUR_OF_DAY, -hours);
            calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
            calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));
            calendar.add(Calendar.HOUR_OF_DAY, TimeOfDay.MORNING.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, str.getDayOffset().getOffset());

            return calendar.getTime();
        }

        return null;
    }


}
