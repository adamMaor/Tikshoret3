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
		try {
			serverSocket = new ServerSocket(9999);
			while (true){
				Socket s = serverSocket.accept();
				ClientHandler ch = new ClientHandler(s);
				Thread t = new Thread(ch);
				t.start();
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}	
		
	// adds an item to the client's cart
	public static synchronized void addItem(int clientNumber, int itemNumber, int quantity) {
		HashMap<Integer, Integer> clientOrder = getClientCart(clientNumber);
		if (clientOrder == null){
			clientOrder = new HashMap<Integer, Integer>();
			clientCarts.put(clientNumber, clientOrder);
		}
		clientOrder.put(itemNumber, quantity);
	}
	
	// removes the client's entire cart
	public static synchronized void removeCart(int clientNumber) {
		clientCarts.remove(clientNumber);
	}
	
	// gets a client's cart
	public static synchronized HashMap<Integer, Integer> getClientCart(int clientNumber) {
		return clientCarts.get(clientNumber);		
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
