import java.awt.EventQueue;

import javax.print.DocFlavor.URL;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class test6 {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    test6 window = new test6();
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
    public test6() {
        initialize();
        displayLateStaff();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        model = new DefaultTableModel();
        model.addColumn("Staff Name");
        model.addColumn("Date");

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 11, 414, 239);
        frame.getContentPane().add(scrollPane);
    }

    private void displayLateStaff() {
    	/*java.net.URL url = getClass().getClassLoader().getResource("Drip.OhStaff.db");
    	String path = url.getFile();
    	String jdbcUrl = "jdbc:sqlite:" + path;*/
    	String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db"; // Replace with your database file path

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Name, Date FROM Staff WHERE Status = 'Late'")) {

            while (rs.next()) {
                String name = rs.getString("Name");
                String date = rs.getString("Date");
                model.addRow(new Object[] {name, date});
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void showWindow() {
        frame.setVisible(true);  // Display the frame when called
    }
}