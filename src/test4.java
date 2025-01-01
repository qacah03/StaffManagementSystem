import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class test4 {

	private JFrame frame;
	private JTextField txtName;
	private JTextField txtRate;
	private JComboBox<String> comboBoxPosition;
	private JLabel lblNewLabel;
	private JButton btnNewButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test4 window = new test4();
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
	public test4() {
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
		
		JLabel lblName = new JLabel("Name : ");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblName.setBounds(10, 70, 45, 13);
		frame.getContentPane().add(lblName);
		
		JLabel lblSalaryRatePer = new JLabel("Salary Rate per Hour :  ");
		lblSalaryRatePer.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSalaryRatePer.setBounds(10, 99, 153, 13);
		frame.getContentPane().add(lblSalaryRatePer);
		
		JLabel lblPosition = new JLabel("Position : ");
		lblPosition.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPosition.setBounds(10, 132, 153, 13);
		frame.getContentPane().add(lblPosition);
		
		txtName = new JTextField();
		txtName.setBounds(67, 68, 330, 19);
		frame.getContentPane().add(txtName);
		txtName.setColumns(10);
		
		txtRate = new JTextField();
		txtRate.setBounds(154, 97, 96, 19);
		frame.getContentPane().add(txtRate);
		txtRate.setColumns(10);
		
		String[] position = {"Kitchen", "Floor"};
        comboBoxPosition = new JComboBox<String>(position);
		comboBoxPosition.setBounds(83, 129, 167, 21);
		frame.getContentPane().add(comboBoxPosition);
		
		lblNewLabel = new JLabel("DRIP.OH STAFF'S REGISTRATION");
		lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 18));
		lblNewLabel.setBounds(41, 0, 373, 33);
		frame.getContentPane().add(lblNewLabel);
		
		btnNewButton = new JButton("REGISTER");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db";   // Replace with your database file path

				/*java.net.URL url = getClass().getClassLoader().getResource("Drip.OhStaff.db");
		    	String path = url.getFile();
		    	String jdbcUrl = "jdbc:sqlite:" + path;*/
				// SQL query to insert data into the Staff table using placeholders (?)
                String sql = "INSERT INTO StaffInfo(Name, Rate, Position) VALUES(?, ?, ?)";

                try (Connection conn = DriverManager.getConnection(url);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                	String name = txtName.getText();
                	String rate = txtRate.getText();
                	double Rate = Double.parseDouble(rate);
                	String position = (String) comboBoxPosition.getSelectedItem();
                    // Set the values for the placeholders
                    pstmt.setString(1, name);          // Set the value for StaffID
                    pstmt.setDouble(2, Rate);          // Set the value for Name
                    pstmt.setString(3, position);    // Set the value for HourlyRate

                    // Execute the insert statement
                    pstmt.executeUpdate();
                    System.out.println("Staff data inserted successfully.");
                    JOptionPane.showMessageDialog(frame, "New Staff has been registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e1) {
                    System.out.println(e1.getMessage());
                }
			}
		});
		btnNewButton.setBounds(165, 180, 85, 21);
		frame.getContentPane().add(btnNewButton);
	}
	
	public void showWindow() {
        frame.setVisible(true);  // Display the frame when called
    }
}
