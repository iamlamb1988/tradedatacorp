import tradedatacorp.tools.time.QuarterInterval;
import tradedatacorp.tools.time.MonthInterval;
import tradedatacorp.warehouse.OHLCV_BinaryWarehouse;

import java.util.TimeZone;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestMain{
    public static void main(String[] args){
        System.out.println("Warehouse testing.");
        OHLCV_BinaryWarehouse warehouse = new OHLCV_BinaryWarehouse();

        String result = warehouse.initialize(new String[]{"/root/test/mydb"});
        System.out.println(result);

        long utcRef = 1757621890000L;
        TimeZone CST = TimeZone.getTimeZone("US/Central");
        TimeZone EST = TimeZone.getTimeZone("US/Eastern");

        printTimeRange(utcRef, CST, 1);
        System.out.println("\n");
        printTimeRange(utcRef, EST, 1);
        System.out.println("\n");
        printTimeRange(utcRef, CST, 60000);
        System.out.println("\n");
        printTimeRange(utcRef, EST, 60000);
    }

    public static void printTimeRange(long utcRef, TimeZone tz, long deltaMillisecond){
        TimeZone utcTZ = TimeZone.getTimeZone("UTC");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        Instant utcInst = Instant.ofEpochMilli(utcRef);
        ZonedDateTime timezoneTime = ZonedDateTime.ofInstant(utcInst, tz.toZoneId());
        ZonedDateTime utcTime = ZonedDateTime.ofInstant(utcInst, utcTZ.toZoneId());

        System.out.println("Timezone:      "+tz.getID());
        System.out.println("Delta time:    "+deltaMillisecond);
        System.out.println("Timestamp Ref: "+utcRef);
        System.out.println(tz.getID()+" time: "+timezoneTime.format(timeFormatter));
        System.out.println("UTC time:      "+utcTime.format(timeFormatter));

        QuarterInterval tmpQtr = new QuarterInterval("Quarter", tz, utcRef, deltaMillisecond);
        MonthInterval tmpMnth = new MonthInterval("Month", utcTZ, utcRef, deltaMillisecond);

        System.out.println("Quarter Start: "+tmpQtr.getFormattedStartTime());
        System.out.println("Quarter End:   "+tmpQtr.getFormattedEndTime());

        ZonedDateTime utcStartFormat = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(tmpQtr.getStartUTC()),
            utcTZ.toZoneId()
        );
        ZonedDateTime utcEndFormat = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(tmpQtr.getEndUTC()),
            utcTZ.toZoneId()
        );

        System.out.println("Quarter UTC Start: "+tmpQtr.getStartUTC());
        System.out.println("Quarter UTC End:   "+tmpQtr.getEndUTC());
        System.out.println("Quarter UTC Start: "+utcStartFormat.format(timeFormatter));
        System.out.println("Quarter UTC End:   "+utcEndFormat.format(timeFormatter));

        System.out.println("Month Start:   "+tmpMnth.getFormattedStartTime());
        System.out.println("Month End:     "+tmpMnth.getFormattedEndTime());
        utcStartFormat = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(tmpMnth.getStartUTC()),
            utcTZ.toZoneId()
        );
        utcEndFormat = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(tmpMnth.getEndUTC()),
            utcTZ.toZoneId()
        );

        System.out.println("Month UTC Start:   "+tmpMnth.getStartUTC());
        System.out.println("Month UTC End:     "+tmpMnth.getEndUTC());
        System.out.println("Month UTC Start:   "+utcStartFormat.format(timeFormatter));
        System.out.println("Month UTC End:     "+utcEndFormat.format(timeFormatter));
    }
}