package LIBRARY_MANAGE;
import java.sql.*;
import java.util.Scanner;

public class LibrarySystem {
    static Connection con;

    public static void main(String[] args) {
        try {
            // 1. Load JDBC driver and connect to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");

            Scanner sc = new Scanner(System.in);
            int choice;

            while (true) {
                System.out.println("\n---- Library Menu ----");
                System.out.println("1. Add User");
                System.out.println("2. Add Book");
                System.out.println("3. Borrow Book");
                System.out.println("4. Return Book");
                System.out.println("5. List Borrowed Books");
                System.out.println("6. View Transaction Log");
                System.out.println("7. Exit");
                System.out.print("Enter choice: ");
                choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        addUser(sc);
                        break;
                    case 2:
                        addBook(sc);
                        break;
                    case 3:
                        borrowBook(sc);
                        break;
                    case 4:
                        returnBook(sc);
                        break;
                    case 5:
                        listBorrowedBooks();
                        break;
                    case 6:
                        viewTransactions();
                        break;
                    case 7:
                        System.out.println("Exiting...");
                        con.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to add user
    static void addUser(Scanner sc) throws SQLException {
        System.out.print("Enter user name: ");
        String name = sc.nextLine();

        PreparedStatement ps = con.prepareStatement("INSERT INTO users(name) VALUES(?)");
        ps.setString(1, name);
        ps.executeUpdate();
        System.out.println("User added successfully.");
    }

    // Method to add book
    static void addBook(Scanner sc) throws SQLException {
        System.out.print("Enter book title: ");
        String title = sc.nextLine();
        System.out.print("Enter author: ");
        String author = sc.nextLine();

        PreparedStatement ps = con.prepareStatement("INSERT INTO books(title, author) VALUES(?, ?)");
        ps.setString(1, title);
        ps.setString(2, author);
        ps.executeUpdate();
        System.out.println("Book added successfully.");
    }

    // Method to borrow book
    static void borrowBook(Scanner sc) throws SQLException {
        System.out.print("Enter user ID: ");
        int uid = sc.nextInt();
        System.out.print("Enter book ID: ");
        int bid = sc.nextInt();

        // Check if book is available
        PreparedStatement check = con.prepareStatement("SELECT is_borrowed FROM books WHERE book_id = ?");
        check.setInt(1, bid);
        ResultSet rs = check.executeQuery();

        if (rs.next() && !rs.getBoolean("is_borrowed")) {
            PreparedStatement borrow = con.prepareStatement("UPDATE books SET is_borrowed = TRUE WHERE book_id = ?");
            borrow.setInt(1, bid);
            borrow.executeUpdate();

            PreparedStatement log = con.prepareStatement("INSERT INTO transactions(user_id, book_id, action) VALUES(?, ?, 'borrow')");
            log.setInt(1, uid);
            log.setInt(2, bid);
            log.executeUpdate();

            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Book is already borrowed or does not exist.");
        }
    }

    // Method to return book
    static void returnBook(Scanner sc) throws SQLException {
        System.out.print("Enter user ID: ");
        int uid = sc.nextInt();
        System.out.print("Enter book ID: ");
        int bid = sc.nextInt();

        PreparedStatement returnBook = con.prepareStatement("UPDATE books SET is_borrowed = FALSE WHERE book_id = ?");
        returnBook.setInt(1, bid);
        returnBook.executeUpdate();

        PreparedStatement log = con.prepareStatement("INSERT INTO transactions(user_id, book_id, action) VALUES(?, ?, 'return')");
        log.setInt(1, uid);
        log.setInt(2, bid);
        log.executeUpdate();

        System.out.println("Book returned successfully.");
    }

    // Method to list borrowed books
    static void listBorrowedBooks() throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM books WHERE is_borrowed = TRUE");

        System.out.println("\nBorrowed Books:");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("book_id") +
                               ", Title: " + rs.getString("title") +
                               ", Author: " + rs.getString("author"));
        }
    }

    // Method to view transactions
    static void viewTransactions() throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM transactions");

        System.out.println("\nTransaction Log:");
        while (rs.next()) {
            System.out.println("TransID: " + rs.getInt("trans_id") +
                               ", UserID: " + rs.getInt("user_id") +
                               ", BookID: " + rs.getInt("book_id") +
                               ", Action: " + rs.getString("action") +
                               ", Date: " + rs.getTimestamp("trans_date"));
        }
    }
}
