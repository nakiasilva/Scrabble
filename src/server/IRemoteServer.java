package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import entity.User;

public interface IRemoteServer extends Remote {
	public void login(User newUser) throws RemoteException;

	public void logoff(User expiredUser) throws RemoteException;

	public boolean startGame(List<User> newGamers) throws RemoteException;

	public boolean placeChar(int x, int y, char c, User user) throws RemoteException;

	public boolean vote(int x1, int y1, int x2, int y2, User user) throws RemoteException;

	public boolean pass(User user) throws RemoteException;

}
