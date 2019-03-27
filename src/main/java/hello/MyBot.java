package hello;

import org.jibble.pircbot.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MyBot extends PircBot {

    public MyBot() {
        this.setName("WeatherBotUniqque");
    }

    private static String getHttpResponse(String url, String extension) {
        String toReturn = "";
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("cmd.exe /c ruby ruby\\http_request.rb " + url + " \"" + extension + "?cc=*&dayf=1\"\n");
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";

            while ((line = b.readLine()) != null) {
                toReturn = toReturn.concat(line + "\n");
            }

            b.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e2) {
        }

        return toReturn;
    }

    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {
        if (message.toLowerCase().startsWith("weather")) {
            char[] string = message.toCharArray();
            int integerSequenceCount = 0;
            boolean foundZipCode = false;
            int zipCodeStart = 0;
            int zipCode = 0;
            for (int i = 0; i < string.length; i++) {
                if (string[i] >= '0' && string[i] <= '9') {
                    integerSequenceCount++;
                    if (integerSequenceCount == 5) {
                        foundZipCode = true;
                        zipCodeStart = i - 4;
                        break;
                    }
                } else {
                    integerSequenceCount = 0;
                }
            }

            if (foundZipCode) {
                for (int i = zipCodeStart; i < string.length; i++) {
                    zipCode *= 10;
                    zipCode += string[i] - '0';
                }
                String response = getHttpResponse("wxdata.weather.com", "/weather/local/" + Integer.toString(zipCode));
                if(response.contains("<error>")){
                    sendMessage(channel,"@" + sender + ": invalid zip code");
                } else {
                    if(response.split("<dayf>")[1].split("<t>")[1].split("</t>")[0].toCharArray().length == 0) {
                        sendMessage(channel, "@" + sender + ": the weather tonight is " +
                                response.split("<dayf>")[1].split("<part p=\"n\">")[1].split("<t>")[1].split("</t>")[0]);
                    } else {
                        sendMessage(channel, "@" + sender + ": the weather today is " +
                                response.split("<dayf>")[1].split("<t>")[1].split("</t>")[0]);
                    }
                }

            }
        }
        /*if (message.equalsIgnoreCase("time")) {
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ": The time is now " + time);
        }*/
    }

}