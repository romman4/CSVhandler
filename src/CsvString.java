import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class CsvString implements Comparable<CsvString>{
    long currentTime;
    String currentHumanReadableTime, id, url;
    int time;

    CsvString() {
    }

    CsvString(String string) {
        this.currentTime = Long.parseLong(string.split(",")[0]);
        this.currentHumanReadableTime = getHumanReadableDate(this.currentTime);
        this.id = string.split(",")[1];
        this.url = string.split(",")[2];
        this.time = Integer.parseInt(string.split(",")[3]);
    }

    CsvString(long currentTime, String id, String url, int i, String currentHumanReadableTime) {
        this.currentTime = currentTime;
        this.currentHumanReadableTime = currentHumanReadableTime;
        this.id = id;
        this.url = url;
        this.time = i;
    }

    static String getHumanReadableDate(Long l) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", new Locale("EN"));
        df.setTimeZone(TimeZone.getTimeZone("Europe"));
        return df.format(new Date(l*1000L)).toUpperCase();
    }

    public int compareTo(CsvString o) {
        if ((id.equals(o.id)) && url.equals(o.url)) {
            return 0;
        } else return 1;
    }
}
