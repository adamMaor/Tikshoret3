package Model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

/**

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
		DataInputStream dis;
		try {
			dis = new DataInputStream(socket.getInputStream());
			String[] request = dis.readUTF().split(" ");
			dis.close();
			
			int clientNum = Integer.parseInt(request[0]); 
			String response = "";
			
			switch (request[1]) {
			
			case "ORD":
				int ingNum = Integer.parseInt(request[2]);
				int ingQnt = Integer.parseInt(request[3]);
				int amountInInventory = Inventory.getInventory().amountLeft(ingNum);
				if (amountInInventory >= ingQnt) {
					response += "ACK";
					Server.addItem(clientNum, ingNum, ingQnt);
				}
				else {
					response += "NACK"; 
				}
				response += " # " + ingNum + " " + amountInInventory;
				sendResponse(response);
				break;
				
			case "BUY":
				if (true == Server.checkOutClient(clientNum)){
					response += "ACK";
				}
				else {
					response += "NACK"; 				
				}
				response += " ORD";
				Server.removeCart(clientNum);
				sendResponse(response);
				break;
				
			case "FIN":
				socket.close();	
				Server.clientFinished();
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendResponse(String response) throws IOException {
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.writeUTF(response);
		dos.close();
	}

	
}
