import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;
	//JMenuItem mnuItemsearchNum; // view by number
	//JMenuItem mnuItemDescUpdateTicket; //update description
	//JMenuItem mnuItemCloseTicket; // close ticket


	

	public Tickets(Boolean isAdmin) {

		if(chkIfAdmin = isAdmin) {
			createMenu(true);
			prepareGUI(true);
			
		}else {
			createMenu(false);
			prepareGUI(false);
		}
		

	}

	private void createMenu(boolean isAdmin) {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);
		// for admin only
		if(isAdmin) {
			// initialize first sub menu items for Admin main menu
			mnuItemUpdate = new JMenuItem("Update Ticket");
			// add to Admin main menu item
			mnuAdmin.add(mnuItemUpdate);
	
			// initialize second sub menu items for Admin main menu
			mnuItemDelete = new JMenuItem("Delete Ticket");
			// add to Admin main menu item
			mnuAdmin.add(mnuItemDelete);
		}

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);
		
		
		
		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		if(isAdmin) {
			mnuItemUpdate.addActionListener(this);
			mnuItemDelete.addActionListener(this);

		}
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		

		 /*
		  * continue implementing any other desired sub menu items (like 
		  * for update and delete sub menus for example) with similar 
		  * syntax & logic as shown above
		 */

 
	}

	private void prepareGUI(boolean isAdmin) {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		if (isAdmin) {
			bar.add(mnuAdmin);
		}
		bar.add(mnuTickets);
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {
			
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name");

			//regex for name
			String unameP = "^[a-zA-Z]{3,10}]+$";
			if(!ticketName.matches(unameP)){
				JOptionPane.showMessageDialog(null, "Name must be only 3-10 characters long");
				return;
			}

			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

			//regex for description
			String descP = "^[a-zA-Z0-9\\s.,!?-]{10,200}+$";
			if(!ticketName.matches(descP)){
				JOptionPane.showMessageDialog(null, "Description is only letters, numbers, spaces, and certain punctuation.");
				return;
			}


			// insert ticket information to database

			int id = dao.insertRecords(ticketName, ticketDesc);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else {
				System.out.println("Ticket cannot be created!!!");
		}
		}

		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}finally {
				try {
					dao.getConnection().close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		
		/*
		 * continue implementing any other desired sub menu items (like for update and
		 * delete sub menus for example) with similar syntax & logic as shown above
		 */
		
	}	else if(e.getSource() == mnuItemUpdate){
			try {
				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

				
			}catch(SQLException e1){
				e1.printStackTrace();
			}
			// get ticket info to update
			String ticketNum = JOptionPane.showInputDialog(null, "Enter ticket number to update");
			String newDesc = JOptionPane.showInputDialog(null, "Enter new description");
			String ticketStatus = JOptionPane.showInputDialog(null, "Close ticket? (1=NO/2=YES)");
			int id = Integer.valueOf(ticketNum);
			int rows = dao.updateRecords(id, newDesc, ticketStatus);
			
			// results
			if(rows > 0) {
				System.out.println("Ticket ID; "+ticketNum+" updated");
				JOptionPane.showMessageDialog(null, "Ticket ID; "+ticketNum+" updated");
			}else {
				System.out.println("Ticket can't be updated.");
				JOptionPane.showMessageDialog(null, "Ticket can't be updated");

			}
		}else if (e.getSource()== mnuItemDelete) {
	
			try {
				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

				
			}catch(SQLException e1){
				e1.printStackTrace();
			}
			String ticketNum = JOptionPane.showInputDialog(null, "Enter ticket number to delete");
			//int id = dao.deleteRecords(Integer.valueOf(ticketNum));
			
			if(ticketNum == null) {
				System.out.println("Canceled");
			}else {
				int confirm = JOptionPane.showConfirmDialog(null, ("Are you sure you want to delete the ticket: "+ Integer.valueOf(ticketNum)), "Please Confirm",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
				// display results
				if(confirm == JOptionPane.YES_OPTION) {
					dao.deleteRecords(Integer.valueOf(ticketNum));
					System.out.println("Ticket ID; "+ticketNum+" deleted");
					JOptionPane.showMessageDialog(null, "Ticket ID; "+ticketNum+" deleted");
				}else {
					System.out.println("No ticket deleted");
			}
			
		
		}
	}
		

	}

}