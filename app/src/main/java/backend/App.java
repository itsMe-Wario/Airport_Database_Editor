package backend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class App {
    private static MongoClient mongoClient;

    public static void main(String[] args) {
        String uri = "mongodb://localhost:27017";
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int width = screenSize.width;
        int height = screenSize.height;

        JFrame frame = new JFrame("Admin Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocation(width / 2 - frame.getWidth() / 2, height / 2 - frame.getHeight() / 2);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        
        JLabel idLabel = new JLabel("ID:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(idLabel, constraints);
        
        JTextField idField = new JTextField(20);
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(idField, constraints);
        
        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, constraints);
        
        JPasswordField passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, constraints);
        
        JButton submitButton = new JButton("Submit");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, constraints);
        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passwordField.getPassword());
                
                try {
                    if (mongoClient == null) {
                        mongoClient = MongoClients.create(uri);
                    }
                    MongoDatabase admindatabase = mongoClient.getDatabase("admin");
                    Document docadmin = admindatabase.getCollection("registration").find(new Document("email", id)).first();

                    if (docadmin != null) {
                        Document docadminpass = admindatabase.getCollection("registration").find(new Document("email", id).append("password", password)).first();
                        if (docadminpass != null){
                            frame.dispose();
                            showDatabaseWindow();
                        } else {
                            JOptionPane.showMessageDialog(frame, "Incorrect password for ID: " + id);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "No admin document found for ID: " + id);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        frame.add(panel);
        frame.setVisible(true);
    }
    
    private static JPanel dataPanel = new JPanel(new BorderLayout());

private static void showDatabaseWindow() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width;
    int height = screenSize.height;

    JFrame dbFrame = new JFrame("Database Contents");
    dbFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    dbFrame.setSize(width, height);
    dbFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

    JPanel mainPanel = new JPanel(new BorderLayout());

    JPanel buttonPanel = new JPanel(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.insets = new Insets(20, 10, 40, 10);
    constraints.anchor = GridBagConstraints.WEST; 

    JButton arrivalButton = new JButton("Arriving Flights");
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buttonPanel.add(arrivalButton, constraints);

    JButton departureButton = new JButton("Departing Flights");
    constraints.gridy = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buttonPanel.add(departureButton, constraints);

    JButton flightBookingButton = new JButton("Booking");
    constraints.gridy = 2;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buttonPanel.add(flightBookingButton, constraints);

    JButton clientButton = new JButton("Client Accounts");
    constraints.gridy = 3;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buttonPanel.add(clientButton, constraints);

    JButton cargoButton = new JButton("Cargo");
    constraints.gridy = 4;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buttonPanel.add(cargoButton, constraints);

    JButton clientCargoButton = new JButton("Client Cargo");
    constraints.gridy = 5;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buttonPanel.add(clientCargoButton, constraints);

    JButton anotherAirrivalButton = new JButton("Another Arrival");
    constraints.gridy = 6;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buttonPanel.add(anotherAirrivalButton, constraints);

    JButton anotherDepartureButton = new JButton("Another Departure");
    constraints.gridy = 7;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buttonPanel.add(anotherDepartureButton, constraints);

    mainPanel.add(buttonPanel, BorderLayout.WEST);
    mainPanel.add(dataPanel, BorderLayout.CENTER);

    dbFrame.add(mainPanel);
    dbFrame.setVisible(true);

    arrivalButton.addActionListener(e -> showCollectionData("airport_management", "arrival_list", dbFrame));
    departureButton.addActionListener(e -> showCollectionData("airport_management", "departure_list", dbFrame));
    flightBookingButton.addActionListener(e -> showCollectionData("airport_management", "flight_tickets", dbFrame));
    clientButton.addActionListener(e -> showCollectionData("airport_management", "client_account", dbFrame));
    cargoButton.addActionListener(e -> showCollectionData("airport_management", "cargo", dbFrame));
    clientCargoButton.addActionListener(e -> showCollectionData("airport_management", "cargo_client", dbFrame));
    anotherAirrivalButton.addActionListener(e -> showCollectionData("airport_management", "another_airport_arrival", dbFrame));
    anotherDepartureButton.addActionListener(e -> showCollectionData("airport_management", "another_airport_departure", dbFrame));
}

private static void showCollectionData(String dbName, String collectionName, JFrame dbFrame) {
    
    dataPanel.removeAll(); // Remove all existing components from dataPanel

    ImageIcon imageIcon = new ImageIcon("D:\\pythonProject\\Airport_Database_Editor\\app\\src\\main\\java\\backend\\img\\Front.png");
    
    // Create a JLabel to hold the image
    JLabel imageLabel = new JLabel();
    imageLabel.setIcon(imageIcon);
    
    // Add the JLabel to dataPanel
    dataPanel.add(imageLabel, BorderLayout.CENTER);
    
    // Revalidate and repaint the panel to update the display
    dataPanel.revalidate();
    dataPanel.repaint();

    MongoDatabase database = mongoClient.getDatabase(dbName);
    MongoCollection<Document> collection = database.getCollection(collectionName);

    List<Document> documents = new ArrayList<>();
    try (MongoCursor<Document> cursor = collection.find().iterator()) {
        while (cursor.hasNext()) {
            documents.add(cursor.next());
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(dbFrame, "Error retrieving data: " + e.getMessage());
        e.printStackTrace();
        return;
    }

    if (documents.isEmpty()) {
        JOptionPane.showMessageDialog(dbFrame, "No documents found in the collection.");
        return;
    }

    List<String> columns = new ArrayList<>(documents.get(0).keySet());
    DefaultTableModel tableModel = new DefaultTableModel(columns.toArray(), 0);

    for (Document document : documents) {
        List<Object> row = new ArrayList<>();
        for (String column : columns) {
            row.add(document.get(column));
        }
        tableModel.addRow(row.toArray());
    }

    JTable table = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(table);
    dataPanel.add(scrollPane, BorderLayout.CENTER);

    JButton addButton = new JButton("Add");
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);

    dataPanel.add(buttonPanel, BorderLayout.SOUTH);


    addButton.addActionListener(e -> addNewRow(columns, dbName, collectionName));
    editButton.addActionListener(e -> editSelectedRow(table, dbName, collectionName));
    deleteButton.addActionListener(e -> deleteSelectedRow(table, dbName, collectionName));

    SwingUtilities.invokeLater(() -> {
        dbFrame.revalidate();  // Revalidate the frame to reflect changes
        dbFrame.repaint();     // Repaint the frame to ensure proper display
    });

    printTableToConsole(tableModel);
}

    private static void printTableToConsole(DefaultTableModel tableModel) {
        int columnCount = tableModel.getColumnCount();
        int rowCount = tableModel.getRowCount();
    
        for (int i = 0; i < columnCount; i++) {
            System.out.print(tableModel.getColumnName(i) + "\t");
        }
        System.out.println();
    
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                System.out.print(tableModel.getValueAt(row, col) + "\t");
            }
            System.out.println();
        }
    }

    private static void editSelectedRow(JTable table, String dbName, String collectionName) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No row selected.");
            return;
        }
    
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Document oldDocument = new Document();
        for (int i = 0; i < model.getColumnCount(); i++) {
            oldDocument.append(model.getColumnName(i), model.getValueAt(selectedRow, i));
        }
    
        JTextField[] fields = new JTextField[model.getColumnCount()];
        JPanel panel = new JPanel(new GridLayout(fields.length, 2));
        
        for (int i = 0; i < fields.length; i++) {
            panel.add(new JLabel(model.getColumnName(i) + ":"));
            fields[i] = new JTextField(model.getValueAt(selectedRow, i).toString());
            panel.add(fields[i]);
        }
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Record", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Document newDocument = new Document();
            for (int i = 0; i < fields.length; i++) {
                newDocument.append(model.getColumnName(i), fields[i].getText());
            }
    
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
    
            // Use the primary key field(s) to identify the original document
            collection.replaceOne(oldDocument, newDocument);
    
            JOptionPane.showMessageDialog(null, "Record updated.");
            showCollectionData(dbName, collectionName, (JFrame) SwingUtilities.getWindowAncestor(table));
        }
    }    
    
    private static void deleteSelectedRow(JTable table, String dbName, String collectionName) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No row selected.");
            return;
        }
    
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Document documentToDelete = new Document();
        for (int i = 0; i < model.getColumnCount(); i++) {
            documentToDelete.append(model.getColumnName(i), model.getValueAt(selectedRow, i));
        }
    
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
    
            collection.deleteOne(documentToDelete);
    
            JOptionPane.showMessageDialog(null, "Record deleted.");
            showCollectionData(dbName, collectionName, (JFrame) SwingUtilities.getWindowAncestor(table));
        }
    }

    private static void addNewRow(List<String> columns, String dbName, String collectionName) {
        JTextField[] fields = new JTextField[columns.size()];
        JPanel panel = new JPanel(new GridLayout(fields.length, 2));
        
        for (int i = 0; i < fields.length; i++) {
            panel.add(new JLabel(columns.get(i) + ":"));
            fields[i] = new JTextField();
            panel.add(fields[i]);
        }
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Record", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Document newDocument = new Document();
            for (int i = 0; i < fields.length; i++) {
                newDocument.append(columns.get(i), fields[i].getText());
            }
    
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
    
            collection.insertOne(newDocument);
    
            JOptionPane.showMessageDialog(null, "New record added.");
            showCollectionData(dbName, collectionName, new JFrame("Database Contents")); /*completed*/
        }
    }    
}
