package client.agent;

import java.rmi.Remote;
import java.util.HashSet;

import entity.User;

public interface IRemoteScrabble extends Remote {
	public void refreshGrid(char[][] grid);
	public void startNewTurn();
	public void endGame(int score);
	public void showUserList(HashSet<User> users);
	public void claim();
	public boolean vote();
}
