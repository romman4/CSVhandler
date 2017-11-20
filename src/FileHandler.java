import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class FileHandler {
    private ArrayList<String> listOfFiles;
    FileHandler(ArrayList<String> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    private TreeSet<CsvString> setOfCsvStrings = new TreeSet<CsvString>(new Comparator<CsvString>() {
        public int compare(CsvString o1, CsvString o2) {
            int i1 = Integer.parseInt(o1.id.replaceAll("[^0-9]+", ""));
            int i2 = Integer.parseInt(o2.id.replaceAll("[^0-9]+", ""));
            int i = i1 - i2;
            if (i == 0) {                               // пишем компаратор для сортировки по цифрам id
                return 1;
            } else return i;
        }
    });
    private TreeMap<String, TreeSet<CsvString>> daySessions = new TreeMap<String, TreeSet<CsvString>>(new Comparator<String>() {
        public int compare(String o1, String o2) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            Date date1 = new Date();
            Date date2 = new Date();
            try {                                         // пишем компаратор для сортировки записей мапы по дате
                date1 = dateFormat.parse(o1);
                date2 = dateFormat.parse(o2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return (int)(date1.getTime()/1000 - date2.getTime()/1000);
        }
    });

        void handleFiles() throws IOException {
        BufferedReader reader = null;
        for (String curFileName :                                                 // Циклом перебираем файлы .csv
                listOfFiles) {
            reader = new BufferedReader(new FileReader(Handler.srcDirectory + "/" + curFileName));
            while (reader.ready()) {
                handleString(reader.readLine());                        // считываем построчно файл
            }
            deleteStringsSameUrlAndId(daySessions);
            createOutputFile(daySessions, curFileName);
            daySessions.clear();
        }
        if (reader != null) {
            reader.close();
        }
    }

    private void deleteStringsSameUrlAndId(TreeMap<String, TreeSet<CsvString>> daySessions) {
        for (String s:                                          // перебираем ключи
                daySessions.keySet()) {
            daySessions.put(s, filterSetOfCsvStrings(daySessions.get(s)));
        }
    }

    private TreeSet<CsvString> filterSetOfCsvStrings(TreeSet<CsvString> csvStrings) {
        boolean hasResultSetIdAndUrl = false;
            TreeSet<CsvString> resultSet = new TreeSet<CsvString>();
            for (CsvString string :
                csvStrings) {
                for (CsvString temp : resultSet) {
                    if (string.compareTo(temp) == 0) {
                        resultSet.remove(temp);
                        temp.time = (string.time + temp.time) / 2;
                        resultSet.add(temp);
                        hasResultSetIdAndUrl = true;
                    }
                    if (hasResultSetIdAndUrl) break;
                }
                if (hasResultSetIdAndUrl) {
                    hasResultSetIdAndUrl = false;
                } else {
                    resultSet.add(string);
                }
            }
            return resultSet;
    }

    private void createOutputFile(TreeMap<String, TreeSet<CsvString>> daySessions, String curFileName) {
        String outputFileName = Handler.outputFilenamePrefix + curFileName;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Handler.outDirectory + "/" + outputFileName));
            for (String tempString :
                    daySessions.keySet()) {
                writer.write(tempString + "\n");
                for (CsvString csvString:
                        daySessions.get(tempString)) {
//                    writer.write(csvString.currentTime + ",");
                    writer.write(csvString.id + ",");
                    writer.write(csvString.url + ",");
                    writer.write(csvString.time + "\n");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Не удалось создать файл");
        }

    }

    private void handleString(String s) {
        CsvString newString = new CsvString(s);                                             // из текстовой строки создаём строку нашего типа
        int timeToDayEnd = (int) (86400 - (newString.currentTime % 86400));                 // считаем время до конца дня
        int numberOfIterations = 0;
        while(isConnectionMoreThanToEndDayTime(newString, timeToDayEnd)) {                        // если проведенное время на сайте больше чем время до конца дня заходим в цикл
            CsvString curString = new CsvString(newString.currentTime, newString.id, newString.url, timeToDayEnd,          // создаем новую строку цсв, в которую добавляем всю исходную строку
                    CsvString.getHumanReadableDate(newString.currentTime + (86400 * numberOfIterations)));            // но время сессии устанавливаем как время до конца дня
            addStringToMap(curString);
            newString.time = newString.time - timeToDayEnd;
            timeToDayEnd = 86400;
            numberOfIterations++;
        }
        newString.currentHumanReadableTime = CsvString.getHumanReadableDate(newString.currentTime + (86400 * numberOfIterations));
        addStringToMap(newString);
        numberOfIterations = 0;
        }

    private boolean isConnectionMoreThanToEndDayTime(CsvString string, int timeToDayEnd) {
        return ((string.time) > (timeToDayEnd));
    }

    private void addStringToMap(CsvString newString) {
        if (daySessions.containsKey(newString.currentHumanReadableTime)) {                // если мап содержит ключ с датой из этой строки
            setOfCsvStrings = daySessions.get(newString.currentHumanReadableTime);
            setOfCsvStrings.add(newString);
            daySessions.put(newString.currentHumanReadableTime, new TreeSet<CsvString>(setOfCsvStrings));           // то достаём список по этой дате и добавляем в него нашу строку
            setOfCsvStrings.clear();
        } else{
            setOfCsvStrings.add(newString);                                                    // если нет, то создаём новый список, добавляем в него нашу строку
            daySessions.put(newString.currentHumanReadableTime, new TreeSet<CsvString>(setOfCsvStrings));              // и добавляем в мапу
            setOfCsvStrings.clear();
        }
    }
}
