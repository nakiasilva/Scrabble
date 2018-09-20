package client.gui;

import java.util.HashSet;

import entity.User;

public class ClientGUI {
	
	//draw 20*20 grid
	public void drawGrid(char[][] grid) {
		for(char[] i : grid) {
			for(char j : i) {
				System.out.println(j=='\u0000'?"null	": j + "	");
			}
			System.out.print("\n");
		}
	}
	
	//place a new char or pass
	public void placeChar() {
		
	}

	//
	
	public void showScore(int socre) {
		System.out.println(socre);
	}
	
	public void showUserList(HashSet<User> users) {
		
	}
	
	public void claim() {
		
	}
	
	public boolean vote() {
		return false;
	}
}
