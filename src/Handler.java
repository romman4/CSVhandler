import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class Handler {
    static String outputFilenamePrefix = "avg_";
    private static ArrayList<String> listOfFiles;
    static String srcDirectory;
    static String outDirectory;
    public static void main(String[] args){
        listOfFiles = new ArrayList<String>();
        try {
            srcDirectory = args[0];
            findFiles(srcDirectory);                                            // запускаем метод поиска файлов с раширением .csv в заданной папке
        } catch (Exception e) {
            System.out.println("Некорректный путь для поиска файлов!");
        }
        try {
            outDirectory = args[1];
        } catch (Exception e) {
            outDirectory = srcDirectory;
        }
        FileHandler fileHandler = new FileHandler(listOfFiles);
        try {
            fileHandler.handleFiles();
        } catch (IOException e) {
            System.out.println("Что-то не то со вводом");
        }
    }

    private static void findFiles(String srcPath) {
        File dir = new File(srcPath);
        File[] files = dir.listFiles(new FilenameFilter() {                     // создаём анонимный класс для поиска в директории файлов по имени
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".csv")) && !(name.startsWith("avg_"));
            }
        });
        if (files != null) {
            for (File f :
                    files) {
                listOfFiles.add(f.getName());
            }
        } else System.out.println("Папка не содержит файлов Csv!");
    }
}
