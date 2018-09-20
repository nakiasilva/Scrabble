package client.agent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;

import client.gui.ClientGUI;
import entity.User;

@SuppressWarnings("serial")
public class RemoteScrabble extends UnicastRemoteObject implements IRemoteScrabble {

	private ClientGUI clientGUI;
	private User user;

	public RemoteScrabble(ClientGUI clientGUI, User user) throws RemoteException {
		super();
		this.clientGUI = clientGUI;
		this.user = user;
	}

	@Override
	public void refreshGrid(char[][] grid) {
		clientGUI.drawGrid(grid);
	}

	@Override
	public void startNewTurn() {
		clientGUI.placeChar();
	}

	@Override
	public void endGame(int score) {
		clientGUI.showScore(score);
	}

	@Override
	public void showUserList(HashSet<User> users) {
		clientGUI.showUserList(users);
	}

	@Override
	public void claim() {
		clientGUI.claim();
		
	}

	@Override
	public boolean vote() {
		// TODO Auto-generated method stub
		return clientGUI.vote();
	}
	
	

}
