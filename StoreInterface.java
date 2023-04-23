import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class StoreInterface extends JFrame implements ActionListener {
    private JLabel titleLabel;
    private JLabel nameLabel;
    private JLabel timeLabel;
    private JLabel inventoryLabel;
    private JLabel bagLabel;
    private JLabel totalLabel;
    private JTextField nameTextField;
    private JTextArea inventoryTextArea;
    private JTextArea bagTextArea;
    private JTextField checkoutTextField;
    private JButton searchButton;
    private JButton stopButton;

    private HashMap<String, Double> inventory;
    private HashMap<String, Double> bag;
    private double total;

    public StoreInterface() {
        super("Store Interface");

        // Initialize data structures
        inventory = new HashMap<>();
        bag = new HashMap<>();
        total = 0;

        // Set layout
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Add title label
        titleLabel = new JLabel("Welcome to the store!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(titleLabel, c);

        // Add time label and text field
        timeLabel = new JLabel("Shop Open {8 - 20}:");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        c.gridx = 0;
        c.gridy = 1;
        add(timeLabel, c);

        // Add name label and text field
        nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        add(nameLabel, c);
        nameTextField = new JTextField(20);
        c.gridx = 1;
        c.gridy = 2;
        add(nameTextField, c);

        // Add inventory label and text area
        inventoryLabel = new JLabel("Available stocks:");
        inventoryLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.gridx = 0;
        c.gridy = 3;
        add(inventoryLabel, c);
        inventoryTextArea = new JTextArea(10, 30);
        inventoryTextArea.setEditable(false);
        inventoryTextArea.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 16)); // Set font to monospaced for alignment
        String inventoryText = "";
        for (String itemName : inventory.keySet()) {
            double itemPrice = inventory.get(itemName);
            inventoryText += itemName;
            inventoryText += "\t:\t";
            inventoryText += itemPrice;
        }
        inventoryTextArea.setText(inventoryText);
        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTextArea);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 6;
        add(inventoryScrollPane, c);

        // Add mouse listener to inventory text area
        inventoryTextArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int start = inventoryTextArea.getSelectionStart();
                int end = inventoryTextArea.getSelectionEnd();
                if (start == end) {
                    int caretPos = inventoryTextArea.getCaretPosition();
                    int rowNum = inventoryTextArea.getDocument().getDefaultRootElement().getElementIndex(caretPos);
                    String itemName = "";
                    double itemPrice = 0.0;
                    int count = 0;
                    for (String name : inventory.keySet()) {
                        if (count == rowNum) {
                            itemName = name;
                            itemPrice = inventory.get(name);
                            break;
                        }
                        count++;
                    }
                    if (bag.containsKey(itemName)) {
                        bag.put(itemName, bag.get(itemName) + inventory.get(itemName));
                    } else {
                        bag.put(itemName, inventory.get(itemName));
                    }
                    total += itemPrice;
                    
                    updateBagText();
                    
                    checkoutTextField.setText("$" + String.format("%.2f", total));
                    inventoryTextArea.setSelectionStart(inventoryTextArea.getText().indexOf(itemName));
                    inventoryTextArea.setSelectionEnd(inventoryTextArea.getText().indexOf(itemName) + itemName.length());
                }
            }
        });
    
        // Add bag label and text area
        bagLabel = new JLabel("Your bag:");
        bagLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        add(bagLabel, c);
        bagTextArea = new JTextArea(10, 30);
        bagTextArea.setEditable(false);
        JScrollPane bagScrollPane = new JScrollPane(bagTextArea);
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        add(bagScrollPane, c);

        // Add total label and text field
        totalLabel = new JLabel("Total:");
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 1;
        add(totalLabel, c);
        checkoutTextField = new JTextField(10);
        checkoutTextField.setEditable(false);
        c.gridx = 1;
        c.gridy = 7;
        add(checkoutTextField, c);

        // Add buy button
        searchButton = new JButton("Buy item");
        searchButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 1;
        add(searchButton, c);

        // Add checkout button
        stopButton = new JButton("Checkout");
        stopButton.addActionListener(this);
        c.gridx = 1;
        c.gridy = 8;
        add(stopButton, c);

        // Load inventory data from file
        try {
            File inventoryFile = new File("items.txt");
            Scanner scanner = new Scanner(inventoryFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String name = parts[0];
                double price = Double.parseDouble(parts[1]);
                inventory.put(name, price);
            }
            scanner.close();
            updateInventoryText();
        } catch (FileNotFoundException e) {
            System.out.println("Inventory file not found!");
        }

        // Set the frame to full screen
        setExtendedState(MAXIMIZED_BOTH);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String itemName = JOptionPane.showInputDialog(this, "Enter the name of the item:");
            if (inventory.containsKey(itemName)) {
                double price = inventory.get(itemName);
                if (bag.containsKey(itemName)) {
                    bag.put(itemName, bag.get(itemName) + price);
                } else {
                    bag.put(itemName, price);
                }
                total += price;
                updateBagText();
                updateTotalText();
            } else {
                JOptionPane.showMessageDialog(this, "Item not found!");
            }
        } else if (e.getSource() == stopButton) {
            String name = nameTextField.getText();
            String message = "Okay, your receipt is printed to receipt.txt\n" + "Thank you for shopping with us";
            JOptionPane.showMessageDialog(this, message);
            try {
                File receipt = new File("receipt.txt");
                receipt.createNewFile();
                java.io.PrintWriter writer = new java.io.PrintWriter(receipt);
                writer.println("Receipt for " + name + ":");
                for (String key : bag.keySet()) {
                    if (key.length() > 7) {
                        writer.println(key + "\t\t$" + inventory.get(key));
                    } else {
                        writer.println(key + "\t\t\t$" + inventory.get(key));
                    }
                }
                writer.println("Total:\t$" + String.format("%.2f", total));
                writer.close();
            } catch (java.io.IOException error) {
                System.out.println("Error writing to receipt.txt");
            }
            System.exit(0);
        }
    }

    private void updateInventoryText() {
        StringBuilder sb = new StringBuilder();
        for (String name : inventory.keySet()) {
            sb.append(name).append(": $").append(inventory.get(name)).append("\n");
        }
        inventoryTextArea.setText(sb.toString());
    }

    private void updateBagText() {
        StringBuilder sb = new StringBuilder();
        for (String name : bag.keySet()) {
            sb.append(name).append(": $").append(bag.get(name)).append("\n");
        }
        bagTextArea.setText(sb.toString());
    }

    private void updateTotalText() {
        checkoutTextField.setText("$" + String.format("%.2f", total));
    }

    public static void main(String[] args) {
        new StoreInterface();
    }
}
