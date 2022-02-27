package ru.avdeev.chat.client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageLog {

    private static final int PRELOAD_MESSAGE_SIZE = 100;
    private static final String LOG_PATH = "log/";
    private final File file;

    public MessageLog(String filename) {

        file = new File(LOG_PATH + filename);
        if (!file.exists()) {
            if (!new File(LOG_PATH).mkdirs()) {
                System.out.println("Can't create directory " + LOG_PATH);
            }
        }
    }

    public void write(String message) {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            bufferedWriter.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readMessages() {

        List<String> result = new ArrayList<>();

        if (file.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                List<String> stringList = bufferedReader.lines().collect(Collectors.toList());
                int logSize = stringList.size();
                int startIndex = Math.max(logSize - PRELOAD_MESSAGE_SIZE, 0);
                result = stringList.subList(startIndex, logSize);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
