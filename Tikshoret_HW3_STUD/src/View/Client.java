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
	/** check comment */
	private HashMap<Integer, Integer> myOrder;

	private int clientNum;
	
	private Socket clientSocket;
	private DataOutputStream dos;
	private DataInputStream dis;

	public Client(int clientNum) {
		this.myOrder = new HashMap<Integer, Integer>();
		this.clientNum = clientNum;
		this.clientSocket = null;
	}

	public void run() {		
		boolean finished = false;	
		try {
			// In our implementation we have 1 socket, 1 reader and 1 writer for each client
			clientSocket = new Socket("127.0.0.1", 9999);
			dos = new DataOutputStream(clientSocket.getOutputStream());
			dis = new DataInputStream(clientSocket.getInputStream());
			while (finished == false)
			{
				myOrder.clear();
				// appemptsForCurrent will make sure we only make attempts as required for a specific item
				int appemptsForCurrent = 0;
				int ingNum = getRandomPartNum();
				int ingQnt = getRandomQuantity(Constants.INIT_QTY);
				while (myOrder.size() < Constants.ITEMS_IN_ORD) {
					appemptsForCurrent++;
					String[] response = sendRequest(ingNum, ingQnt);
					// if the response was ACK - then we add the item and quantity to cart
					// then, reset params and go again for next item
					if (response[0].equals("ACK") && response[1].equals("#")){
						// as specified in forum - if already exists - add to current quantity to new one
						if (myOrder.containsKey(ingNum)){
							ingQnt += myOrder.get(ingNum);
						}
						myOrder.put(ingNum, ingQnt);							
						ingNum = getRandomPartNum();
						ingQnt = getRandomQuantity(Constants.INIT_QTY);
						appemptsForCurrent = 0;
					}
					// if the response was NACK - 2 cases:
					else {
						// get number of items available for this product
						int availableQnt = Integer.parseInt(response[3]);
						// availableQnt == 0
						if (availableQnt == 0 || appemptsForCurrent > Constants.ATTMPTS_PER_ITEM){
							ingNum = getRandomPartNum();
							ingQnt = getRandomQuantity(Constants.INIT_QTY);
							appemptsForCurrent = 0;
						}
						else {  // 0 < availableQnt < INIT_QTY
							ingQnt = getRandomQuantity(availableQnt);
						}
					}		
				}
				// when reached here - cart is full.
				// if finished == true - then we're done. else, start over from scratch
				finished = sendBUY();				
			}
			// when reached here - cart was approved. print and send FIN to server
			printOrder();
			sendFIN();
		}catch (IOException e) {
			// server com error
			e.printStackTrace();
		}
	}

	private int getRandomPartNum() {
		return new Random().nextInt(Constants.TOT_PARTS) + 1;
	}

	private int getRandomQuantity(int totalAmount) {
		return new Random().nextInt(totalAmount) + 1;
	}

	// sends a BUY REQUEST message to to server and returns a boolean response
	private boolean sendBUY() throws IOException {
		String buyRequest = clientNum + " BUY ";
		dos.writeUTF(buyRequest);
		// wait for response if ACK return true
		String[] buyResponse = dis.readUTF().split(" ");
		return buyResponse[0].equals("ACK");		
	}

	// sends an ORD REQUEST message for a single ingredient and returns the server's RESPONSE message
	private String[] sendRequest(int ingredientNumber, int quantity) throws IOException {
		String ordRequest = clientNum + " ORD " + ingredientNumber + " " + quantity;	
		dos.writeUTF(ordRequest);
		// wait for response, parse and return
		String[] orderResponse = dis.readUTF().split(" ");
		// do we need to check "ORD" ? not much use for it....
		
		return orderResponse;
	}
	
	// send a FIN REQUEST message to the server and closes the server socket
	private void sendFIN() throws IOException {
		String finRequest = clientNum + " FIN ";		
		dos.writeUTF(finRequest);
		// no need to wait
	}

	public void printOrder() {
		String output = "\nClient No.: " + clientNum + "\n---------------\n";
		for (Map.Entry<Integer, Integer> ingredient : myOrder.entrySet()) {
			output += "Ingredient: " + Client_Server.getIngredientName(ingredient.getKey()) + ", Quantity: " + ingredient.getValue() + "\n";
		}
		System.out.println(output);
	}
}
