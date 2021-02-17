package com.github.thethingyee.database;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DatabasePost {

    public static void main(String[] args) {
        System.out.println("------------------------------------------------------------");
        System.out.println("| Welcome to AnnouncementsPoster                           |");
        System.out.println("| Developer: TheTHINGYEEEEE                                |");
        System.out.println("|                                                          |");
        System.out.println("| Version: 1.0.0                                           |");
        System.out.println("|                                                          |");
        System.out.println("| Specifically made for AnnounceAPI.                       |");
        System.out.println("------------------------------------------------------------");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the method you wanna use. (remove, add)");
        String method = scanner.nextLine();
        if(method.equalsIgnoreCase("remove")) {
            System.out.println("Enter the ID that you wanna remove: ");
            String id = scanner.nextLine();

            System.out.println("Attempting to remove ID " + id + "....");
            try {
                URL apiURL = new URL("https://node1.thingyservers.xyz/remove/" + id);
                HttpsURLConnection connection = (HttpsURLConnection) apiURL.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                System.out.println("Response code: " + responseCode + " OK \n.\n.\n.\n.\n.");

                // Get JSON from URL
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse to JSON / read as JSON
                JSONObject jsonObject = new JSONObject(response.toString());
                System.out.println(jsonObject.getString("msg"));

            } catch(Exception e) {
                System.out.println("Error catched... " + e.getMessage());
                System.exit(0);
            }
        } else {

            System.out.println("Enter a title: ");
            String etitle = scanner.nextLine().replaceAll(" ", "%20");
            String otitle = etitle.replaceAll("/", "%2F");
            String title = otitle.replace("'", "%27");

            System.out.println("Enter the body: ");
            String ebodyDesc = scanner.nextLine().replaceAll(" ", "%20");
            String obodyDesc = ebodyDesc.replaceAll("/", "%2F");
            String bodyDesc = obodyDesc.replaceAll("'", "%27");

            System.out.println("Enter the state (solved, none): ");
            String stateInput = scanner.nextLine();
            String state;
            switch (stateInput) {
                case "solved":
                    state = "solved";
                    break;
                case "none":
                    state = "none";
                    break;

                default:
                    state = null;
                    System.out.println("Invalid response.");
                    System.exit(0);
                    break;
            }
            System.out.println("--------------------");
            System.out.println("Title: " + title.replaceAll("%20", " ").replaceAll("%2F", "/").replaceAll("'", "%27") + "\n ");
            System.out.println("Body: " + bodyDesc.replaceAll("%20", " ").replaceAll("%2F", "/").replaceAll("'", "%27") + "\n ");
            System.out.println("State: " + state);
            System.out.println("--------------------");

            System.out.println("Proceed with action? (Y/N)");
            String bool = scanner.nextLine();
            if (bool.equalsIgnoreCase("y")) {

                System.out.println("Connecting to the api...");
                try {

                    URL apiURL = new URL("https://node1.thingyservers.xyz/getids");
                    HttpsURLConnection connection = (HttpsURLConnection) apiURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    System.out.println("Response code: " + responseCode);

                    // Get JSON from URL
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse to JSON / read as JSON
                    JSONObject jsonObject = new JSONObject(response.toString());
                    System.out.println("Fetched ids: " + jsonObject.length());

                    System.out.println(".\n.\n.\n.\n.\nGetting valid id...");
                    int validID;

                    int i = 0;
                    while (true) {

                        if (!jsonObject.has(String.valueOf(i)) && jsonObject.isNull(String.valueOf(i))) {
                            System.out.println(i + ": VALID");
                            validID = i;
                            System.out.println("\n.\n.\n.\n.\n.\n.\n.\n.\nID validated: " + validID);
                            break;
                        } else {
                            System.out.println(i + ": TAKEN");
                            i++;
                        }
                    }
                    System.out.println("\n.\n.\n.\n.\n.\n.\n.\nAdding to the database...");
                    URL postURL = new URL("https://node1.thingyservers.xyz/add/" + i + "/" + title + "/" + bodyDesc + "/" + state);

                    HttpURLConnection postConnection = (HttpsURLConnection) postURL.openConnection();
                    postConnection.setRequestMethod("GET");

                    System.out.println("Executing '" + postURL.toString() + "'");

                    postConnection.connect();

                    int postResponseCode = postConnection.getResponseCode();
                    System.out.println("Response code: " + postResponseCode);

                    BufferedReader in2 = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
                    String postInputLine;
                    StringBuffer res = new StringBuffer();
                    while ((postInputLine = in2.readLine()) != null) {
                        res.append(postInputLine);
                    }
                    in.close();

                    JSONObject postObject = new JSONObject(res.toString());
                    String msg = postObject.getString("msg");

                    System.out.println(msg);
                } catch (Exception e) {
                    System.out.println("Error catched... " + e.getMessage());
                    System.exit(0);
                }
            } else {
                System.out.println("Action cannot continue.");
                System.exit(0);
            }
        }
    }
}
