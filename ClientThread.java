package heaven.nchat.server;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
	Socket clientSocket;
	boolean noErrors = true;
	String username = "";
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
    	try {
			put.write((byte) 0x04);
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
    
    public void broadcastMessage(String msg)
    {
    	for (ClientThread cThread : Server.clientThreads)
		{
			boolean result = cThread.sendMessage(msg);
			if (result == false)
			{
				Server.clientThreads.remove(cThread);
			}
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
		    while(noErrors)
		    {
		    	
		    	if (ins.available() > 0)
		    	{
		    		packetId = ins.read();
				    System.out.println("Received packet id - "+packetId);
		    		
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
							
							this.sendMessage(Server.welcome);
							broadcastMessage("** "+username+" has joined the server");
							
							
							put.flush();
							break;
						case 0x03:
							short msgLenght = in.readShort();
							String msg = "";
							for (int i = msgLenght; i > 0; i--)
							{
								msg += in.readChar();
							}
							broadcastMessage("<"+username+">"+msg);
							break;
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
		} catch (Exception e) {
		    System.out.println("We haz a problem :C (someone disconnected?)");
		    //e.printStackTrace();
		}
    	
    }
}
