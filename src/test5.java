import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class test5 {

    private JFrame frame;
    private JTable table;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    test5 window = new test5();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public test5() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // Create a table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Name");
        model.addColumn("Position");

        // Connect to the database and retrieve staff data
        String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db"; // Replace with your database file path
        /*java.net.URL url = getClass().getClassLoader().getResource("Drip.OhStaff.db");
    	String path = url.getFile();
    	String jdbcUrl = "jdbc:sqlite:" + path;*/
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Name, Position FROM StaffInfo")) {

            // Add data to the table model
            while (rs.next()) {
                model.addRow(new Object[] { rs.getString("Name"), rs.getString("Position") });
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Create a table and add it to a scroll pane
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 10, 424, 261);
        frame.getContentPane().add(scrollPane);
    }
    
    public void showWindow() {
        frame.setVisible(true);  // Display the frame when called
    }
}