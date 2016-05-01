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
	
	// a counter for server to know if all clients had finished
	private static volatile int clientsToWait;

	public static void main(String[] args) {
			
		clientsToWait = Constants.NUM_OF_CLIENTS;
		clientCarts = new HashMap<Integer, HashMap<Integer, Integer>>();
		initInventory();
		System.out.println("Inventory is initialized");
		try {
			serverSocket = new ServerSocket(9999);
			// since accept in blocking, every second (timeout) check condition again
			// all of this is in order to see if all clients had finished
			serverSocket.setSoTimeout(1000);
			while (clientsToWait > 0 ){
				try {
					Socket s = serverSocket.accept();
					// if accepted - create a handler in a new thread and execute it.
					ClientHandler ch = new ClientHandler(s);
					Thread t = new Thread(ch);
					t.start();
					// if reached here will return to listen for more requests
				}
				catch (SocketTimeoutException e){
					// do nothing - just continue with the loop
				}
			}
			// if reached here - all clients are finished
			System.out.println("...All Clients Are Finished...\n" + Inventory.getInventory().toString());
			
		} catch (IOException e) {
			System.out.println("ERROR: a server communication error has occured...");
			e.printStackTrace();
		}
		
		
	}	
	// We've synchronized all of the following methods (exept init) in order to keep data integrity
		
	// adds an item to the client's cart
	public static synchronized void addItem(int clientNumber, int itemNumber, int quantity) {
		HashMap<Integer, Integer> clientCart = getClientCart(clientNumber);
		// if client isn't yet registered at all create an entry for him
		if (clientCart == null){
			clientCart = new HashMap<Integer, Integer>();
			clientCarts.put(clientNumber, clientCart);
		}
		// as specified in forum - if already exists - add to quantity
		else if (clientCart.containsKey(itemNumber)){
			quantity += clientCart.get(itemNumber);
		}
		// put item and quantity in client cart
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
		// get new approval for all items
		for (Entry<Integer, Integer> entry : clientCart.entrySet()){
			res = Inventory.getInventory().checkIngredient(entry.getKey(), entry.getValue());
			// if an item isn't approved - return false
			if (false == res){
				return res;
			}
		}
		// approval given remove all item in cart from inventory
		for (Entry<Integer, Integer> entry : clientCart.entrySet()){
			Inventory.getInventory().removeIngredient(entry.getKey(), entry.getValue());
		}
		return res;
	}
	
	// when client finishes his work we decrement the waiting counter
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
