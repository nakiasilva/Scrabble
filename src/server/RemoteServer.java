package server;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import client.agent.IRemoteClient;
import entity.User;

@SuppressWarnings("serial")
public class RemoteServer extends UnicastRemoteObject implements IRemoteServer {
	private char[][] grid;
	private List<User> gamers;
	private Map<User, Integer> users;
	private boolean startFlag;
	private int passCount;

	public RemoteServer() throws IOException, SQLException {
		grid = new char[20][20];
		gamers = new CopyOnWriteArrayList<User>();
		users = new ConcurrentHashMap<User, Integer>();
		startFlag = false;
		passCount = 0;

	}

	public void login(User newUser) {
		users.put(newUser, 0);
		showActiveUsers();
	}

	public void logoff(User expiredUser) {
		users.remove(expiredUser);
		showActiveUsers();
	}

	public boolean startGame(List<User> newGamers) {
		if (startFlag) {
			return false;
		}
		grid = new char[20][20];
		gamers = new CopyOnWriteArrayList<User>();
		users = new ConcurrentHashMap<User, Integer>();
		startFlag = true;
		passCount = 0;

		int connectedGammers = newGamers.parallelStream().mapToInt(newGamer -> {
			try {
				users.put(newGamer, 0);
				gamers.add(newGamer);
				getRemoteClient(newGamer).refreshGrid(grid);
				return 1;
			} catch (RemoteException | NotBoundException e) {
				System.out.println("New gamer: " + newGamer.toString() + " is disconnected\n");
				e.printStackTrace();
				return 0;
			}
		}).sum();

		try {
			if (connectedGammers == newGamers.size()) {
				getRemoteClient(newGamers.get(0)).startNewTurn();
				return true;
			}
		} catch (NotBoundException | RemoteException e) {
			System.out.println("The first gamer: " + newGamers.get(0).toString() + " is disconnected\n");
			e.printStackTrace();
		}

		gracefulShutDown();
		return false;
	}

	public boolean placeChar(int x, int y, char c, User user) {
		grid[x][y] = c;

		int connectedGamers = gamers.parallelStream().mapToInt(gamer -> {
			try {
				getRemoteClient(gamer).refreshGrid(grid);
				return 1;
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + gamer.toString() + " is disconnected\n");
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

		} catch (NotBoundException | RemoteException e) {
			System.out.println("Gamer: " + user.toString() + " is disconnected\n");
			e.printStackTrace();
		}

		gracefulShutDown();
		return false;
	}

	public boolean vote(int x1, int y1, int x2, int y2, User user) {
		int count = gamers.parallelStream().mapToInt(gamer -> {
			try {
				if (getRemoteClient(gamer).vote(x1, y1, x2, y2, grid)) {
					return 1;
				} else {
					return 0;
				}
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + gamer.toString() + " is disconnected\n during voting");
				e.printStackTrace();
			}
			return -1 - gamers.size();
		}).sum();

		if (count >= gamers.size() / 2) {
			int score = (x1 == x2) ? 1 + Math.abs(y1 - y2) : 1 + Math.abs(x1 - x2);
			users.put(user, users.get(user) + score);
		}

		try {
			if (count >= 0) {
				int index = gamers.indexOf(user);
				user = gamers.get(index == gamers.size() - 1 ? 0 : index + 1);
				getRemoteClient(user).startNewTurn();
				return true;
			}
		} catch (NotBoundException | RemoteException e) {
			System.out.println("Gamer: " + user.toString() + " is disconnected\n");
			e.printStackTrace();
		}

		gracefulShutDown();
		return false;

	}

	public boolean pass(User user) {
		passCount++;
		try {
			if (passCount < gamers.size()) {
				int index = gamers.indexOf(user);
				user = gamers.get(index == gamers.size() - 1 ? 0 : index + 1);
				getRemoteClient(user).startNewTurn();
				return true;
			}
		} catch (NotBoundException | RemoteException e) {
			System.out.println("Gamer: " + user.toString() + " is disconnected\n");
			e.printStackTrace();
		}
		gracefulShutDown();
		return false;
	}

	private IRemoteClient getRemoteClient(User user) throws RemoteException, NotBoundException {
		return (IRemoteClient) LocateRegistry.getRegistry(user.getIp(), user.getPort()).lookup(user.getName());
	}

	private void showActiveUsers() {
		Set<User> disconnectedUsers = users.keySet().parallelStream().map(user -> {
			if (startFlag == false || !gamers.contains(user)) {
				try {
					getRemoteClient(user).showUserList(users.keySet());
				} catch (RemoteException | NotBoundException e) {
					System.out.println("User: " + user.toString() + " is disconnected\n");
					e.printStackTrace();
					return user;
				}
			}
			return null;
		}).filter(i -> i != null).collect(Collectors.toSet());

		if (disconnectedUsers.size() > 0) {
			for(User user : disconnectedUsers) {
				users.remove(user);
			}
			showActiveUsers();
		}
	}

	private void gracefulShutDown() {
		gamers.parallelStream().forEach(gamer -> {
			try {
				getRemoteClient(gamer).endGame(users.get(gamer));
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Gamer: " + gamer.toString() + " is disconnected\n");
				users.remove(gamer);
				e.printStackTrace();
			}
		});
		showActiveUsers();
		startFlag = false;
	}

}
