package server;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import entity.User;

public interface IRemoteServer extends Remote {
	public void login(User newUser) throws AccessException, RemoteException, NotBoundException;
	
	public void logoff(User expiredUser) throws AccessException, RemoteException, NotBoundException;

	public boolean startGame(List<User> newGamers) throws AccessException, RemoteException, NotBoundException;

	public void placeChar(int x, int y, char c, User user) throws AccessException, RemoteException, NotBoundException;

	public void vote(int x1, int y1, int x2, int y2, User user)
			throws AccessException, RemoteException, NotBoundException;

	public void pass(User user) throws AccessException, RemoteException, NotBoundException;

}
