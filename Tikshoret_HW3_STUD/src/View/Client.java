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
			clientSocket = new Socket("127.0.0.1", 9999);
			dos = new DataOutputStream(clientSocket.getOutputStream());
			dis = new DataInputStream(clientSocket.getInputStream());
			while (finished == false)
			{
				myOrder.clear();
				int appemptsForCurrent = 0;
				int ingNum = getRandomPartNum();
				int ingQnt = getRandomQuantity(Constants.INIT_QTY);
				while (myOrder.size() < Constants.ITEMS_IN_ORD) {
					appemptsForCurrent++;
					String[] response = sendRequest(ingNum, ingQnt);
					// Do we need to check for matches in ORD/BUY/FIN, and Ing Number ????
					if (response[0].equals("ACK")){
						if (myOrder.containsKey(ingNum)){
							ingQnt += myOrder.get(ingNum);
						}
						myOrder.put(ingNum, ingQnt);							
						ingNum = getRandomPartNum();
						ingQnt = getRandomQuantity(Constants.INIT_QTY);
						appemptsForCurrent = 0;
					}
					else {
						int availableQnt = Integer.parseInt(response[3]);
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
				finished = sendBUY();				
			}
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
		String[] buyResponse = dis.readUTF().split(" ");
		return buyResponse[0].equals("ACK");		
	}

	// sends an ORD REQUEST message for a single ingredient and returns the server's RESPONSE message
	private String[] sendRequest(int ingredientNumber, int quantity) throws IOException {
		String ordRequest = clientNum + " ORD " + ingredientNumber + " " + quantity;	
		dos.writeUTF(ordRequest);	
		String[] orderResponse = dis.readUTF().split(" ");
		// do we need to check "ORD" ? not much use for it....
		
		return orderResponse;
	}
	
	// send a FIN REQUEST message to the server and closes the server socket
	private void sendFIN() throws IOException {
		String finRequest = clientNum + " FIN ";		
		dos.writeUTF(finRequest);
	}

	public void printOrder() {
		String output = "\nClient No.: " + clientNum + "\n---------------\n";
		for (Map.Entry<Integer, Integer> ingredient : myOrder.entrySet()) {
			output += "Ingredient: " + Client_Server.getIngredientName(ingredient.getKey()) + ", Quantity: " + ingredient.getValue() + "\n";
		}
		System.out.println(output);
	}
}
