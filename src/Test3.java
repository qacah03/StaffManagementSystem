import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test3 {

    private JFrame frame;
    private Map<String, LocalDateTime> checkInTimes = new HashMap<>();
    private Map<String, LocalDateTime> checkOutTimes = new HashMap<>();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private DateTimeFormatter dateOnlyFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Scheduler for automatic PDF generation
    private Map<String, JCheckBox[]> customCheckboxes = new HashMap<>();
    private Map<String, Duration> breakDurations = new HashMap<>();
    
    private JCheckBox boxAzlanOut;
    private JCheckBox boxIzwanQudriOut;
    private JCheckBox boxIzwanAlifOut;
    private JCheckBox boxRajaSyediOut;
    private JCheckBox boxNazriOut;
    private JCheckBox boxAmirulFizryOut;
    private JCheckBox boxFakhrullahOut;
    
 // Method to retrieve staff names from the database
    private void retrieveStaffNames() {
        String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db"; // Replace with your database file path

    	/*java.net.URL url = getClass().getClassLoader().getResource("jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db");
    	String path = url.getFile();
    	String jdbcUrl = "jdbc:sqlite:" + path;*/
    	try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Name FROM StaffInfo")) {

            while (rs.next()) {
                String staffName = rs.getString("Name");
                createStaffCheckboxes(staffName);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void updateBreakDurationLabel(JLabel label, Duration breakDuration) {
        long hours = breakDuration.toHours();
        long minutes = breakDuration.toMinutes() % 60;

        label.setText(String.format("Total Break: %d hours %d mins", hours, minutes));

        // Change label color to red if break duration exceeds 1 hour
        if (hours >= 1) {
            label.setForeground(Color.RED);
        } else {
            label.setForeground(Color.BLACK); // Reset to black if under 1 hour
        }
    }

    // Method to create staff checkboxes dynamically
    private void createStaffCheckboxes(String staffName) {
        JCheckBox boxIn = new JCheckBox(staffName + " - Check In");
        boxIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
        boxIn.setBounds(6, 95 + (staffNameList.size() * 23), 150, 21);
        frame.getContentPane().add(boxIn);
        
     // Create a label to display total break duration
        JLabel lblBreakDuration = new JLabel("Total Break: 0 hours 0 mins");
        lblBreakDuration.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblBreakDuration.setBounds(720, 95 + (staffNameList.size() * 23), 200, 21); // Adjust position as needed
        frame.getContentPane().add(lblBreakDuration);

        // Create Start Break and End Break buttons
        JButton btnStartBreak = new JButton("Start Break");
        JButton btnEndBreak = new JButton("End Break");
        btnEndBreak.setEnabled(false); // Initially disabled

        // Add action listener for Start Break
        btnStartBreak.addActionListener(e -> {
            LocalDateTime breakStartTime = LocalDateTime.now();
            System.out.println(staffName + " started break at: " + dtf.format(breakStartTime));
            btnStartBreak.setEnabled(false);
            btnEndBreak.setEnabled(true);

            // Store the break start time
            checkInTimes.put(staffName + "_breakStart", breakStartTime);
        });

        // Add action listener for End Break
        btnEndBreak.addActionListener(e -> {
            if (checkInTimes.containsKey(staffName + "_breakStart")) {
                LocalDateTime breakEndTime = LocalDateTime.now();
                System.out.println(staffName + " ended break at: " + dtf.format(breakEndTime));
                btnEndBreak.setEnabled(false);
                btnStartBreak.setEnabled(true);

                // Calculate break duration
                LocalDateTime breakStartTime = checkInTimes.get(staffName + "_breakStart");
                Duration breakDuration = Duration.between(breakStartTime, breakEndTime);
                long breakHours = breakDuration.toHours();
                long breakMinutes = breakDuration.toMinutes() % 60;

                System.out.println("Break duration for " + staffName + ": " + breakHours + " hours " + breakMinutes + " minutes");

                // Store the break duration in the breakDurations map
                breakDurations.put(staffName, breakDuration);

                // Update the break duration label
                updateBreakDurationLabel(lblBreakDuration, breakDuration);
            }
        });

        // Add buttons to the frame
        frame.getContentPane().add(btnStartBreak);
        frame.getContentPane().add(btnEndBreak);
        // Set bounds for buttons (you may need to adjust these)
        btnStartBreak.setBounds(460, 95 + (staffNameList.size() * 23), 120, 21);
        btnEndBreak.setBounds(590, 95 + (staffNameList.size() * 23), 120, 21);


        JCheckBox boxOut = new JCheckBox(staffName + " - Check Out");
        boxOut.setFont(new Font("Tahoma", Font.PLAIN, 14));
        boxOut.setBounds(300, 95 + (staffNameList.size() * 23), 150, 21);
        frame.getContentPane().add(boxOut);
        boxOut.setEnabled(false);

        staffNameList.add(staffName);

        boxIn.addActionListener(e -> {
            if (boxIn.isSelected()) {
                int response = JOptionPane.showConfirmDialog(frame, "Are you sure you want to check in?", "Confirm Check In", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    LocalDateTime checkInTime = LocalDateTime.now();
                    checkInTimes.put(staffName, checkInTime);
                    System.out.println(staffName + " checked in at: " + dtf.format(checkInTime));
                    boxIn.setEnabled(false);
                    boxOut.setEnabled(true);

                    // Check if the staff checked in after 12 PM
                    if (checkInTime.getHour() >= 11) {
                        boxOut.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(frame, "YOU'RE LATE BROO, PLEASE DO BETTER NEXT TIME", "WARNING NOTICE", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    boxIn.setSelected(false);
                }
            }
        });

        boxOut.addActionListener(e -> {
            if (boxOut.isSelected() && checkInTimes.containsKey(staffName)) {
                int response = JOptionPane.showConfirmDialog(frame, "Are you sure you want to check out?", "Confirm Check Out", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    LocalDateTime checkOutTime = LocalDateTime.now();
                    checkOutTimes.put(staffName, checkOutTime);
                    LocalDateTime checkInTime = checkInTimes.get(staffName);
                    Duration duration = Duration.between(checkInTime, checkOutTime);
                    long totalHours = duration.toHours();
                    long totalMinutes = duration.toMinutes() % 60;
                    System.out.println(staffName + " checked out at: " + dtf.format(checkOutTime));
                    System.out.println("Total hours worked by " + staffName + ": " + totalHours + " hours and " + totalMinutes + " minutes");
                    boxOut.setEnabled(false);
                } else {
                    boxOut.setSelected(false);
                }
            }
        });
    }

    // List to store staff names
    private List<String> staffNameList = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Test3 window = new Test3();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @wbp.parser.entryPoint
     */
    public Test3() {
        initialize();
        scheduleDailyPdfGeneration();
        scheduleAutoCheckOut();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 696, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
     // Add a panel to hold the time settings
        JPanel timeSettingsPanel = new JPanel();
        timeSettingsPanel.setLayout(new FlowLayout());
        timeSettingsPanel.setBounds(6, 603, 500, 50);
        frame.getContentPane().add(timeSettingsPanel);

        // Add a label and spinner for the hour
        JLabel lblHour = new JLabel("Hour:");
        timeSettingsPanel.add(lblHour);
        JSpinner spinnerHour = new JSpinner(new SpinnerNumberModel(11, 0, 23, 1));
        timeSettingsPanel.add(spinnerHour);

        // Add a label and spinner for the minute
        JLabel lblMinute = new JLabel("Minute:");
        timeSettingsPanel.add(lblMinute);
        JSpinner spinnerMinute = new JSpinner(new SpinnerNumberModel(30, 0, 59, 1));
        timeSettingsPanel.add(spinnerMinute);

        // Add a button to update the time settings
        JButton btnUpdateTime = new JButton("Update Time");
        timeSettingsPanel.add(btnUpdateTime);

        // Add an action listener to the button to update the time settings
        btnUpdateTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int hour = (int) spinnerHour.getValue();
                int minute = (int) spinnerMinute.getValue();
                LocalTime targetTime = LocalTime.of(hour, minute);
                LocalTime now = LocalTime.now();

                long initialDelay = Duration.between(now, targetTime).getSeconds(); // Calculate initial delay in seconds

                if (initialDelay < 0) {
                    // If the target time is already passed, schedule for the next day
                    initialDelay += TimeUnit.DAYS.toSeconds(1);
                }

                // Schedule the task to run daily at the specified time
                scheduler.scheduleAtFixedRate(Test3.this::generatePdf, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
                JOptionPane.showMessageDialog(frame, "PDF Generation Time Has Been Update Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Add a panel to hold the auto-check-out time settings
        JPanel autoCheckOutSettingsPanel = new JPanel();
        autoCheckOutSettingsPanel.setLayout(new FlowLayout());
        autoCheckOutSettingsPanel.setBounds(6, 686, 500, 50);
        frame.getContentPane().add(autoCheckOutSettingsPanel);

        // Add a label and spinner for the hour
        JLabel lblAutoCheckOutHour = new JLabel("Auto Check-out Hour:");
        autoCheckOutSettingsPanel.add(lblAutoCheckOutHour);
        JSpinner spinnerAutoCheckOutHour = new JSpinner(new SpinnerNumberModel(2, 0, 23, 1));
        autoCheckOutSettingsPanel.add(spinnerAutoCheckOutHour);

        // Add a label and spinner for the minute
        JLabel lblAutoCheckOutMinute = new JLabel("Auto Check-out Minute:");
        autoCheckOutSettingsPanel.add(lblAutoCheckOutMinute);
        JSpinner spinnerAutoCheckOutMinute = new JSpinner(new SpinnerNumberModel(59, 0, 59, 1));
        autoCheckOutSettingsPanel.add(spinnerAutoCheckOutMinute);

        // Add a button to update the auto-check-out time settings
        JButton btnUpdateAutoCheckOutTime = new JButton("Update Auto Check-out Time");
        autoCheckOutSettingsPanel.add(btnUpdateAutoCheckOutTime);

        // Add an action listener to the button to update the auto-check-out time settings
        btnUpdateAutoCheckOutTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int hour = (int) spinnerAutoCheckOutHour.getValue();
                int minute = (int) spinnerAutoCheckOutMinute.getValue();
                LocalTime targetTime = LocalTime.of(hour, minute);
                LocalTime now = LocalTime.now();

                long initialDelay = Duration.between(now, targetTime).getSeconds(); // Calculate initial delay in seconds

                if (initialDelay < 0) {
                    // If the target time is already passed, schedule for the next day
                    initialDelay += TimeUnit.DAYS.toSeconds(1);
                }

                // Schedule the task to run daily at the specified time
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(Test3.this::autoCheckOut, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
                JOptionPane.showMessageDialog(frame, "Checkout Time Has Been Update Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        retrieveStaffNames(); // Call the retrieveStaffNames method here
        
     // Add a JTextField to input the custom name
        JTextField txtCustomName = new JTextField();
        txtCustomName.setBounds(50, 345, 200, 20);
        frame.getContentPane().add(txtCustomName);
        
     // Create a panel to hold the custom checkboxes
        JPanel customPanel = new JPanel();
        customPanel.setLayout(new BoxLayout(customPanel, BoxLayout.Y_AXIS));
        customPanel.setBounds(6, 399, 500, 163);
        frame.getContentPane().add(customPanel);

        // Create a map to store the custom checkboxes
        Map<String, JCheckBox[]> customCheckboxes = new HashMap<>();

        // Add a JButton to add the custom name and checkboxes to the GUI
        JButton btnAddCustomName = new JButton("Add Part Time Name");
        btnAddCustomName.setBounds(260, 344, 150, 20);
        frame.getContentPane().add(btnAddCustomName);

     // Add an ActionListener to the JButton to add the custom name and checkboxes to the GUI
        btnAddCustomName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customName = txtCustomName.getText();
                if (!customName.isEmpty()) {
                    // Create two JCheckBoxes for check-in and check-out
                    JCheckBox boxCustomIn = new JCheckBox(customName + " - Check In");
                    JCheckBox boxCustomOut = new JCheckBox(customName + " - Check Out");
                    boxCustomOut.setEnabled(false);
                    boxCustomIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
                    boxCustomOut.setFont(new Font("Tahoma", Font.PLAIN, 14));

                    // Create a label to display total break duration
                    JLabel lblCustomBreakDuration = new JLabel("Total Break: 0 hours 0 mins");
                    lblCustomBreakDuration.setFont(new Font("Tahoma", Font.PLAIN, 14));

                    // Create Start Break and End Break buttons
                    JButton btnCustomStartBreak = new JButton("Start Break");
                    JButton btnCustomEndBreak = new JButton("End Break");
                    btnCustomEndBreak.setEnabled(false); // Initially disabled

                    // Add action listener for Start Break
                    btnCustomStartBreak.addActionListener(e1 -> {
                        LocalDateTime breakStartTime = LocalDateTime.now();
                        System.out.println(customName + " started break at: " + dtf.format(breakStartTime));
                        btnCustomStartBreak.setEnabled(false);
                        btnCustomEndBreak.setEnabled(true);

                        // Store the break start time
                        checkInTimes.put(customName + "_breakStart", breakStartTime);
                    });

                    // Add action listener for End Break
                    btnCustomEndBreak.addActionListener(e1 -> {
                        if (checkInTimes.containsKey(customName + "_breakStart")) {
                            LocalDateTime breakEndTime = LocalDateTime.now();
                            System.out.println(customName + " ended break at: " + dtf.format(breakEndTime));
                            btnCustomEndBreak.setEnabled(false);
                            btnCustomStartBreak.setEnabled(true);

                            // Calculate break duration
                            LocalDateTime breakStartTime = checkInTimes.get(customName + "_breakStart");
                            Duration breakDuration = Duration.between(breakStartTime, breakEndTime);
                            long breakHours = breakDuration.toHours();
                            long breakMinutes = breakDuration.toMinutes() % 60;

                            System.out.println("Break duration for " + customName + ": " + breakHours + " hours " + breakMinutes + " minutes");

                            // Store the break duration in the breakDurations map
                            breakDurations.put(customName, breakDuration);

                            // Update the break duration label
                            updateBreakDurationLabel(lblCustomBreakDuration, breakDuration);
                        }
                    });

                    // Add the checkboxes and buttons to the custom panel
                    JPanel checkboxPanel = new JPanel();
                    checkboxPanel.add(boxCustomIn);
                    checkboxPanel.add(boxCustomOut);
                    checkboxPanel.add(btnCustomStartBreak);
                    checkboxPanel.add(btnCustomEndBreak);
                    checkboxPanel.add(lblCustomBreakDuration);
                    customPanel.add(checkboxPanel);
                    frame.revalidate();
                    frame.repaint();

                    // Store the checkboxes in the map
                    customCheckboxes.put(customName, new JCheckBox[] {boxCustomIn, boxCustomOut});

                    // Add ActionListener to the custom check-in checkbox
                    boxCustomIn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (boxCustomIn.isSelected()) {
                                int response = JOptionPane.showConfirmDialog(frame, "Are you sure you want to check in?", "Confirm Check In", JOptionPane.YES_NO_OPTION);
                                if (response == JOptionPane.YES_OPTION) {
                                    LocalDateTime checkInTime = LocalDateTime.now();
                                    checkInTimes.put(customName, checkInTime);
                                    System.out.println(customName + " checked in at: " + dtf.format(checkInTime));
                                    boxCustomIn.setEnabled(false);
                                    boxCustomOut.setEnabled(true);
                                    
                                    // Check if the staff checked in after 12 PM
                                    if (checkInTime.getHour() >= 11) {
                                        boxCustomOut.setForeground(Color.RED);
                                        JOptionPane.showMessageDialog(frame, "YOU'RE LATE BROO, PLEASE DO BETTER NEXT TIME", "WARNING NOTICE", JOptionPane.WARNING_MESSAGE);
                                    }
                                } else {
                                    boxCustomIn.setSelected(false);
                                }
                            }
                        }
                    });

                    // Add ActionListener to the custom check-out checkbox
                    boxCustomOut.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (boxCustomOut.isSelected() && checkInTimes.containsKey(customName)) {
                                int response = JOptionPane.showConfirmDialog(frame, "Are you sure you want to check out?", "Confirm Check Out", JOptionPane.YES_NO_OPTION);
                                if (response == JOptionPane.YES_OPTION) {
                                    LocalDateTime checkOutTime = LocalDateTime.now();
                                    checkOutTimes.put(customName, checkOutTime);
                                    LocalDateTime checkInTime = checkInTimes.get(customName);
                                    Duration duration = Duration.between(checkInTime, checkOutTime);
                                    long totalHours = duration.toHours();
                                    long totalMinutes = duration.toMinutes() % 60;
                                    System.out.println(customName + " checked out at: " + dtf.format(checkOutTime));
                                    System.out.println("Total hours worked by " + customName + ": " + totalHours + " hours and " + totalMinutes + " minutes");
                                    boxCustomOut.setEnabled(false);
                                } else {
                                    boxCustomOut.setSelected(false);
                                }
                            }
                        }
                    });
                }
            }
        });
        
        JLabel lblNewLabel = new JLabel("CHECK IN");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblNewLabel.setBounds(50, 58, 106, 13);
        frame.getContentPane().add(lblNewLabel);
        
        JLabel lblCheckOut = new JLabel("CHECK OUT");
        lblCheckOut.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblCheckOut.setBounds(342, 58, 156, 13);
        frame.getContentPane().add(lblCheckOut);

        JButton btnGeneratePdf = new JButton("Generate PDF");
        btnGeneratePdf.setBounds(522, 442, 150, 30);
        frame.getContentPane().add(btnGeneratePdf);
        
        JLabel lblNewLabel_1 = new JLabel("DRIP.OH EATERY STAFF'S ATTENDANCE SYSTEM");
        lblNewLabel_1.setFont(new Font("PMingLiU-ExtB", Font.BOLD | Font.ITALIC, 22));
        lblNewLabel_1.setBounds(76, 0, 523, 39);
        frame.getContentPane().add(lblNewLabel_1);
        
     // Add a logo image to the GUI
        JLabel lblLogo = new JLabel();
        lblLogo.setBounds(626, 0, 56, 58);
        frame.getContentPane().add(lblLogo);

        // Load the logo image from a resource file
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("drip.oh - 3.PNG"));
            lblLogo.setIcon(logoIcon);
         // Resize the image to fit the label
            Image img = logoIcon.getImage();
            Image newImg = img.getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(newImg);
            lblLogo.setIcon(logoIcon);
        } catch (Exception e) {
            System.out.println("Error loading logo image: " + e.getMessage());
        }
        
     // Add a logo image to the GUI
        JLabel lblLogo1 = new JLabel();
        lblLogo1.setBounds(0, 0, 56, 58);
        frame.getContentPane().add(lblLogo1);

        // Load the logo image from a resource file
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("drip.oh - 3.PNG"));
            lblLogo1.setIcon(logoIcon);
         // Resize the image to fit the label
            Image img = logoIcon.getImage();
            Image newImg = img.getScaledInstance(lblLogo1.getWidth(), lblLogo1.getHeight(), java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(newImg);
            lblLogo1.setIcon(logoIcon);
            
            JButton btnMonthlySalary = new JButton("MONTHLY SALARY");
            btnMonthlySalary.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		String password = JOptionPane.showInputDialog(frame, "Enter password to generate PDF:");

                    if ("Heda".equals(password)) {
                    	test2 window = new test2();  // Create an instance of test2
                        window.showWindow();  // Call the method to show the test2 frame
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid password!", "Error", JOptionPane.ERROR_MESSAGE);
                    }	
            	}
            });
            btnMonthlySalary.setBounds(522, 383, 150, 30);
            frame.getContentPane().add(btnMonthlySalary);
            
            JLabel lblNewLabel_2 = new JLabel("Update Time To Generate PDF Automatically");
            lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 14));
            lblNewLabel_2.setBounds(6, 580, 500, 13);
            frame.getContentPane().add(lblNewLabel_2);
            
            JLabel lblNewLabel_2_1 = new JLabel("Update Time To Automatically Check Out all Staff (1 Min before PDF)");
            lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 14));
            lblNewLabel_2_1.setBounds(6, 663, 500, 13);
            frame.getContentPane().add(lblNewLabel_2_1);
            
            JButton btnNewButton = new JButton("REGISTER STAFF");
            btnNewButton.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		test4 window = new test4();
            		window.showWindow();
            	}
            });
            btnNewButton.setBounds(522, 502, 150, 30);
            frame.getContentPane().add(btnNewButton);
            
            JButton btnStaffList = new JButton("Staff's List");
            btnStaffList.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		test5 window = new test5();  // Create an instance of test2
                    window.showWindow();  // Call the method to show the test2 frame
            	}
            });
            btnStaffList.setBounds(522, 557, 150, 30);
            frame.getContentPane().add(btnStaffList);
        } catch (Exception e) {
            System.out.println("Error loading logo image: " + e.getMessage());
        }

        btnGeneratePdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show password dialog
                String password = JOptionPane.showInputDialog(frame, "Enter password to generate PDF:");

                if ("Azlana".equals(password)) {
                    generatePdf();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void scheduleDailyPdfGeneration() {
    	LocalDate currentDate = LocalDate.now();

        // Get the day of the week for the current date
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
    	if(dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)
    	{
    		LocalTime targetTime = LocalTime.of(23, 00); // 10:00 PM
            LocalTime now = LocalTime.now();

            long initialDelay = Duration.between(now, targetTime).getSeconds(); // Calculate initial delay in seconds

            if (initialDelay < 0) {
                // If the target time is already passed, schedule for the next day
                initialDelay += TimeUnit.DAYS.toSeconds(1);
            }

            // Schedule the task to run daily at 10 PM
            scheduler.scheduleAtFixedRate(this::generatePdf, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    	}
    	else
    	{
    		LocalTime targetTime = LocalTime.of(23, 00); // 10:00 PM
            LocalTime now = LocalTime.now();

            long initialDelay = Duration.between(now, targetTime).getSeconds(); // Calculate initial delay in seconds

            if (initialDelay < 0) {
                // If the target time is already passed, schedule for the next day
                initialDelay += TimeUnit.DAYS.toSeconds(1);
            }

            // Schedule the task to run daily at 10 PM
            scheduler.scheduleAtFixedRate(this::generatePdf, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    	}
        
    }
    
 // Method to generate the PDF
    private void generatePdf() {
        String status = "";
        System.out.println("Generating PDF...");
        Document document = new Document();
        String date = dateOnlyFormat.format(LocalDateTime.now());
        try {
            PdfWriter.getInstance(document, new FileOutputStream("Staff Report " + date + ".pdf"));
            document.open();
            document.add(new Paragraph("Drip.Oh Eatery Staff Report\n"));

            PdfPTable table = new PdfPTable(6); // 6 columns for Name, Check-in, Check-out, Total hours, Break Duration, Date

            table.addCell("Name");
            table.addCell("Check-in");
            table.addCell("Check-out");
            table.addCell("Total Hours");
            table.addCell("Break Duration");
            table.addCell("Date");

            for (String name : checkInTimes.keySet()) {
                LocalDateTime checkIn = checkInTimes.get(name);
                LocalDateTime checkOut = checkOutTimes.get(name);
                Duration breakDuration = breakDurations.getOrDefault(name, Duration.ZERO); // Default to zero if no break duration

                if (checkIn != null && checkOut != null) {
                    // Calculate total duration worked
                    Duration totalDuration = Duration.between(checkIn, checkOut);
                    // Deduct break duration
                    Duration effectiveDuration = totalDuration.minus(breakDuration);

                    long totalHours = effectiveDuration.toHours();
                    long totalMinutes = effectiveDuration.toMinutes() % 60;
                    long breakHours = breakDuration.toHours();
                    long breakMinutes = breakDuration.toMinutes() % 60;

                    table.addCell(name);
                    table.addCell(dtf.format(checkIn));
                    table.addCell(dtf.format(checkOut));
                    table.addCell(totalHours + " hours " + totalMinutes + " mins");
                    table.addCell(breakHours + " hours " + breakMinutes + " mins"); // Add break duration to the PDF
                    table.addCell(dateOnlyFormat.format(checkIn));

                    // Calculate total earnings
                    double hourlyRate = retrieveHourlyRatesFromDatabase().getOrDefault(name, 7.2); // Default hourly rate
                    double totalEarnings = (totalHours + (totalMinutes / 60.0)) * hourlyRate; // Calculate total earnings based on the hourly rate

                    // Insert data into the Staff table
                    insertStaffDataIntoDatabase(name, date, totalEarnings, status);
                }
            }

            document.add(table);
            
            document.add(new Paragraph("\nDrip.Oh Eatery Staff Daily Earnings\n"));

            // Create a new table to display the total earnings of each staff daily
            PdfPTable earningsTable = new PdfPTable(3); // 3 columns for Name, Hourly Rate, and Total Earnings

            earningsTable.addCell("Name");
            earningsTable.addCell("Hourly Rate (RM)");
            earningsTable.addCell("Total Earnings (RM)");

            for (String name : checkInTimes.keySet()) {
                LocalDateTime checkIn = checkInTimes.get(name);
                LocalDateTime checkOut = checkOutTimes.get(name);
                Duration breakDuration = breakDurations.getOrDefault(name, Duration.ZERO);

                if (checkIn != null && checkOut != null) {
                    Duration duration = Duration.between(checkIn, checkOut);
                    Duration effectiveDuration = duration.minus(breakDuration);
                    long totalHours = effectiveDuration.toHours();
                    long totalMinutes = effectiveDuration.toMinutes() % 60;
                    double hourlyRate = retrieveHourlyRatesFromDatabase().getOrDefault(name, 7.2);
                    double totalEarnings = (totalHours + (totalMinutes / 60.0)) * hourlyRate;

                    earningsTable.addCell(name);
                    earningsTable.addCell(String.valueOf(hourlyRate));
                    earningsTable.addCell(String.format("%.2f", totalEarnings));
                }
            }

            document.add(earningsTable);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        JOptionPane.showMessageDialog(frame, "PDF generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to retrieve hourly rates from database
    private Map<String, Double> retrieveHourlyRatesFromDatabase() {
        String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db"; // Replace with your database file path
        /*java.net.URL url = getClass().getClassLoader().getResource("jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db");
    	String path = url.getFile();
    	String jdbcUrl = "jdbc:sqlite:" + path;*/
        Map<String, Double> hourlyRates = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Name, Rate FROM StaffInfo")) {

            while (rs.next()) {
                String name = rs.getString("Name");
                double hourlyRate = rs.getDouble("Rate");
                hourlyRates.put(name, hourlyRate);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return hourlyRates;
    }

 // Method to insert staff data into database
    private void insertStaffDataIntoDatabase(String name, String date, double salary, String status) {
        String url = "jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db"; // Replace with your database file path

    	/*java.net.URL url = getClass().getClassLoader().getResource("jdbc:sqlite:C:\\Users\\moham_000\\Desktop\\Drip.OhStaff.db");
    	String path = url.getFile();
    	String jdbcUrl = "jdbc:sqlite:" + path;*/
    	try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Staff(Name, Date, Salary, Status) VALUES(?, ?, ?, ?)")) {

            pstmt.setString(1, name);
            pstmt.setString(2, date);
            pstmt.setDouble(3, salary);
            pstmt.setString(4, status);

            pstmt.executeUpdate();
            System.out.println("Staff data inserted successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
 // Add a method to check out all checked-in boxes
    private void autoCheckOut() {
        System.out.println("Auto checking out all checked-in boxes...");
        for (String name : checkInTimes.keySet()) {
            if (!checkOutTimes.containsKey(name)) {
                LocalDateTime checkOutTime = LocalDateTime.now();
                checkOutTimes.put(name, checkOutTime);
                LocalDateTime checkInTime = checkInTimes.get(name);
                Duration duration = Duration.between(checkInTime, checkOutTime);
                long totalHours = duration.toHours();
                long totalMinutes = duration.toMinutes() % 60;
                System.out.println(name + " checked out at: " + dtf.format(checkOutTime));
                System.out.println("Total hours worked by " + name + ": " + totalHours + " hours and " + totalMinutes + " minutes");
            }
        }
        // Update the GUI to reflect the checked-out boxes
        for (JCheckBox[] checkboxes : customCheckboxes.values()) {
            checkboxes[1].setEnabled(false);
            checkboxes[0].setEnabled(true); // Enable the check-in checkbox
        }
        // Update the GUI to reflect the checked-out boxes for the predefined staff
        boxAzlanOut.setEnabled(false);
        boxIzwanQudriOut.setEnabled(false);
        boxIzwanAlifOut.setEnabled(false);
        boxRajaSyediOut.setEnabled(false);
        boxNazriOut.setEnabled(false);
        boxAmirulFizryOut.setEnabled(false);
        boxFakhrullahOut.setEnabled(false);
    }

 // Schedule the auto-check-out task to run daily at 10 PM
    private void scheduleAutoCheckOut() {
    	LocalDate currentDate = LocalDate.now();

        // Get the day of the week for the current date
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        
        if(dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)
        {
        	LocalTime targetTime = LocalTime.of(22, 59); // 10 PM
            LocalTime now = LocalTime.now();

            long initialDelay = Duration.between(now, targetTime).getSeconds(); // Calculate initial delay in seconds

            if (initialDelay < 0) {
                // If the target time is already passed, schedule for the next day
                initialDelay += TimeUnit.DAYS.toSeconds(1);
            }

            // Schedule the task to run daily at 10 PM
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(this::autoCheckOut, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        }
        else
        {
        	LocalTime targetTime = LocalTime.of(22, 59); // 10 PM
            LocalTime now = LocalTime.now();

            long initialDelay = Duration.between(now, targetTime).getSeconds(); // Calculate initial delay in seconds

            if (initialDelay < 0) {
                // If the target time is already passed, schedule for the next day
                initialDelay += TimeUnit.DAYS.toSeconds(1);
            }

            // Schedule the task to run daily at 10 PM
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(this::autoCheckOut, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        }
        
    }
}