package client.agent;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;

import entity.User;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class ScrabbleGUI {

	private JFrame frame;
	private JTextField txtUserName;
	private JTextField txtPort;
	private JTextField txtIpAddress;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScrabbleGUI window = new ScrabbleGUI();
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
	public ScrabbleGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setForeground(Color.DARK_GRAY);
		frame.setBackground(Color.DARK_GRAY);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtUserName = new JTextField();
		txtUserName.setText("User Name");
		txtUserName.setBounds(6, 26, 130, 26);
		frame.getContentPane().add(txtUserName);
		txtUserName.setColumns(10);
		
		txtPort = new JTextField();
		txtPort.setText("Port Number");
		txtPort.setBounds(148, 26, 130, 26);
		frame.getContentPane().add(txtPort);
		txtPort.setColumns(10);
		
		txtIpAddress = new JTextField();
		txtIpAddress.setText("IP Address");
		txtIpAddress.setBounds(290, 26, 130, 26);
		frame.getContentPane().add(txtIpAddress);
		txtIpAddress.setColumns(10);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				User user = new User(txtIpAddress.getText(), txtUserName.getText(), Integer.parseInt(txtPort.getText()));
				RemoteClient client;
				try {
					client = new RemoteClient(LocateRegistry.getRegistry("localhost", 19140), user);
					LocateRegistry.createRegistry(user.getPort());
					LocateRegistry.getRegistry(user.getIp(), user.getPort()).bind(user.getName(), client);
					client.login();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (AlreadyBoundException e1) {
					e1.printStackTrace();
				} catch (NotBoundException e1) {
					
					e1.printStackTrace();
				}
				
			}
		});
		btnSubmit.setBounds(161, 109, 117, 29);
		frame.getContentPane().add(btnSubmit);
	}
}
