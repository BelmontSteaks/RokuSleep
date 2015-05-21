package com.man.wee.rokusleep;

import android.os.AsyncTask;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by mweber on 5/21/2015.
 */
public class Sleep extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        /* create byte arrays to hold our send and response data */
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        TextView statusView = (TextView)findViewById(R.id.statusText);

		/* our M-SEARCH data as a byte array */
        String MSEARCH = "M-SEARCH * HTTP/1.1\nHost: 239.255.255.250:1900\nMan: \"ssdp:discover\"\nST: roku:ecp\n";
        sendData = MSEARCH.getBytes();

        statusView.setText("Creating MSEARCH request to 239.255.255.250 on port 1900...");
		/* create a packet from our data destined for 239.255.255.250:1900 */
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), 1900);

        statusView.append("Sending multicast SSDP MSEARCH request...");
		/* send packet to the socket we're creating */
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.send(sendPacket);

        statusView.append("Waiting for network response...");
		/* receive response and store in our receivePacket */
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

		/* get the response as a string */
        String response = new String(receivePacket.getData());

		/* close the socket */
        clientSocket.close();

		/* parse the IP from the response */
		/* the response should contain a line like:
			Location:  http://192.168.1.9:8060/
		   and we're only interested in the address -- not the port.
		   So we find the line, then split it at the http:// and the : to get the address.
		*/
        response = response.toLowerCase();
        String address = response.split("location:")[1].split("\n")[0].split("http://")[1].split(":")[0].trim();
        statusView.append("Found Roku at " + address);

		/* return the IP */
        return address;
    }
}
