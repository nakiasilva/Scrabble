package server;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

public class ServerGUIMain {

	private JFrame frame;
	private int portNumber;
	private String ipAddress;
	private JTextField txtIpAddress;
	private JTextField txtPortNumber;
	private JTextArea textAreaServer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUIMain window = new ServerGUIMain();
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
	public ServerGUIMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtIpAddress = new JTextField();
		txtIpAddress.setText("IP Address");
		txtIpAddress.setBounds(6, 20, 130, 26);
		frame.getContentPane().add(txtIpAddress);
		txtIpAddress.setColumns(10);
		
		txtPortNumber = new JTextField();
		txtPortNumber.setText("Port Number");
		txtPortNumber.setBounds(6, 60, 130, 26);
		frame.getContentPane().add(txtPortNumber);
		txtPortNumber.setColumns(10);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				portNumber=Integer.parseInt(txtPortNumber.getText());
				ipAddress=txtIpAddress.getText();
//TODO  exception handling				
				try {
					IRemoteServer server = new RemoteServer();
					LocateRegistry.createRegistry(portNumber);
					Registry registry = LocateRegistry.getRegistry(ipAddress, portNumber);
				
					registry.rebind("server", server);
				
					
				} catch (AccessException e1) {
					
				} catch (RemoteException e1) {
					
				} catch (IOException e1) {
				
				} catch (SQLException e1) {
					
				}	
			}
		});
		btnSubmit.setBounds(6, 112, 130, 29);
		frame.getContentPane().add(btnSubmit);
		
		textAreaServer = new JTextArea();
		textAreaServer.setBounds(218, 20, 178, 121);
		frame.getContentPane().add(textAreaServer);
		

	}
}
