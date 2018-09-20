package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import client.agent.RemoteScrabble;
import entity.User;

@SuppressWarnings("serial")
public class ScrabbleServer extends UnicastRemoteObject implements IScrabbleServer {
	private ServerSocket server;
	private char[][] grid;
	private HashSet<User> users;
	private LinkedHashMap<User, Integer> gamers;
	private boolean startFlag;
	private int passCount;

	public ScrabbleServer(int port) throws IOException, SQLException {
		server = new ServerSocket(port);
		grid = new char[20][20];
		users = new HashSet<User>();
		gamers = new LinkedHashMap<User, Integer>();
		startFlag = false;
		passCount = 0;

	}

	public void login(String name, String ip) throws AccessException, RemoteException, NotBoundException {
		users.add(new User(name, ip));
		for (User user : users) {
			if (startFlag == false || !gamers.containsKey(user)) {
				((RemoteScrabble) LocateRegistry.getRegistry(user.getIp()).lookup(user.getUserName()))
						.showUserList(users);
			}
		}
	}

	public void logoff(String name, String ip) throws AccessException, RemoteException, NotBoundException {
		users.remove(new User(name, ip));
		for (User user : users) {
			if (startFlag == false || !gamers.containsKey(user)) {
				((RemoteScrabble) LocateRegistry.getRegistry(user.getIp()).lookup(user.getUserName()))
						.showUserList(users);
			}
		}
	}

	public boolean startGame(List<User> newGamers) throws AccessException, RemoteException, NotBoundException {
		if (startFlag)
			return false;

		grid = new char[20][20];
		gamers = new LinkedHashMap<User, Integer>();
		startFlag = true;
		passCount = 0;
		for (User newGamer : newGamers) {
			gamers.put(newGamer, 0);
			((RemoteScrabble) LocateRegistry.getRegistry(newGamer.getIp()).lookup(newGamer.getUserName()))
					.refreshGrid(grid);
		}
		((RemoteScrabble) LocateRegistry.getRegistry(newGamers.get(0).getIp()).lookup(newGamers.get(1).getUserName()))
				.startNewTurn();
		return true;
	}

	public void placeChar(int x, int y, char c, User user) throws AccessException, RemoteException, NotBoundException {
		grid[x][y] = c;
		for (Entry<User, Integer> gamer : gamers.entrySet()) {
			((RemoteScrabble) LocateRegistry.getRegistry(gamer.getKey().getIp()).lookup(gamer.getKey().getUserName()))
					.refreshGrid(grid);
		}
		passCount = 0;
		((RemoteScrabble) LocateRegistry.getRegistry(user.getIp()).lookup(user.getUserName())).claim();
	}

	public void vote(int x1, int y1, int x2, int y2, User user) throws AccessException, RemoteException, NotBoundException {
		int count = 0;
		for (Entry<User, Integer> gamer : gamers.entrySet()) {
			if(((RemoteScrabble) LocateRegistry.getRegistry(gamer.getKey().getIp()).lookup(gamer.getKey().getUserName())).vote()) {
				count++;
			}
		}
		if(count>=gamers.size()/2) {
			int score = (x1 == x2) ? 1 + Math.abs(y1-y2) : 1 + Math.abs(x1-x2);
			gamers.put(user, gamers.get(user) + score);
		}
	}

	public void pass() throws AccessException, RemoteException, NotBoundException {
		passCount++;
		if(passCount==gamers.size()) {
			startFlag = false;
			passCount = 0;
			for (Entry<User, Integer> gamer : gamers.entrySet()) {
				((RemoteScrabble) LocateRegistry.getRegistry(gamer.getKey().getIp()).lookup(gamer.getKey().getUserName())).endGame(gamer.getValue());
			}
		}
	}

//	public void handle() {
//
//		try {
	// Wait and accept a connection
//			Socket socket = server.accept();
//			InputStream s1In = socket.getInputStream();
//			DataInputStream dis = new DataInputStream(s1In);
//			JSONObject request = new JSONObject(dis.readUTF());
//
//			// Parse client requst
//			String type = request.getString("type");
//			String name = request.getJSONObject("user").getString("name");
//			String ip = request.getJSONObject("user").getString("ip");
//
//			switch (type) {
//
//			case "login":
//				users.add(new User(name, ip));
//				for (User user : users) {
//					if (startFlag == false || !gamers.containsKey(new User(name, ip))) {
//						((RemoteScrabble) registry.lookup(user.getIp())).showUserList(users);
//					}
//				}
//				break;
//
//			case "logoff":
//				users.remove(new User(name, ip));
//				for (User user : users) {
//					if (startFlag == false || !gamers.containsKey(new User(name, ip))) {
//						((RemoteScrabble) registry.lookup(user.getIp())).showUserList(users);
//					}
//				}
//				break;

//			case "startGame":
//				grid = new String[20][20];
//				gamers = new LinkedHashMap<User, Integer>();
//				startFlag = true;
//				passCount = 0;
//				gamers.put(new User(name, ip), 0);
//
//				JSONArray jsonArray = request.getJSONArray("gamers");
//				for (int i = 0; i < jsonArray.length(); i++) {
//					String userName = jsonArray.getJSONObject(i).getString("name");
//					String userIp = jsonArray.getJSONObject(i).getString("ip");
//					gamers.put(new User(userName, userIp), 0);
//					RemoteScrabble remoteClient = (RemoteScrabble) registry
//							.lookup(jsonArray.getJSONObject(i).getString("ip"));
//					remoteClient.refreshGrid(grid);
//				}
//				RemoteScrabble remoteClient = (RemoteScrabble) registry.lookup(ip);
//				remoteClient.refreshGrid(grid);
//				remoteClient.startNewTurn();
//				break;

//			case "placeChar":
//				int x = request.getInt("x");
//				int y = request.getInt("y");
//				String letter = request.getString("char");
//				grid[x][y] = letter;
//				for (Entry<User, Integer> gamer : gamers.entrySet()) {
//					((RemoteScrabble) registry.lookup(gamer.getKey().getIp())).refreshGrid(grid);
//				}
//				((RemoteScrabble) registry.lookup(ip)).startVoting();
//				break;
//
//			case "startVoting":
//				for (Entry<User, Integer> gamer : gamers.entrySet()) {
//					((RemoteScrabble) registry.lookup(gamer.getKey().getIp())).vote();
//				}
//				break;
//
//			case "vote":
//
//			case "pass":
//				break;
//
//			default:
//				System.out.println("Illegal request type");
//				break;
//			}
//
//			// Close the connection
//			dis.close();
//			s1In.close();
//			socket.close();
//		} catch (IOException | JSONException | NotBoundException e) {
//			e.printStackTrace();
//		}
//
//	}

}
