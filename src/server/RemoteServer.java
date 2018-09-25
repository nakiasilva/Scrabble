package server;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import client.agent.IRemoteClient;
import entity.User;

@SuppressWarnings("serial")
public class RemoteServer extends UnicastRemoteObject implements IRemoteServer {
	private char[][] grid;
	private HashSet<User> users;
	private List<User> gamers;
	private HashMap<User, Integer> scores;
	private boolean startFlag;
	private int passCount;

	public RemoteServer() throws IOException, SQLException {
		grid = new char[20][20];
		users = new HashSet<User>();
		gamers = new ArrayList<User>();
		scores = new HashMap<User, Integer>();
		startFlag = false;
		passCount = 0;

	}

	public void login(User newUser) throws RemoteException {
		users.add(newUser);

		users.parallelStream().forEach(user -> {
			if (startFlag == false || !gamers.contains(user)) {
				try {
					getRemoteClient(user).showUserList(users);
				} catch (RemoteException | NotBoundException e) {
					System.out.println("User: " + user.toString() + " is disconnected");
					users.remove(user);
					e.printStackTrace();
				}
			}
		});
	}

	public void logoff(User expiredUser) throws RemoteException {
		users.remove(expiredUser);
		users.parallelStream().forEach(user -> {
			if (startFlag == false || !gamers.contains(user)) {
				try {
					getRemoteClient(user).showUserList(users);
				} catch (RemoteException | NotBoundException e) {
					System.out.println("User: " + user.toString() + " is disconnected");
					users.remove(user);
					e.printStackTrace();
				}
			}
		});
	}

	public boolean startGame(List<User> newGamers) throws RemoteException {
		if (startFlag) {
			return false;
		}

		grid = new char[20][20];
		gamers = new ArrayList<User>();
		scores = new HashMap<User, Integer>();
		startFlag = true;
		passCount = 0;

		int connectedGammers = newGamers.parallelStream().mapToInt(newGamer -> {
			try {
				scores.put(newGamer, 0);
				gamers.add(newGamer);
				getRemoteClient(newGamer).refreshGrid(grid);
				return 1;
			} catch (RemoteException | NotBoundException e) {
				System.out.println("New gamer: " + newGamer.toString() + " is disconnected");
				e.printStackTrace();
				return 0;
			}
		}).sum();

		try {
			if (connectedGammers == newGamers.size()) {
				getRemoteClient(newGamers.get(0)).startNewTurn();
				return true;
			}
		} catch (NotBoundException e) {
			System.out.println("The first gamer: " + newGamers.get(0).toString() + " is disconnected");
			e.printStackTrace();
		}

		gracefulShutDown();
		return false;
	}

	public boolean placeChar(int x, int y, char c, User user) throws RemoteException {
		grid[x][y] = c;

		int connectedGamers = gamers.parallelStream().mapToInt(gamer -> {
			try {
				getRemoteClient(gamer).refreshGrid(grid);
				return 1;
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + gamer.toString() + " is disconnected");
				e.printStackTrace();
				return 0;
			}
		}).sum();
		passCount = 0;

		try {
			if (connectedGamers == gamers.size()) {
				getRemoteClient(user).claim();
				return true;
			}

		} catch (NotBoundException e) {
			System.out.println("Gamer: " + user.toString() + " is disconnected");
			e.printStackTrace();
		}

		gracefulShutDown();
		return false;
	}

	public boolean vote(int x1, int y1, int x2, int y2, User user) throws RemoteException {
		int count = gamers.parallelStream().mapToInt(gamer -> {
			try {
				if (getRemoteClient(gamer).vote(x1, y1, x2, y2, grid)) {
					return 1;
				} else {
					return 0;
				}
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + gamer.toString() + " is disconnected during voting");
				e.printStackTrace();
			}
			return -1 - gamers.size();
		}).sum();

		if (count >= gamers.size() / 2) {
			int score = (x1 == x2) ? 1 + Math.abs(y1 - y2) : 1 + Math.abs(x1 - x2);
			scores.put(user, scores.get(user) + score);
		}

		try {
			if (count >= 0) {
				int index = gamers.indexOf(user);
				user = gamers.get(index == gamers.size() - 1 ? 0 : index + 1);
				getRemoteClient(user).startNewTurn();
				return true;
			}
		} catch (NotBoundException e) {
			System.out.println("Gamer: " + user.toString() + " is disconnected");
			e.printStackTrace();
		}

		gracefulShutDown();
		return false;

	}

	public boolean pass(User user) throws RemoteException {
		passCount++;
		try {
			if (passCount < gamers.size()) {
				int index = gamers.indexOf(user);
				user = gamers.get(index == gamers.size() - 1 ? 0 : index + 1);
				getRemoteClient(user).startNewTurn();
				return true;
			}
		} catch (NotBoundException e) {
			System.out.println("Gamer: " + user.toString() + " is disconnected");
			e.printStackTrace();
		}
		gracefulShutDown();
		return false;
	}

	private IRemoteClient getRemoteClient(User user) throws RemoteException, NotBoundException {
		return (IRemoteClient) LocateRegistry.getRegistry(user.getIp(), user.getPort()).lookup(user.getName());
	}

	private void gracefulShutDown() {
		gamers.parallelStream().forEach(gamer -> {
			try {
				getRemoteClient(gamer).endGame(scores.get(gamer));
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + gamer.toString() + " is disconnected");
				users.remove(gamer);
				e.printStackTrace();
			}
		});
		startFlag = false;
	}

}
