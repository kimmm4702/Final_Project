import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout; //useful for layouts
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//controls-label text fields, button
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Login extends JFrame {

	Dao conn;

	public Login() {

		super("IIT HELP DESK LOGIN");
		conn = new Dao();
		//conn.createTables();
		setSize(400, 250);
		setLayout(new GridLayout(4, 2));
		setLocationRelativeTo(null); // centers window
		
		// set background color
		getContentPane().setBackground(Color.lightGray);
				
				
		// SET UP CONTROLS
		JLabel lblUsername = new JLabel("Username",JLabel.LEFT);
		JLabel lblPassword = new JLabel("Password",JLabel.LEFT);
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);
		// JLabel lblSpacer = new JLabel(" ", JLabel.CENTER);
		
		// text color
		lblUsername.setForeground(Color.black);
		lblPassword.setForeground(Color.black);
		lblStatus.setForeground(Color.red);
		
		// fonts
		Font labelfont = new Font("Arial" , Font.BOLD, 14);
		lblUsername.setFont(labelfont);
		lblPassword.setFont(labelfont);
		lblStatus.setFont(labelfont);
		


		JTextField txtUname = new JTextField(10);
		JPasswordField txtPassword = new JPasswordField();
		
		// color for text boxes
		txtUname.setBackground(Color.white);
		txtPassword.setBackground(Color.white);

		JButton btn = new JButton("Submit");
		JButton btnExit = new JButton("Exit");

		// button colors
		btn.setBackground(Color.PINK);
		btn.setForeground(Color.BLACK);
		btnExit.setBackground(Color.magenta);
		btnExit.setForeground(Color.BLACK);
		
		

		lblStatus.setToolTipText("Contact help desk to unlock password");
		lblUsername.setHorizontalAlignment(JLabel.CENTER);
		lblPassword.setHorizontalAlignment(JLabel.CENTER);
 
		// ADD OBJECTS TO FRAME
		add(lblUsername);  // 1st row filler
		add(txtUname);
		add(lblPassword);  // 2nd row
		add(txtPassword);
		add(btn);          // 3rd row
		add(btnExit);
		add(lblStatus);    // 4th row

		btn.addActionListener(new ActionListener() {
			int count = 0; // count agent

			@SuppressWarnings({ "deprecation" })
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean admin = false;
				count = count + 1;
				// verify credentials of user (MAKE SURE TO CHANGE TO YOUR TABLE NAME BELOW)

				String query = "SELECT * FROM kdomin_users WHERE uname = ? and upass = ?;";
				try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					stmt.setString(2, txtPassword.getText());
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						admin = rs.getBoolean("admin"); // get table column value
						new Tickets(admin); //open Tickets file / GUI interface
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
					} else
						lblStatus.setText("Try again! " + (3 - count) + " / 3 attempt(s) left");
						if(count >= 3) {
							JOptionPane.showMessageDialog(null,"Too many attempts, program is closing");
							System.exit(0);;
						}
				} catch (SQLException ex) {
					ex.printStackTrace();
					lblStatus.setText("Database error. Try again.");
				}
 			 
			}
		});
		btnExit.addActionListener(e -> System.exit(0));

		setVisible(true); // SHOW THE FRAME
	}

	public static void main(String[] args) {

		new Login();
	}
}
