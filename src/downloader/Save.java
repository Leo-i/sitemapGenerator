package downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Save {

    private Map<String, Set<String>> urlMap;
    private File folder = null;
    private String originalUrl;

    public Save(Map<String, Set<String>> urlMap, String fileName, String originalUrl) {
        this.urlMap = urlMap;
        this.originalUrl = originalUrl;

        if (fileName != null && !fileName.equals("")) {
            File file = new File(fileName);
            if(file.isDirectory() || !file.exists() && file.mkdirs())
                folder = file;
        }

        new Thread(this::print).start();

    }

    private void print() {
        String fileName = "sites";
        File file;
        if (folder == null)
            file = new File(fileName);
        else {
            file = new File(folder, fileName);
        }
        int embeddedCount = 0;

        try (PrintStream stream = new PrintStream(new FileOutputStream(file))) {

                writeInFile(stream, embeddedCount, originalUrl, new HashSet<>());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private synchronized void writeInFile(PrintStream stream, int embeddedCount, String key, HashSet<String> set ) {

        stream.println(createName(key, embeddedCount));

        if (set.contains(key))
            return;
        set.add(key);

        Set<String> urls = null;
        if (urlMap.containsKey(key))
            urls = urlMap.get(key);


        if (urls != null) {
            for (String url : urls) {
                writeInFile(stream, embeddedCount + 1, url, set);
            }
        }

    }

    private String createName(String key, int embeddedCount) {

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < embeddedCount; i++)
            str.append("\t");
        str.append(key);
        return str.toString();
    }
}
