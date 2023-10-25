package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class FinancialTracker {

    //ArrayList to store Transactions
    private static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);


//Entry Point of the Application
    public static void main(String[] args) {
        //For loading Transactions from CSv File
        loadTransactions(FILE_NAME);
        //This scanner is for user input
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to Stuart's Financial Services");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    addPayment(scanner);
                    break;
                case "L":
                    ledgerMenu(scanner);
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }

        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);
                    LocalTime time = LocalTime.parse(parts[1], TIME_FORMATTER);
                    String description = parts[2].trim();
                    String vendor = parts[3].trim();
                    double amount = Double.parseDouble(parts[4]);
                    Transaction transaction = new Transaction(date, time, description, vendor, amount);
                    transactions.add(transaction);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    private static void addDeposit(Scanner scanner) {
        //Asks for deposit information in this section.
        System.out.println("Enter deposit information:");
        LocalDate date = askForDate(scanner);
        LocalTime time = askForTime(scanner);
        System.out.print("Enter the name of the Vendor: ");
        String vendor = scanner.nextLine().trim();
        double amount = askForAmount(scanner);

        Transaction deposit = new Transaction(date, time, "Deposit", vendor, amount);
        transactions.add(deposit);
        saveTransactions(FILE_NAME);
    }

    private static void addPayment(Scanner scanner) {
        System.out.println("Please enter the payment information:");
        LocalDate date = askForDate(scanner);
        LocalTime time = askForTime(scanner);
        System.out.print("Please enter the name of the Vendor:: ");
        String vendor = scanner.nextLine().trim();
        double amount = -askForAmount(scanner);

        Transaction payment = new Transaction(date, time, "Payment", vendor, amount);
        transactions.add(payment);
        saveTransactions(FILE_NAME);
    }

    private static LocalDate askForDate(Scanner scanner) {
        System.out.print("Date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine().trim();
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    private static LocalTime askForTime(Scanner scanner) {
        System.out.print("Time (HH:mm:ss): ");
        String timeStr = scanner.nextLine().trim();
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    private static double askForAmount(Scanner scanner) {
        double amount = 0;
        boolean validInput = false;
        while (!validInput) {
            System.out.print("Amount: ");
            String amountStr = scanner.nextLine().trim();
            try {
                amount = Double.parseDouble(amountStr);
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid amount.");
            }
        }
        return amount;
    }

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A":
                    displayLedger(transactions);
                    break;
                case "D":
                    displayDeposits();
                    break;
                case "P":
                    displayPayments();
                    break;
                case "R":
                    reportsMenu(scanner);
                    break;
                case "H":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void displayLedger(ArrayList<Transaction> transactionsToDisplay) {
        Collections.reverse(transactionsToDisplay);
        System.out.println("Ledger:");
        for (Transaction transaction : transactionsToDisplay) {
            System.out.println(transaction);
        }
    }

    private static void displayDeposits() {
        ArrayList<Transaction> deposits = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                deposits.add(transaction);
            }
        }
        Collections.reverse(deposits);
        System.out.println("Deposits:");
        for (Transaction deposit : deposits) {
            System.out.println(deposit);
        }
    }

    private static void displayPayments() {
        ArrayList<Transaction> payments = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                payments.add(transaction);
            }
        }
        Collections.reverse(payments);
        System.out.println("Payments:");
        for (Transaction payment : payments) {
            System.out.println(payment);
        }
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    generateReport(LocalDate.now().withDayOfMonth(1), LocalDate.now());
                    break;
                case "2":
                    LocalDate firstDayOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate lastDayOfPreviousMonth = LocalDate.now().withDayOfMonth(1).minusDays(1);
                    generateReport(firstDayOfPreviousMonth, lastDayOfPreviousMonth);
                    break;
                case "3":
                    generateReport(LocalDate.now().withDayOfYear(1), LocalDate.now());
                    break;
                case "4":
                    LocalDate firstDayOfPreviousYear = LocalDate.now().minusYears(1).withDayOfYear(1);
                    LocalDate lastDayOfPreviousYear = LocalDate.now().withDayOfYear(1).minusDays(1);
                    generateReport(firstDayOfPreviousYear, lastDayOfPreviousYear);
                    break;
                case "5":
                    System.out.print("Enter Vendor Name: ");
                    String vendorName = scanner.nextLine().trim();
                    filterTransactionsByVendor(vendorName);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void generateReport(LocalDate startDate, LocalDate endDate) {
        System.out.println("Report from " + startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER) + ":");
        for (Transaction transaction : transactions) {
            if (transaction.getDate().isAfter(startDate) && transaction.getDate().isBefore(endDate)) {
                System.out.println(transaction);
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                if (!found) {
                    System.out.println("Transactions for vendor '" + vendor + "':");
                    found = true;
                }
                System.out.println(transaction);
            }
        }
        if (!found) {
            System.out.println("No transactions found for vendor '" + vendor + "'.");
        }
    }

    private static void saveTransactions(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true));
            for (Transaction transaction : transactions) {
                writer.write(transaction.toCsv());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }
}


