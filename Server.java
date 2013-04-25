package heaven.nchat.server;

import java.net.*;
//import java.security.*;
import java.util.ArrayList;


public class Server {
	
	static ServerSocket serverSocket;
	public static ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();
	public static String motd = "§1"+"\0"+"1"+"\0"+"0.5.0"+"\0"+"nchat test server"+"\0"+"0"+"\0"+"0";
	public static String welcome = "Welcome on ncraft test server";
	public static ArrayList<String> clientList = new ArrayList<String>();
	// 
	/**
	 * @param args
	 */
	
	public static String[] listUsers()
	{
		ArrayList<String> users = new ArrayList<String>();
		for (ClientThread cThread : Server.clientThreads)
		{
			
			if (cThread.clientSocket.isClosed())
			{
				
				Server.clientThreads.remove(cThread);
			}
			users.add(cThread.username);
		}
		return (String[]) users.toArray();
	}
	
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
	public static void broadcastMessage(String msg)
	{
		broadcastMessage(msg, (byte)0x04);
	}
	 public static void broadcastMessage(String msg, byte type)
	    {
		 if (type==0x04)
		 {
			 System.out.println(msg);
		 }
	    	for (ClientThread cThread : clientThreads)
			{
	    		if (cThread.noErrors == true)
	    		{
		    		try 
		    		{
		    			if (cThread.clientSocket.isClosed())
						{
							clientThreads.remove(cThread);
						}
						boolean result = cThread.sendMessage(msg, type);
						if (result == false)
						{
							clientThreads.remove(cThread);
						}
		    			
		    		} catch (Exception e)
		    		{
		    			try 
			    		{
			    			clientThreads.remove(cThread);
			    			
			    		} catch (Exception e2)
			    		{
							
							
			    			
			    		}
		    		}
	    		}
				
			}	
	    	
	    }

}
