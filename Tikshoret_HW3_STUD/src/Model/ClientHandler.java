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
		// TODO Auto-generated method stub
		
	}

	
}
