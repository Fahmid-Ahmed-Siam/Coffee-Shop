package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import calculator.TotalCalculator;

public class CoffeeShop extends JFrame implements MouseListener, ActionListener {
    private JPanel panel;
    private JLabel[] imageLabels, priceLabels, quantityLabels;
    private JComboBox[] quantityBoxes;
    private JButton saveButton, confirmButton;
    private JLabel totalLabel;
    private JTextArea orderHistoryArea;
    private ImageIcon[] icons;
    private String[] productNames = {"Espresso", "Americano", "Latte", "Cappuccino", "Mocha"};
    private int[] prices = {300, 150, 250, 180, 350};
    private TotalCalculator totalCalculator;
    private boolean isSaved;

    public CoffeeShop() {
        super("Coffee Shop");
        setBounds(70, 70, 1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        totalCalculator = new TotalCalculator(prices);
        isSaved = false;
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(0xDDBDA4));
        add(panel);

        imageLabels = new JLabel[5];
        priceLabels = new JLabel[5];
        quantityLabels = new JLabel[5];
        quantityBoxes = new JComboBox[5];
        icons = new ImageIcon[5];

        for (int i = 0; i < 5; i++) {
            int x = (int) (98 + (195 * i));
            icons[i] = new ImageIcon("Pic/P" + (i+1) + ".jpg");
            JPanel imagePanel = new JPanel();
            imagePanel.setBounds(x, 50, 148, 250);
            imagePanel.setBackground(new Color(0xDDBDA4));
            imageLabels[i] = new JLabel(icons[i]);
            imageLabels[i].setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(imageLabels[i]);
            panel.add(imagePanel);
            
            JPanel pricePanel = new JPanel();
            pricePanel.setBounds(x, 310, 148, 25);
            pricePanel.setBackground(new Color(133, 68, 66));
            pricePanel.setForeground(Color.WHITE);
            priceLabels[i] = new JLabel(productNames[i] + " - Price: " + prices[i]);
            priceLabels[i].setForeground(Color.WHITE);
            pricePanel.add(priceLabels[i]);
            panel.add(pricePanel);
            
            quantityLabels[i] = new JLabel("Quantity");
            quantityLabels[i].setBounds(x, 340, 80, 25);
            quantityLabels[i].setBackground(new Color(133, 68, 66));
            quantityLabels[i].setForeground(Color.WHITE);
            quantityLabels[i].setOpaque(true);
            panel.add(quantityLabels[i]);
            
            quantityBoxes[i] = new JComboBox();
            for (int j = 0; j <= 10; j++) {
                quantityBoxes[i].addItem(j);
            }
            quantityBoxes[i].setBounds(x + 89, 340, 47, 25);
            quantityBoxes[i].setBackground(new Color(133, 68, 66));
            quantityBoxes[i].setForeground(Color.WHITE);
            quantityBoxes[i].setOpaque(true);
            panel.add(quantityBoxes[i]);
        }

        saveButton = new JButton("Save");
        saveButton.setBounds(477, 370, 80, 30); 
        saveButton.setBackground(new Color(133, 68, 66));
        saveButton.setForeground(Color.WHITE);
        saveButton.addMouseListener(this);
        panel.add(saveButton);

        confirmButton = new JButton("Confirm");
        confirmButton.setBounds(563, 370, 100, 30);
        confirmButton.setBackground(new Color(133, 68, 66));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setEnabled(false);
        confirmButton.addMouseListener(this);
        panel.add(confirmButton);

        totalLabel = new JLabel("Total Price : 0");
        totalLabel.setBounds(495, 410, 89, 30);
        totalLabel.setBackground(new Color(133, 68, 66));
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setOpaque(true);
        panel.add(totalLabel);

        orderHistoryArea = new JTextArea();
        orderHistoryArea.setBackground(new Color(176, 130, 103));
        orderHistoryArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(orderHistoryArea);
        scrollPane.setBounds(355, 450, 367, 150);
        panel.add(scrollPane);
        
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        try {
            File file = new File("./Data/user_data.txt");
            if (file.exists()) {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;
                orderHistoryArea.setText("");
                while ((line = br.readLine()) != null) {
                    orderHistoryArea.append(line + "\n");
                }
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mouseClicked(MouseEvent me) { 
        if (me.getSource() == saveButton) {
            int[] quantities = new int[5];
            for (int i = 0; i < 5; i++) {
                quantities[i] = (int) quantityBoxes[i].getSelectedItem();
            }
            int totalPrice = totalCalculator.calculateTotal(quantities);
            totalLabel.setText("Total Price : " + totalPrice);
            isSaved = true;
            confirmButton.setEnabled(true);
        }
		
        if (me.getSource() == confirmButton) {
            if (!confirmButton.isEnabled()) {
                JOptionPane.showMessageDialog(this, "Order was not saved!", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            } else {
                try {
                    File file = new File("./Data/user_data.txt");
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }

                    FileWriter fw = new FileWriter(file, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw);

                    LocalDateTime myDateObj = LocalDateTime.now();
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm a, dd/MM/yyyy");
                    String timeAndDate = myDateObj.format(myFormatObj);

                    pw.println("Order Date and Time: " + timeAndDate);
                    pw.println("===============================================");

                    int totalPrice = 0;
                    
                    for (int i = 0; i < 5; i++) {
                        int quantity = (int) quantityBoxes[i].getSelectedItem();
                        if (quantity > 0) {
                            int subtotal = quantity * prices[i];
                            totalPrice += subtotal;
                            pw.println(productNames[i] + " - Quantity: " + quantity + " - Price per unit: " + prices[i] + " - Subtotal: " + subtotal);
                        }
                    }
                    
                    pw.println("-----------------------------------------------");
                    pw.println("Total Price: " + totalPrice);
                    pw.println("===============================================\n");
                    pw.close();

                    for (int i = 0; i < 5; i++) {
                        quantityBoxes[i].setSelectedIndex(0);
                    }

                    JOptionPane.showMessageDialog(this, "Order saved successfully!", "Confirmation", JOptionPane.INFORMATION_MESSAGE);
                    
                    totalLabel.setText("Total Price : 0");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error saving order. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                loadOrderHistory();
                isSaved = false;
                confirmButton.setEnabled(false);
            }
        }
    }
    
    public void mousePressed(MouseEvent me) {}
    public void mouseReleased(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}
    public void actionPerformed(ActionEvent me) {}
}