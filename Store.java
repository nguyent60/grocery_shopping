import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Store {
    static HashMap<String, Double> inventory = new HashMap<>();
    static HashMap<String, Double> bag = new HashMap<>();
    static double total = 0;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Store’s open time: 8 - 20. What time is it?");
        int time = scanner.nextInt();

        if (time >= 8 && time < 20) {
            System.out.println("Welcome to the store");

            System.out.println("What is your name?");
            String name = scanner.next();
            System.out.println("Okay " + name + ", here are the available stocks: ");

            File file = new File("items.txt");
            try {
                Scanner fileScanner = new Scanner(file);
                while (fileScanner.hasNextLine()) {
                    String[] line = fileScanner.nextLine().split(", ");
                    String item = line[0];
                    Double price = Double.parseDouble(line[1]);
                    inventory.put(item, price);
                    if (item.length() > 7) {
                        System.out.println("item : " + item + ",\t price : " + price);
                    }
                    else {
                        System.out.println("item : " + item + ",\t\t price : " + price);
                    }
                }
                fileScanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            }
            System.out.println("Your option");
            System.out.println("[N]: Search by name");
            System.out.println("[Stop]: Finish shopping");
            String option = scanner.next();

            while (!option.equals("stop".toLowerCase())) {
                if (option.equals("N".toLowerCase())) {
                    searchItem(inventory);
                    break;
                } else {
                    System.out.println("Invalid input. Please try again");
                    System.out.println("[N]: Search by name");
                    System.out.println("[Stop]: Finish shopping");
                    option = scanner.next();
                }
            }

            // write receipt to file
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
                writer.println("Total:\t$" + total);
                writer.close();
                System.out.println("Okay, your receipt is printed to receipt.txt");
            } catch (java.io.IOException e) {
                System.out.println("Error writing to receipt.txt");
            }
            System.out.println("Thank you for shopping with us.");
        } else {
            System.out.println("Sorry we are closed");
        }
        scanner.close();
    }

    public static void searchItem(HashMap<String, Double> inventory) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("What would you like to get?");
        System.out.println("| [stop] to finish");
        String input = scanner.next().toLowerCase();
        
        List<String> items = new ArrayList<>(inventory.keySet()); // Store keys in a list
        
        while (!input.equals("stop")) {
            boolean found = false;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).toLowerCase().contains(input.toLowerCase())) {
                    System.out.println("You mean " + items.get(i) + "? (Y/N)");
                    String choice = scanner.next().toLowerCase();
                    if (choice.equals("y")) {
                        System.out.print("Okay, you got " + items.get(i) + ". What else?");
                        System.out.println("| [stop] to finish");
                        bag.put(items.get(i), inventory.get(items.get(i)));
                        found = true;
                        total += inventory.get(items.get(i));
                        break;
                    }else if (choice.equals("n")) {
                        continue;
                    }
                    else {
                        break;
                    }
                }
            }
            if (!found) {
                System.out.println("Sorry, we don't have that. Please choose something else.");
            }
            input = scanner.next().toLowerCase();
        }
        scanner.close();
    }
}