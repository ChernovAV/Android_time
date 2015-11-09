package com.chernov.android.android_time;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeIntentService extends IntentService {

    public final static String ENDPOINT = "http://android-logs.uran.in.ua/test.php";
    public static final String ITEM = "item";
    public static final String RECEIVER = "receiver";
    static boolean isStopped = false;

    public TimeIntentService() {
        super("TimeIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String data = null;
        try {
            data = getUrl(ENDPOINT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);

        if(data!=null) {
            // its need to be in milisecond
            Date df = new java.util.Date(Long.valueOf(data)*1000);
            String result = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(df);

            Bundle bundle = new Bundle();
            bundle.putString(ITEM, result);
            receiver.send(1, bundle);
        } else {
            receiver.send(0, null);
        }

        isStopped = true;
    }

    // получает данные по URL и возвращает их в виде массива байтов
    private byte[] getUrlBytes(String urlSpec) throws IOException {
        // создаем объект URL на базе строки urlSpec, например https://www.google.com
        URL url = new URL(urlSpec);
        // создаем объект подключения к заданному URL адресу
        // url.openConnection() - возвращает URLConnection (подключение по протоколу HTTP)
        // это откроет доступ для работы с методами запросов. HttpURLConnection - предоставляет подключение
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            // создаем пустой массив байтов
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // связь с конечной точкой
            InputStream in = connection.getInputStream();
            // если подключение с интернетом отсутствует (нет кода страницы)
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            // разбираем код по 1024 байта, пока не закончится информация
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            // чтение закончено, выдаем массив байтов
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    // преобразует результат (массив байтов) из getUrlBytes(String) в String
    private String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
