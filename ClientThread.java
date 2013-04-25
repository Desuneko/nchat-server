package heaven.nchat.server;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
	Socket clientSocket;
	boolean noErrors = true;
	public String username = "";
	String serverId = "";
	int protocolVersion = 0;
	OutputStream outs;
	BufferedOutputStream out;
	DataOutputStream put;
	InputStream ins;
	DataInputStream in;
	
    ClientThread(Socket client) {
        this.clientSocket = client;
    }
    public synchronized boolean sendMessage(String msg)
    {
    	return sendMessage(msg, (byte)0x04);
    
    }
    public synchronized boolean sendMessage(String msg, byte type)
    {
    	try {
			put.write((byte) type);
			put.writeShort(msg.length());
			put.writeChars(msg);
			put.flush();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    
   
    
    public void run() {
    	try {
    		outs = clientSocket.getOutputStream();
    		out = new BufferedOutputStream(outs);
    		put = new DataOutputStream(out);
    		ins = clientSocket.getInputStream();
    		in = new DataInputStream(ins);
			int packetId = 0;
			long lastReadTime = System.currentTimeMillis();
		    while(noErrors)
		    {
		    	if ((clientSocket.isClosed()) || (!clientSocket.isConnected()))
		    	{
		    		if (username != "")
		    		{
		    			String kick_msg = "You have been disconnected from server.";
		    			put.write((byte) 0xFF);
						put.writeShort(kick_msg.length());
						put.writeChars(kick_msg);
						put.flush();
		    			Server.clientList.remove(username);
		    			Server.broadcastMessage("** "+username+" has disconnected from server");
		    			Server.clientThreads.remove(this);
		    		}
		    		noErrors = false;
		    		break;
		    	}
		    	
		    	if ((System.currentTimeMillis() - lastReadTime) > 2000)
		    	{
		    		//System.out.println("Sent keepalive");
		    		put.write((byte) 0x01);
					put.flush();
					lastReadTime = System.currentTimeMillis();
					
		    	}
		    	if (ins.available() > 0)
		    	{
		    		packetId = ins.read();
				    //System.out.println("Received packet id - "+packetId);
		    		
					switch(packetId)
					{
						case 0xFE:
							if (in.readByte() != 1) {
							    System.out.println("We haz a problem :C (someone disconnected?/someone has wrung nchat?)");
							    noErrors = false;
							} else {
								put.write((byte) 0xFF);
								put.writeShort(Server.motd.length());
								put.writeChars(Server.motd);
								put.flush();
							}
							break;
						case 0x02:
							protocolVersion = in.readByte();
							short usernameLenght = in.readShort();
							username = "";
							for (int i = usernameLenght; i > 0; i--)
							{
								username += in.readChar();
							}
							Server.clientList.remove(username);
							Server.clientList.add(username);
							this.sendMessage(Server.welcome);
							Server.broadcastMessage("** "+username+" has joined the server");
							Server.broadcastMessage(username, (byte)0x06);
							put.write((byte) 0x04);
							String userString = "";
							for (String username : Server.clientList)
							{
								if (userString == "")
								{
									userString = username;
								} else {
									userString = userString + ", " + username;
								}
							}
							userString = "Connected users: "+userString;
							put.writeShort(userString.length());
							put.writeChars(userString);
							put.flush();
							put.write((byte) 0x05);
							userString = "";
							for (String username : Server.clientList)
							{
								if (userString == "")
								{
									userString = username;
								} else {
									userString = userString + "\0" + username;
								}
							}
							put.writeShort(userString.length());
							put.writeChars(userString);
							put.flush();
							break;
						case 0x03:
							short msgLenght = in.readShort();
							String msg = "";
							for (int i = msgLenght; i > 0; i--)
							{
								msg += in.readChar();
							}
							switch (msg)
							{
								case "/who":
									put.write((byte) 0x04);
									String userStrings = "";
									for (String username : Server.clientList)
									{
										if (userStrings == "")
										{
											userStrings = username;
										} else {
											userStrings = userStrings + ", " + username;
										}
									}
									userStrings = "Connected users: "+userStrings;
									put.writeShort(userStrings.length());
									put.writeChars(userStrings);
									put.flush();
									break;
								default:
									Server.broadcastMessage("<"+username+">"+msg);
									break;
							}
					}
		    	}
		    }
		    put.close();
		    in.close();
		    ins.close();
		    out.close();
		    outs.close();
		    clientSocket.close();
		    Server.clientThreads.remove(this);
    	} catch (EOFException e) {
    		if (username != "")
    		{
    			noErrors = false;
    			Server.clientList.remove(username);
    			Server.broadcastMessage("** "+username+" has disconnected from server");
    			Server.broadcastMessage(username, (byte)0x07);
    			Server.clientThreads.remove(this);
    		}
		} catch (SocketException e) {
    		if (username != "")
    		{
    			noErrors = false;
    			Server.clientList.remove(username);
    			Server.broadcastMessage("** "+username+" has disconnected from server");
    			Server.broadcastMessage(username, (byte)0x07);
    			Server.clientThreads.remove(this);
    		}
		} catch (Exception e) {
		    System.out.println("We haz a problem :C (someone disconnected?)");
		    e.printStackTrace();
		}
    	
    }
}
