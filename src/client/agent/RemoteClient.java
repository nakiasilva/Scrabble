package client.agent;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import entity.User;
import server.IRemoteServer;

@SuppressWarnings("serial")
public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {
	private Registry registry;
	private User user;
	Scanner sc = new Scanner(System.in);

	public RemoteClient(Registry registry, User user) throws RemoteException {
		this.registry = registry;
		this.user = user;
	}

	// GUI 1. initialize client with server ip, server port, client ip, client port,
	// user name

	// GUI 2. login & logout fuction/button
	public void login() throws AccessException, RemoteException, NotBoundException {
		((IRemoteServer) registry.lookup("server")).login(user);
	}

	public void logoff() throws AccessException, RemoteException, NotBoundException {
		((IRemoteServer) registry.lookup("server")).logoff(user);
	}

	// GUI 3. show active user list, with invitation function to start a game, like
	// check box
	@Override
	public void showUserList(Set<User> users) throws AccessException, RemoteException, NotBoundException {
		// show List of uers
		for (User user : users) {
			System.out.println(user);
		}

		// start new game
		List<User> gamers = new ArrayList<User>();
		gamers.addAll(users);
		((IRemoteServer) registry.lookup("server")).startGame(gamers);
	}

	// GUI 4. refresh the whole grids
	@Override
	public void refreshGrid(char[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			System.out.printf("{");
			for (int j = 0; j < grid[i].length; j++) {
				System.out.printf(grid[i][j] == '\u0000' ? "	" : Character.toString(grid[i][j]) + "	");
			}
			System.out.println("}");
		}
	}

	// GUI 5. Client start to place a char with timeout
	@Override
	public void startNewTurn() throws AccessException, RemoteException, NotBoundException {
		// fill a letter in an empty block
		System.out.println("Do you want to place a new char or pass");
		

		if (sc.nextBoolean()) {
			System.out.println("Plz point a pixel x,y");
			((IRemoteServer) registry.lookup("server")).placeChar(sc.nextInt(), sc.nextInt(), 'A', user);

		} else {
			((IRemoteServer) registry.lookup("server")).pass(user);
		}
	}

	// GUI 6. Client ends game show score board
	@Override
	public void endGame(int score) {
		System.out.println(score);
		sc.close();
	}

	// GUI 7. client claim a word between 2 grids with timeout
	@Override
	public void claim() throws AccessException, RemoteException, NotBoundException {
		// claim a word
		System.out.println("Plz fill in a region between (x1,y1) and (x2,y2)");

		// start voting
		((IRemoteServer) registry.lookup("server")).vote(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt(), user);
	}

	// GUI 8. show the claimed word and vote
	@Override
	public boolean vote(int x1, int y1, int x2, int y2, char[][] grid) {
		String out = "";
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				out += (grid[i][j] == '\u0000' ? " " : Character.toString(grid[i][j]));
			}
		}
		System.out.println(out);

		System.out.println("Decision for voting?");
		

		// a button for votte
		return sc.nextBoolean();
	}

}
