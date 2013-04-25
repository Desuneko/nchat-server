package heaven.nchat.server;

import java.net.*;
//import java.security.*;
import java.util.ArrayList;


public class Server {
	
	static ServerSocket serverSocket;
	public static ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();
	public static String motd = "§1"+"\0"+"1"+"\0"+"0.5.0"+"\0"+"nchat test server"+"\0"+"0"+"\0"+"0";
	public static String welcome = "Welcome on ncraft test server";
	// 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			
			serverSocket = new ServerSocket(14668);
		    while (true)
		    {
		    	Socket client = serverSocket.accept();
		    	ClientThread cThread;
		    	clientThreads.add(cThread = new ClientThread(client));
		    	cThread.start();
		    }
		} 
		catch (Exception e) {
		    System.out.println("Could not listen on port: 14668");
		    System.exit(-1);
		}
	}

}
