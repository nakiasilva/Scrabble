package client.agent;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import entity.User;
import server.IRemoteServer;

@SuppressWarnings("serial")
public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {
	private Registry registry;
	private User user;
	
	public RemoteClient( Registry registry, User user) throws RemoteException {
		this.registry = registry;
		this.user = user;
	}
	
	
	public void login() throws AccessException, RemoteException, NotBoundException {
		((IRemoteServer)registry.lookup("server")).login(user);
	}
	
	public void logoff() throws AccessException, RemoteException, NotBoundException {
		((IRemoteServer)registry.lookup("server")).logoff(user);
	}

	@Override
	public void refreshGrid(char[][] grid) {
		for(char[] i : grid) {
			for(char j : i) {
				System.out.println(j=='\u0000'?"": j);
			}
		}
	}

	@Override
	public void startNewTurn() throws AccessException, RemoteException, NotBoundException {
		//fill a letter in an empty block
		((IRemoteServer)registry.lookup("server")).placeChar(1, 3, 'A', user);
		//((IRemoteServer)registry.lookup("server")).pass();
	}

	@Override
	public void endGame(int score) {
		System.out.println(score);
	}

	@Override
	public void showUserList(HashSet<User> users) throws AccessException, RemoteException, NotBoundException {
		//show List of uers
		for(User user : users) {
			System.out.println(user);
		}

		//start new game
		List<User> gamers = new ArrayList<User>();
		for(User user : users) {
			gamers.add(user);
		}
		((IRemoteServer)registry.lookup("server")).startGame(gamers);
	}

	@Override
	public void claim() throws AccessException, RemoteException, NotBoundException {
		//claim a word 
		
		//start voting
		((IRemoteServer)registry.lookup("server")).vote(1, 2, 1, 5, user);
	}

	@Override
	public boolean vote() {
		//a button for votte
		return false;
	}

}
