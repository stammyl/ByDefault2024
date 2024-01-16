// MainFrame.java
package com.vodafone.clientapp;

// import java.awt.BorderLayout;
// import javax.swing.JButton;
// import javax.swing.JFrame;
// import javax.swing.JPanel;
// import javax.swing.JOptionPane;

// import javax.swing.JLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.concurrent.ExecutionException;
import java.io.IOException;

//import Rest.RestClient;

public class MainFrame extends JFrame {

    private JButton startButton;
    private JButton stopButton;
    private JButton sendRequestButton;
	
	
	
	private JLabel resultLabel;
	private JList<String> dynamicList;
    private DefaultListModel<String> listModel;
	
	private RestClient restClient;
	
    public MainFrame() {
        initializeComponents();
        createLayout();
        setupListeners();
		
		// Initialize RestClient instance
        restClient = new RestClient();
    }

    private void initializeComponents() {
        startButton = new JButton("start");
        stopButton = new JButton("Stop");
		sendRequestButton = new JButton("testRequest!");//test
		
		 // Create the result label
        resultLabel = new JLabel("Result: ");

        // Create a DefaultListModel for the JList
        listModel = new DefaultListModel<>();

        // Create the JList with the DefaultListModel
        dynamicList = new JList<>(listModel);

        // Set a reasonable size for the JList
        dynamicList.setFixedCellHeight(20);
        dynamicList.setFixedCellWidth(150);

        // Add some initial items to the list
        for (int i = 1; i <= 20; i++) {
            listModel.addElement("Item " + i);
        }

       
    }

    private void createLayout() {
		//-----1---------
		
        // setLayout(new java.awt.FlowLayout());
		// setSize(400, 200);
        // setTitle("Client App");
		// setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		
		// add(startButton);
        // add(stopButton);
        // add(resultLabel);        
        
        // pack();
		//-----1---------
		
		//------2--------
		
		// JPanel panel = new JPanel(new BorderLayout());
		
		// // Create a panel for buttons and add them to the SOUTH (bottom) of the BorderLayout
        // JPanel buttonPanel = new JPanel();
        // buttonPanel.add(startButton);
        // buttonPanel.add(stopButton);
        // panel.add(buttonPanel, BorderLayout.SOUTH);
		
        // panel.add(resultLabel, BorderLayout.CENTER);

        // setContentPane(panel);
        // setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        // setTitle("GUI with Buttons");
		// setSize(400, 200);
        // pack();
		//------2--------
		
		//-----3-------
		
		//-----3-------
		JPanel panel = new JPanel(new BorderLayout());

        // Add the result label to the NORTH (top) of the BorderLayout
        panel.add(resultLabel, BorderLayout.NORTH);

        // Add the JList to the CENTER of the BorderLayout
        panel.add(new JScrollPane(dynamicList), BorderLayout.CENTER);

        // Create a panel for buttons and add them to the SOUTH (bottom) of the BorderLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(sendRequestButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Scrollable List GUI");
        setSize(900, 300); // Set initial size
    }

    private void setupListeners() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //String response = RestClient.startDockerInstance();
                    // Process the response, update GUI, etc.					
                    //System.out.println("Start Docker Instance Response: " + response);
                    System.out.println("Start Docker Instance Response: " );
					//displayMessage("Start pressed :)");
					setResult("Started!");
                } catch (Exception ex) {
                    ex.printStackTrace(); // Handle the exception appropriately
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //String response = RestClient.stopDockerInstance();
                    // Process the response, update GUI, etc.					
                    //System.out.println("Stop Docker Instance Response: " + response);
                    System.out.println("Stop Docker Instance Response: ");
					//displayMessage("Stop pressed :( :(");
					setResult("Stoped!");
                } catch (Exception ex) {
                    ex.printStackTrace(); // Handle the exception appropriately
                }
            }
        });
		
		dynamicList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Get the selected item from the JList
                String selectedValue = dynamicList.getSelectedValue();
                // Update the result label with the selected item
                resultLabel.setText("Result: " + selectedValue);
            }
        });   
		
		sendRequestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Trigger the HTTP GET request when the button is pressed
                new HttpRequestWorker().execute();
            }
        });

    }
	
	 private void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
	private void setResult(String result) {
        resultLabel.setText("Result: " + result);
    }
	
	//----Http Workers for concurrent call on API
	// SwingWorker to perform HTTP request in the background
    private class HttpRequestWorker extends SwingWorker<String, Void> {
        @Override
        protected String doInBackground() throws Exception {
            try {
                // Call the sendGetRequest method from RestClient
                return restClient.startDockerInstance();
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void done() {
            try {
                // Retrieve the result of the HTTP request
                String result = get();
                resultLabel.setText("HTTP Request Result: " + result);
				System.out.println("HTTP Request Result: " + result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
