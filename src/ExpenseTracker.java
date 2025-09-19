import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

class Expense {
    private String description;
    private double amount;
    private String category;
    private LocalDate date;

    public Expense(String description, double amount, String category, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    // Getters
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return String.format("%s | $%.2f | %s | %s", description, amount, category, date);
    }
}

public class ExpenseTracker {
    private static List<Expense> expenses = new ArrayList<>();
    private static final String FILE_NAME = "expenses.txt";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        loadExpenses();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1 -> addExpense(scanner);
                case 2 -> viewExpenses();
                case 3 -> viewSummary();
                case 4 -> deleteExpense(scanner);
                case 5 -> {
                    saveExpenses();
                    System.out.println(ANSI_GREEN + "Goodbye!" + ANSI_RESET);
                    System.exit(0);
                }
                default -> System.out.println(ANSI_RED + "Invalid choice. Try again." + ANSI_RESET);
            }
        }
    }

    private static void displayMenu() {
        System.out.println(ANSI_BLUE + "╭────────────────────────────────────────────╮" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "│          Personal Expense Tracker          │" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "│────────────────────────────────────────────│" + ANSI_RESET);
        System.out.println("│ 1. Add Expense                             │");
        System.out.println("│ 2. View All Expenses                       │");
        System.out.println("│ 3. View Summary (Total & By Category)      │");
        System.out.println("│ 4. Delete Expense                          │");
        System.out.println("│ 5. Exit                                    │");
        System.out.println(ANSI_BLUE + "╰────────────────────────────────────────────╯" + ANSI_RESET);
        System.out.print("Enter choice: ");
    }

    private static void addExpense(Scanner scanner) {
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        System.out.print("Amount: $");
        double amt = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Category: ");
        String cat = scanner.nextLine();
        LocalDate date = LocalDate.now(); // Default to today; extend for custom if needed
        expenses.add(new Expense(desc, amt, cat, date));
        System.out.println(ANSI_GREEN + "Expense added!" + ANSI_RESET);
    }

    private static void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println(ANSI_YELLOW + "No expenses yet." + ANSI_RESET);
            return;
        }
        System.out.println(ANSI_BLUE + "Expenses:" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "ID | Description          | Amount | Category     | Date" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "---+--------------------+--------+--------------+----------" + ANSI_RESET);
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            System.out.printf("%2d | %-18s | $%6.2f | %-12s | %s%n",
                    i + 1, e.getDescription(), e.getAmount(), e.getCategory(), e.getDate());
        }
    }

    private static void viewSummary() {
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        System.out.println(ANSI_RED + "Total Spent: $" + String.format("%.2f", total) + ANSI_RESET);
        Map<String, Double> byCategory = new HashMap<>();
        for (Expense e : expenses) {
            byCategory.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        System.out.println("By Category:");
        byCategory.forEach((cat, amt) -> System.out.println("  " + cat + ": $" + String.format("%.2f", amt)));
    }

    private static void deleteExpense(Scanner scanner) {
        viewExpenses();
        if (expenses.isEmpty()) return;
        System.out.print("Enter ID to delete: ");
        int id = scanner.nextInt() - 1;
        if (id >= 0 && id < expenses.size()) {
            expenses.remove(id);
            System.out.println(ANSI_GREEN + "Deleted!" + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "Invalid ID." + ANSI_RESET);
        }
    }

    private static void saveExpenses() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Save failed: " + e.getMessage() + ANSI_RESET);
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadExpenses() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            expenses = (List<Expense>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(ANSI_RED + "Load failed: " + e.getMessage() + ANSI_RESET);
            expenses = new ArrayList<>();
        }
    }
}