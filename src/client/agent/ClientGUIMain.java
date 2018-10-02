package client.agent;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JTextField;

import client.agent.RemoteClient;
import entity.User;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class ClientGUIMain {

	private JFrame frame;
	private JTextField txtServerIpAddress;
	private JTextField txtServerPortNumber;
	private JTextField txtCPortNumber;
	private JTextField txtClientIpAddress;
	private JTextField txtUser;
	private int clientPortNumber;
	private int serverPortNumber;
	private String serverIpAddress;
	private String clientIPAddress;
	private String userName;
	static ArrayList<buttonProp> buttons =new ArrayList<buttonProp>(400);
	static int score = 0;
	private JTextArea textArea;
	private RemoteClient client;
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUIMain window = new ClientGUIMain();
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
	public ClientGUIMain() {
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

		txtServerIpAddress = new JTextField();
		txtServerIpAddress.setText("Server IP address");
		txtServerIpAddress.setBounds(6, 22, 136, 26);
		frame.getContentPane().add(txtServerIpAddress);
		txtServerIpAddress.setColumns(10);

		txtServerPortNumber = new JTextField();
		txtServerPortNumber.setText("Server Port Number");
		txtServerPortNumber.setBounds(6, 60, 136, 26);
		frame.getContentPane().add(txtServerPortNumber);
		txtServerPortNumber.setColumns(10);

		txtCPortNumber = new JTextField();
		txtCPortNumber.setText("Client Port Number");
		txtCPortNumber.setBounds(6, 103, 136, 26);
		frame.getContentPane().add(txtCPortNumber);
		txtCPortNumber.setColumns(10);

		txtClientIpAddress = new JTextField();
		txtClientIpAddress.setText("Client IP address");
		txtClientIpAddress.setBounds(6, 144, 136, 26);
		frame.getContentPane().add(txtClientIpAddress);
		txtClientIpAddress.setColumns(10);

		txtUser = new JTextField();
		txtUser.setText("User");
		txtUser.setBounds(6, 180, 136, 26);
		frame.getContentPane().add(txtUser);
		txtUser.setColumns(10);

		//Text area
		textArea = new JTextArea(5, 20);
		textArea.setBounds(6, 225, 438, 47);
		frame.getContentPane().add(textArea);
		
		JButton btnSubmit = new JButton("Submit");
		
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				serverIpAddress=txtServerIpAddress.getText();
				serverPortNumber=Integer.parseInt(txtServerPortNumber.getText());
				clientPortNumber=Integer.parseInt(txtCPortNumber.getText());
				clientIPAddress=txtClientIpAddress.getText();
				userName=txtUser.getText();
				//1635
				User user = new User(clientIPAddress, userName, clientPortNumber);
				try {
					client = new RemoteClient(LocateRegistry.getRegistry(serverIpAddress, serverPortNumber), user);
					LocateRegistry.createRegistry(user.getPort());
					LocateRegistry.getRegistry(user.getIp(), user.getPort()).bind(user.getName(), client);

					client.setTextArea(textArea);
				
					client.login();
					
				
				} catch (Exception e1) {
					e1.printStackTrace();

				}
				}
			});
		btnSubmit.setBounds(208, 40, 117, 29);
		frame.getContentPane().add(btnSubmit);
		
		JButton btnStartGame = new JButton("Start Game");
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(client.getGamers());
				try {
					Client_GUI window = new Client_GUI();
					window.getFrame().setVisible(true);
					client.startGame();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
//				catch (RemoteException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (NotBoundException e1) {
//				
//					e1.printStackTrace();
//				}
			}
		});
		btnStartGame.setBounds(208, 103, 117, 29);
		frame.getContentPane().add(btnStartGame);
		
		


		}
	}
