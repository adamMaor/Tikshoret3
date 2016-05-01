package View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is a client representation. Each instance provides a separate
 * process
 *
 */
public class Client extends Thread {

	private HashMap<Integer, Integer> myOrder;

	private int clientNum;

	public Client(int clientNum) {

	}

	public void run() {

		

	}

	private int getRandomPartNum() {
		return new Random().nextInt(Constants.TOT_PARTS) + 1;
	}

	private int getRandomQuantity(int totalAmount) {
		return new Random().nextInt(totalAmount) + 1;
	}

	// sends a BUY REQUEST message to to server and returns a boolean response
	private boolean sendBUY() throws IOException {
		return false;
		
	}

	// sends an ORD REQUEST message for a single ingredient and returns the server's RESPONSE message
	private String[] sendRequest(int ingredientNumber, int quantity) throws IOException {
		return null;

	}
	
	// send a FIN REQUEST message to the server and closes the server socket
	private void sendFIN() {
		
	}

	public void printOrder() {
		String output = "\nClient No.: " + clientNum + "\n---------------\n";
		for (Map.Entry<Integer, Integer> ingredient : myOrder.entrySet()) {
			output += "Ingredient: " + Client_Server.getIngredientName(ingredient.getKey()) + ", Quantity: " + ingredient.getValue() + "\n";
		}
		System.out.println(output);
	}
}
