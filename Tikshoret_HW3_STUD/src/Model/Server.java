package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	private static ServerSocket serverSocket;
	/*
	 * hashmap of orders<client,order> and order is <partNum,quantity>
	 */
	private static HashMap<Integer, HashMap<Integer, Integer>> clientCarts;

	public static void main(String[] args) {
				
		clientCarts = new HashMap<>();
		initInventory();
		System.out.println("Inventory is initialized");
		
		
	// adds an item to the client's cart
	public static void addItem(int clientNumber, int itemNumber, int quantity) {

	}
	
	// removes the client's entire cart
	public static void removeCart(int clientNumber) {

	}
	
	// gets a client's cart
	public static HashMap<Integer, Integer> getClientCart(int clientNumber) {
	}
	
	private static void initInventory(){
		String part[] = {};
		
		try (BufferedReader br = new BufferedReader(new FileReader("inventory.txt"))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				part = sCurrentLine.split(" ");
				Inventory.getInventory().addIngredient(new Ingredient(Integer.parseInt(part[0]), part[1], Integer.parseInt(part[2])));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	}
}
