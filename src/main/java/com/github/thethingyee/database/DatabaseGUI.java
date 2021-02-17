package com.github.thethingyee.database;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DatabaseGUI extends JFrame implements ActionListener {

    JButton postButton;
    JTextField ip;
    JTextField title;
    JTextArea body;
    JComboBox<String> state;
    JCheckBox usehttps;

    JTextArea elabel;

    public DatabaseGUI() {

        ImageIcon icon = new ImageIcon("logo.png");
        this.setIconImage(icon.getImage());

        this.getContentPane().setLayout(new FlowLayout());

        JLabel label1 = new JLabel("Database IP: ");
        JLabel label2 = new JLabel("State: ");
        JLabel label3 = new JLabel("Title: ");
        JLabel label4 = new JLabel("Body: ");

        state = new JComboBox<>();
        state.addItem("Solved");
        state.addItem("None");

        ip = new JTextField();
        ip.setPreferredSize(new Dimension(250, 20));

        title = new JTextField();
        title.setPreferredSize(new Dimension(300, 20));

        body = new JTextArea();
        body.setPreferredSize(new Dimension(400, 300));
        body.setLineWrap(true);
        body.setWrapStyleWord(true);

        postButton = new JButton("Post");
        postButton.setVerticalTextPosition(JButton.BOTTOM);
        postButton.addActionListener(this);

        usehttps = new JCheckBox("Use HTTPS");

        JPanel panel1 = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(10, 10, 10, 10);
        c.gridx = 0;
        c.gridy = 0;

        panel1.add(label1);
        panel1.add(ip);

        c.gridx = 1;
        c.gridy = 1;

        panel1.add(usehttps, c);

        c.gridx = 0;
        c.gridy = 2;

        panel1.add(label2, c);
        c.gridx = 1;
        panel1.add(state, c);

        c.gridx = 0;
        c.gridy = 3;

        panel1.add(label3, c);
        c.gridx = 1;
        panel1.add(title, c);

        c.gridx = 0;
        c.gridy = 4;

        panel1.add(label4, c);
        c.gridx = 1;
        panel1.add(body, c);

        c.gridx = 1;
        c.gridy = 5;

        panel1.add(postButton, c);

        c.gridx = 1;
        c.gridy = 6;

        elabel = new JTextArea(3, 60);
        elabel.setWrapStyleWord(true);
        elabel.setLineWrap(true);
        elabel.setEditable(false);
        panel1.add(elabel, c);

        this.add(panel1, BorderLayout.WEST);

        this.setTitle("Announcements Poster");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(900, 650);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == postButton) {
            String titleString = title.getText().replaceAll(" ", "%20").replaceAll("/", "%2F").replaceAll("'", "%27");
            String bodyString = body.getText().replaceAll(" ", "%20").replaceAll("/", "%2F").replaceAll("'", "%27");
            String stateString = null;
            if (state.getSelectedItem().toString().equalsIgnoreCase("solved")) {
                stateString = "solved";
            } else if (state.getSelectedItem().toString().equalsIgnoreCase("none")) {
                stateString = "none";
            }

            String databaseString = null;

            System.out.println("Connecting to the api...");
            
            if(usehttps.isSelected()) {
                try {

                    databaseString = "https://" + ip.getText();
                    URL apiURL = new URL(databaseString + "/getids");
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
                    URL postURL = new URL(databaseString + "/add/" + i + "/" + titleString + "/" + bodyString + "/" + stateString);

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
                } catch (Exception exep) {
                    System.out.println("Error catched... " + exep.getMessage());
                    elabel.setText(exep.getMessage());
                }
            } else {
                try {
                    elabel.setText("Connecting to the api...");
                    databaseString = "http://" + ip.getText();
                    URL apiURL = new URL(databaseString + "/getids");
                    HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    elabel.setText("Response code: " + responseCode);
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
                    elabel.setText("Fetched ids: " + jsonObject.length());

                    System.out.println(".\n.\n.\n.\n.\nGetting valid id...");
                    elabel.setText("Getting valid id...");
                    int validID;

                    int i = 0;
                    while (true) {

                        if (!jsonObject.has(String.valueOf(i)) && jsonObject.isNull(String.valueOf(i))) {
                            System.out.println(i + ": VALID");
                            elabel.setText(i + ": VALID");
                            validID = i;
                            System.out.println("\n.\n.\n.\n.\n.\n.\n.\n.\nID validated: " + validID);
                            elabel.setText("ID validated: " + validID);
                            break;
                        } else {
                            elabel.setText(i + ": TAKEN");
                            System.out.println(i + ": TAKEN");
                            i++;
                        }
                    }
                    System.out.println("\n.\n.\n.\n.\n.\n.\n.\nAdding to the database...");
                    elabel.setText("Adding to the database...");
                    URL postURL = new URL(databaseString + "/add/" + i + "/" + titleString + "/" + bodyString + "/" + stateString);

                    HttpURLConnection postConnection = (HttpURLConnection) postURL.openConnection();
                    postConnection.setRequestMethod("GET");

                    System.out.println("Executing '" + postURL.toString() + "'");
                    elabel.setText("Executing '" + postURL.toString() + "'");

                    postConnection.connect();

                    int postResponseCode = postConnection.getResponseCode();
                    System.out.println("Response code: " + postResponseCode);
                    elabel.setText("Response code: " + postResponseCode);

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
                    elabel.setText(msg);
                } catch (Exception exep) {
                    System.out.println("Error catched... " + exep.getMessage());
                    elabel.setText(exep.getMessage());
                }
            }
        }
    }
}
