package client.agent;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import entity.User;

public class ClientAgent {

	public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException {
		User user = new User("localhost", "david", 1635);
		RemoteClient client = new RemoteClient(LocateRegistry.getRegistry("localhost", 19140), user);
		LocateRegistry.createRegistry(user.getPort());
		LocateRegistry.getRegistry(user.getIp(), user.getPort()).bind(user.getName(), client);
		client.login();
	}
}
