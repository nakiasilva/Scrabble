package client.agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientAgent {
	private RemoteScrabble remoteScrabble;
	private String ip;
	private int port;
	
	public String sendRequest(JSONObject json) throws IOException, JSONException {
		Socket socket = new Socket("localhost", port);
		// Get a communication stream associated with the socket
		OutputStream s1out = socket.getOutputStream();
		DataOutputStream dos = new DataOutputStream(s1out);
		// Send a Json
		dos.writeUTF(json.toString());

		// Get an input file handle from the socket and read the input
		InputStream s1In = socket.getInputStream();
		DataInputStream dis = new DataInputStream(s1In);
		String response = new String(dis.readUTF());
		// Close the connection
		dos.close();
		s1out.close();
		dis.close();
		s1In.close();
		socket.close();
		return response;
	}
	
	public void initialize() throws IOException {
		//check if the host port is alive
		Socket socket = new Socket("localhost", port);
		socket.close();
	}

}
