package client.agent;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import entity.User;

public interface IRemoteClient extends Remote {
	public void refreshGrid(char[][] grid) throws RemoteException;
	public void startNewTurn() throws AccessException, RemoteException, NotBoundException;
	public void endGame(int score) throws RemoteException;
	public void showUserList(Set<User> users) throws AccessException, RemoteException, NotBoundException;
	public void claim() throws AccessException, RemoteException, NotBoundException;
	public boolean vote(int x1, int y1, int x2, int y2, char[][] grid) throws RemoteException;
}
