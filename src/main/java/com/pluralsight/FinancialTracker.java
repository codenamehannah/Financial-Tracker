package com.pluralsight;

    import java.io.*;
    import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

    public class FinancialTracker {

        private static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        private static final String FILE_NAME = "transactions.csv";
        private static final String DATE_FORMAT = "yyyy-MM-dd";
        private static final String TIME_FORMAT = "HH:mm:ss";
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

        public static void main(String[] args) {
            loadTransactions(FILE_NAME);
            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("Welcome to TransactionApp");
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
                    if (parts.length == 4) {
                        LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);
                        LocalTime time = LocalTime.parse(parts[1], TIME_FORMATTER);
                        String description = parts[2];
                        String vendor = parts[3];
                        double amount = Double.parseDouble(parts[4]);
                        transactions.add(new Transaction(date, time, description, vendor, amount));
                    }
                }

                reader.close();
            } catch (IOException e) {
                System.err.println("Error loading transactions: " + e.getMessage());
            }
        }



        private static void addDeposit(Scanner scanner) {

            System.out.print("Enter deposit date (yyyy-MM-dd HH:mm:ss): ");
            String dateStr = scanner.nextLine().trim();
            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();
            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine().trim();
            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            LocalDate date = LocalDate.parse(dateStr.split(" ")[0], DATE_FORMATTER);
            LocalTime time = LocalTime.parse(dateStr.split(" ")[1], TIME_FORMATTER);

            transactions.add(new Transaction(date, time, description, vendor, amount));
            System.out.println("Deposit added.");
            saveTransactions(FILE_NAME);


        }

        private static void addPayment(Scanner scanner) {
            System.out.print("Enter payment date (yyyy-MM-dd HH:mm:ss): ");
            String dateStr = scanner.nextLine().trim();
            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();
            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine().trim();
            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            LocalDate date = LocalDate.parse(dateStr.split(" ")[0], DATE_FORMATTER);
            LocalTime time = LocalTime.parse(dateStr.split(" ")[1], TIME_FORMATTER);

            transactions.add(new Transaction(date, time, description, vendor, -amount)); // Note the negative amount for payments
            System.out.println("Payment added.");
            saveTransactions(FILE_NAME); // save transactions after adding a payment
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
                        displayLedger();
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
                    default:
                        System.out.println("Invalid option");
                        break;
                }
            }
        }

        private static void displayLedger() {
            // This method should display a table of all transactions in the `transactions` ArrayList.
            // The table should have columns for date, time, vendor, type, and amount.
            Collections.sort(transactions);
            System.out.println("Ledger (All Entries)");
            System.out.println("Date | Time | Description | Vendor | Amount");

            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }

        private static void displayDeposits() {
            // This method should display a table of all deposits in the `transactions` ArrayList.
            // The table should have columns for date, time, vendor, and amount.
            System.out.println("Deposits");
            System.out.println("Date | Time | Description | Vendor | Amount");

            for (Transaction transaction : transactions) {
                if (transaction instanceof Deposit) {
                    System.out.println(transaction);
                }
            }
        }

        private static void displayPayments() {
            // This method should display a table of all payments in the `transactions` ArrayList.
            // The table should have columns for date, time, vendor, and amount.
            System.out.println("Payments");
            System.out.println("Date | Time | Description | Vendor | Amount");

            for (Transaction transaction : transactions) {
                if (transaction instanceof Payment) {
                    System.out.println(transaction);
                }
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
                        // Generate a report for all transactions within the current month,
                        // including the date, vendor, and amount for each transaction.
                        generateReport(LocalDate.now().withDayOfMonth(1), LocalDate.now());
                        break;
                    case "2":
                        // Generate a report for all transactions within the previous month,
                        // including the date, vendor, and amount for each transaction.
                        generateReport(LocalDate.now().minusMonths(1).withDayOfMonth(1), LocalDate.now().withDayOfMonth(1).minusDays(1));
                    case "3":
                        // Generate a report for all transactions within the current year,
                        // including the date, vendor, and amount for each transaction.
                        generateReport(LocalDate.now().withDayOfYear(1), LocalDate.now());
                        break;

                    case "4":
                        // Generate a report for all transactions within the previous year,
                        // including the date, vendor, and amount for each transaction.
                        generateReport(LocalDate.now().minusYears(1).withDayOfYear(1), LocalDate.now().withDayOfYear(1).minusDays(1));
                        break;
                    case "5":
                        // Prompt the user to enter a vendor name, then generate a report for all transactions
                        // with that vendor, including the date, vendor, and amount for each transaction.
                        System.out.print("Enter vendor name: ");
                        String vendorName = scanner.nextLine().trim();
                        searchByVendor(vendorName);
                        break;
                    case "0":
                        running = false;
                    default:
                        System.out.println("Invalid option");
                        break;
                }
            }
        }
        private static void generateReport(LocalDate startDate, LocalDate endDate) {
            System.out.println("Report");
            System.out.println("Date | Vendor | Amount");

            for (Transaction transaction : transactions){
                LocalDate transactionDate = transaction.getDate();
                if (transactionDate.isEqual(startDate) || transactionDate.isEqual(endDate) ||
                        (transactionDate.isAfter(startDate) && transactionDate.isBefore(endDate))) {
                    System.out.println(transaction.toReportString());


                }
            }
        }


        private static void filterTransactionsByDate(LocalDate startDate, LocalDate endDate) {
            // This method filters the transactions by date and prints a report to the console.
            // It takes two parameters: startDate and endDate, which represent the range of dates to filter by.
            // The method loops through the transactions list and checks each transaction's date against the date range.
            // Transactions that fall within the date range are printed to the console.
            // If no transactions fall within the date range, the method prints a message indicating that there are no results.
        }

        private static void filterTransactionsByVendor(String vendor) {
            // This method filters the transactions by vendor and prints a report to the console.
            // It takes one parameter: vendor, which represents the name of the vendor to filter by.
            // The method loops through the transactions list and checks each transaction's vendor name against the specified vendor name.
            // Transactions with a matching vendor name are printed to the console.
            // If no transactions match the specified vendor name, the method prints a message indicating that there are no results.
        }
    }


