import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import java.awt.Font;

public class test2 {

    private JFrame frame;
    private JComboBox<String> comboBoxName;
    private JComboBox<String> comboBoxMonth;
    private JLabel labelSalary;
    private JButton btnNewButton;
    private JLabel lblNewLabel;
    private JButton btnMostDisciplined;
    private JButton btnForeverlateAward;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    test2 window = new test2();
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
    public test2() {
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

        // Fetch unique names from the database
        String[] names = getUniqueNames().toArray(new String[0]);
        comboBoxName = new JComboBox<String>(names);
        comboBoxName.setFont(new Font("Tahoma", Font.PLAIN, 12));
        comboBoxName.setBounds(85, 66, 257, 27);
        frame.getContentPane().add(comboBoxName);

        // Month selection combo box
        String[] months = { "2024-10", "2024-11", "2024-12", "2025-01", "2025-02", "2025-03", "2025-04", "2025-05", "2025-06", "2025-07", "2025-08", "2025-09", "2025-10", "2025-11", "2025-12" };
        comboBoxMonth = new JComboBox<String>(months);
        comboBoxMonth.setFont(new Font("Tahoma", Font.PLAIN, 12));
        comboBoxMonth.setBounds(85, 115, 257, 27);
        frame.getContentPane().add(comboBoxMonth);

        labelSalary = new JLabel("Total salary: $");
        labelSalary.setBounds(100, 180, 200, 20);
        frame.getContentPane().add(labelSalary);
        
        btnNewButton = new JButton("Late Staff List");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		test6 window = new test6();
        		window.showWindow();
        	}
        });
        btnNewButton.setBounds(10, 226, 102, 27);
        frame.getContentPane().add(btnNewButton);
        
        lblNewLabel = new JLabel("STAFF'S MONTHLY SALARIES");
        lblNewLabel.setFont(new Font("Perpetua", Font.BOLD, 17));
        lblNewLabel.setBounds(102, 10, 284, 20);
        frame.getContentPane().add(lblNewLabel);
        
        btnMostDisciplined = new JButton("Most Disciplined");
        btnMostDisciplined.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db";
        	    String sql = "SELECT Name, OnTimeCount\r\n"
        	            + "FROM (\r\n"
        	            + "  SELECT Name, COUNT(Status) AS OnTimeCount\r\n"
        	            + "  FROM Staff\r\n"
        	            + "  WHERE Status = 'On time'\r\n"
        	            + "  GROUP BY Name\r\n"
        	            + ") AS subquery\r\n"
        	            + "WHERE OnTimeCount = (\r\n"
        	            + "  SELECT MAX(OnTimeCount)\r\n"
        	            + "  FROM (\r\n"
        	            + "    SELECT Name, COUNT(Status) AS OnTimeCount\r\n"
        	            + "    FROM Staff\r\n"
        	            + "    WHERE Status = 'On time'\r\n"
        	            + "    GROUP BY Name\r\n"
        	            + "  ) AS subquery\r\n"
        	            + ");";

        	    try (Connection conn = DriverManager.getConnection(url);
        	         Statement stmt = conn.createStatement();
        	         ResultSet rs = stmt.executeQuery(sql)) {

        	        if (rs.next()) {
        	            String staffName = rs.getString("Name");
        	            JOptionPane.showMessageDialog(null, "Congrats! The Most Disciplined Staff Award Goes To : " + staffName);
        	        } else {
        	            JOptionPane.showMessageDialog(null, "No staff found with 'On time' status.");
        	        }

        	    } catch (SQLException e1) {
        	        System.out.println(e1.getMessage());
        	    }
        	}
        });
        btnMostDisciplined.setBounds(142, 226, 129, 27);
        frame.getContentPane().add(btnMostDisciplined);
        
        btnForeverlateAward = new JButton("Forever-Late Award");
        btnForeverlateAward.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db";
        	    String sql = "SELECT Name, OnTimeCount\r\n"
        	            + "FROM (\r\n"
        	            + "  SELECT Name, COUNT(Status) AS OnTimeCount\r\n"
        	            + "  FROM Staff\r\n"
        	            + "  WHERE Status = 'Late'\r\n"
        	            + "  GROUP BY Name\r\n"
        	            + ") AS subquery\r\n"
        	            + "WHERE OnTimeCount = (\r\n"
        	            + "  SELECT MAX(OnTimeCount)\r\n"
        	            + "  FROM (\r\n"
        	            + "    SELECT Name, COUNT(Status) AS OnTimeCount\r\n"
        	            + "    FROM Staff\r\n"
        	            + "    WHERE Status = 'Late'\r\n"
        	            + "    GROUP BY Name\r\n"
        	            + "  ) AS subquery\r\n"
        	            + ");";

        	    try (Connection conn = DriverManager.getConnection(url);
        	         Statement stmt = conn.createStatement();
        	         ResultSet rs = stmt.executeQuery(sql)) {

        	        if (rs.next()) {
        	            String staffName = rs.getString("Name");
        	            JOptionPane.showMessageDialog(null, "Congrats! The Forever-Late Award goes to : " + staffName);
        	        } else {
        	            JOptionPane.showMessageDialog(null, "No staff found with 'On time' status.");
        	        }

        	    } catch (SQLException e1) {
        	        System.out.println(e1.getMessage());
        	    }
        	}
        });
        btnForeverlateAward.setBounds(301, 226, 125, 27);
        frame.getContentPane().add(btnForeverlateAward);

        // ActionListener for when both name and month are selected
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedName = (String) comboBoxName.getSelectedItem();
                String selectedMonth = (String) comboBoxMonth.getSelectedItem();
                if (selectedName != null && selectedMonth != null) {
                    double totalSalary = getTotalSalaryForMonth(selectedName, selectedMonth);
                    labelSalary.setText("Total salary: RM" + String.format("%.2f", totalSalary));
                }
            }
        };

        comboBoxName.addActionListener(listener);
        comboBoxMonth.addActionListener(listener);
    }

    // Method to fetch unique names from the SQLite database
    private List<String> getUniqueNames() {
    	String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db";
    	String sql = "SELECT DISTINCT name FROM Staff";    // SQL to select unique names

        List<String> uniqueNames = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Add each unique name from the result set to the list
            while (rs.next()) {
                uniqueNames.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return uniqueNames;  // Return the list of unique names
    }

    // Method to calculate total salary for the selected staff and month
    private double getTotalSalaryForMonth(String name, String month) {
    	//String url = "jdbc:sqlite:C:\\Users\\Acer\\Documents\\Drip.OhStaff.db";  // Replace with your SQLite file path
    	java.net.URL url = getClass().getClassLoader().getResource("Drip.OhStaff.db");
    	String path = url.getFile();
    	String jdbcUrl = "jdbc:sqlite:" + path;
    	String sql = "SELECT SUM(salary) AS TotalSalary " +
                     "FROM Staff " +
                     "WHERE name = '" + name + "' " +
                     "AND strftime('%Y-%m', date) = '" + month + "'";  // Filter by selected month

        double totalSalary = 0.0;

        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                totalSalary = rs.getDouble("TotalSalary");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return totalSalary;  // Return total salary for the selected month
    }
    
    public void showWindow() {
        frame.setVisible(true);  // Display the frame when called
    }
}
