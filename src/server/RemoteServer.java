package server;

import java.io.IOException;
import java.rmi.AccessException;
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
	private HashMap<User,Integer> scores;
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

	public void login(User newUser) throws AccessException, RemoteException, NotBoundException {
		users.add(newUser);
		for (User user : users) {
			if (startFlag == false || !gamers.contains(user)) {
				((IRemoteClient) LocateRegistry.getRegistry(user.getIp(), user.getPort()).lookup(user.getName()))
						.showUserList(users);
			}
		}
	}

	public void logoff(User expiredUser) throws AccessException, RemoteException, NotBoundException {
		users.remove(expiredUser);
		for (User user : users) {
			if (startFlag == false || !gamers.contains(user)) {
				((IRemoteClient) LocateRegistry.getRegistry(user.getIp(), user.getPort()).lookup(user.getName()))
						.showUserList(users);
			}
		}
	}

	public boolean startGame(List<User> newGamers) throws AccessException, RemoteException, NotBoundException {
		if (startFlag)
			return false;

		grid = new char[20][20];
		gamers = new ArrayList<User>();
		startFlag = true;
		passCount = 0;
		for (User newGamer : newGamers) {
			scores.put(newGamer, 0);
			gamers.add(newGamer);
			((IRemoteClient) LocateRegistry.getRegistry(newGamer.getIp(), newGamer.getPort())
					.lookup(newGamer.getName())).refreshGrid(grid);
		}
		((IRemoteClient) LocateRegistry.getRegistry(newGamers.get(0).getIp(), newGamers.get(0).getPort())
				.lookup(newGamers.get(0).getName())).startNewTurn();
		return true;
	}

	public void placeChar(int x, int y, char c, User user) throws AccessException, RemoteException, NotBoundException {
		grid[x][y] = c;
		for (User gamer : gamers) {
			((IRemoteClient) LocateRegistry.getRegistry(gamer.getIp(), gamer.getPort()).lookup(gamer.getName()))
					.refreshGrid(grid);
		}
		passCount = 0;
		((IRemoteClient) LocateRegistry.getRegistry(user.getIp(), user.getPort()).lookup(user.getName())).claim();
	}

	public void vote(int x1, int y1, int x2, int y2, User user)
			throws AccessException, RemoteException, NotBoundException {
		int count = 0;
		for (User gamer : gamers) {
			if (((IRemoteClient) LocateRegistry.getRegistry(gamer.getIp(), gamer.getPort()).lookup(gamer.getName()))
					.vote(x1,y1,x2,y2,grid)) {
				count++;
			}
		}
		if (count >= gamers.size() / 2) {
			int score = (x1 == x2) ? 1 + Math.abs(y1 - y2) : 1 + Math.abs(x1 - x2);
			scores.put(user, scores.get(user)+score);
		}
		int index = gamers.indexOf(user);
		User nextGamer = gamers.get(index == gamers.size() - 1 ? 0 : index + 1);
		((IRemoteClient) LocateRegistry.getRegistry(nextGamer.getIp(), nextGamer.getPort()).lookup(nextGamer.getName()))
				.startNewTurn();
	}

	public void pass(User user) throws AccessException, RemoteException, NotBoundException {
		passCount++;
		if (passCount == gamers.size()) {
			startFlag = false;
			passCount = 0;
			for (User gamer : gamers) {
				((IRemoteClient) LocateRegistry.getRegistry(gamer.getIp(), gamer.getPort()).lookup(gamer.getName()))
						.endGame(scores.get(user));
			}
		} else {
			int index = gamers.indexOf(user);
			User nextGamer = gamers.get(index == gamers.size() - 1 ? 0 : index + 1);
			((IRemoteClient) LocateRegistry.getRegistry(nextGamer.getIp(), nextGamer.getPort())
					.lookup(nextGamer.getName())).startNewTurn();

		}
	}

}
