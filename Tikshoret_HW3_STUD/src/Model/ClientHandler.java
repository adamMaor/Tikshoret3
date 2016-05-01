package Model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
	this class will handle a client from start to end
 */
public class ClientHandler implements Runnable {
	
	private Socket socket;
	/**
	 * @param socket
	 */
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			boolean notFinished = true;
			// we will wait and read from InputStream until client is done
			while (notFinished) {
				String[] request = dis.readUTF().split(" ");
				int clientNum = Integer.parseInt(request[0]); 
				String response = "";
				switch (request[1]) {
				
				case "ORD":
					int ingNum = Integer.parseInt(request[2]);
					int ingQnt = Integer.parseInt(request[3]);
					// get the current amount left in inventory
					int amountInInventory = Inventory.getInventory().amountLeft(ingNum);
					// if OK - add to cart
					if (amountInInventory >= ingQnt) {
						response += "ACK";
						Server.addItem(clientNum, ingNum, ingQnt);
					}
					// if not OK 
					else {
						response += "NACK"; 
					}
					response += " # " + ingNum + " " + amountInInventory;
					dos.writeUTF(response);
					break;
					
				case "BUY":
					// check if cart is approved in server (synced)
					if (true == Server.checkOutClient(clientNum)){
						response += "ACK";
					}
					else {
						response += "NACK"; 				
					}
					response += " ORD";
					// anyway remove the cart
					Server.removeCart(clientNum);
					dos.writeUTF(response);
					break;
					
				case "FIN":
					System.out.println("closing socket for client " +  clientNum + " in Server - client finished");
					Server.clientFinished();
					// we close the socket only in server
					dis.close();
					dos.close();
					socket.close();
					notFinished = false;
					break;
				}
			}
			
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
