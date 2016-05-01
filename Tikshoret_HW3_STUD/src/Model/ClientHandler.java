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
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			boolean notFinished = true;
			while (notFinished) {
				String[] request = dis.readUTF().split(" ");
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
					dos.writeUTF(response);
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
					dos.writeUTF(response);
					break;
					
				case "FIN":
					System.out.println("closing socket for client " +  clientNum + " in Server - client finished");
					Server.clientFinished();
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
