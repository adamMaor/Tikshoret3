package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map.Entry;

import View.Constants;

public class Server {
	private static ServerSocket serverSocket;
	/*
	 * hashmap of orders<client,order> and order is <partNum,quantity>
	 */
	private static HashMap<Integer, HashMap<Integer, Integer>> clientCarts;
	
	private static volatile int clientsToWait;

	public static void main(String[] args) {
			
		clientsToWait = Constants.NUM_OF_CLIENTS;
		clientCarts = new HashMap<>();
		initInventory();
		System.out.println("Inventory is initialized");
		try {
			serverSocket = new ServerSocket(9999);
			serverSocket.setSoTimeout(50);
			while (clientsToWait > 0 ){
				try {
					Socket s = serverSocket.accept();					
					ClientHandler ch = new ClientHandler(s);
					Thread t = new Thread(ch);
					t.start();
				}
				catch (SocketTimeoutException e){
					// do nothing - just continue with the loop
				}
			}
			
			System.out.println("...All Clients Are Finished...\n" + Inventory.getInventory().toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}	
		
	// adds an item to the client's cart
	public static synchronized void addItem(int clientNumber, int itemNumber, int quantity) {
		HashMap<Integer, Integer> clientCart = getClientCart(clientNumber);
		if (clientCart == null){
			clientCart = new HashMap<Integer, Integer>();
			clientCarts.put(clientNumber, clientCart);
		}
		else if (clientCart.containsKey(itemNumber)){
			quantity += clientCart.get(itemNumber);
		}
		clientCart.put(itemNumber, quantity);
	}
	
	// removes the client's entire cart
	public static synchronized void removeCart(int clientNumber) {
		clientCarts.remove(clientNumber);
	}
	
	// gets a client's cart
	public static synchronized HashMap<Integer, Integer> getClientCart(int clientNumber) {
		return clientCarts.get(clientNumber);		
	}
	
	// checks all item in cart and if all exist - removes from Inventory
	public static synchronized boolean checkOutClient(int clientNum){
		boolean res = true;
		HashMap<Integer, Integer> clientCart = getClientCart(clientNum);
		for (Entry<Integer, Integer> entry : clientCart.entrySet()){
			res = Inventory.getInventory().checkIngredient(entry.getKey(), entry.getValue());
			if (false == res){
				return res;
			}
		}
		for (Entry<Integer, Integer> entry : clientCart.entrySet()){
			Inventory.getInventory().removeIngredient(entry.getKey(), entry.getValue());
		}
		return res;
	}
	
	public static synchronized void clientFinished(){
		clientsToWait--;
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
