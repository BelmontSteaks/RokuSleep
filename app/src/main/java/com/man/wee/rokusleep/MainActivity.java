package com.man.wee.rokusleep;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String sendRequest(){
        /* create byte arrays to hold our send and response data */
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

		/* our M-SEARCH data as a byte array */
        String MSEARCH = "M-SEARCH * HTTP/1.1\nHost: 239.255.255.250:1900\nMan: \"ssdp:discover\"\nST: roku:ecp\n";
        sendData = MSEARCH.getBytes();

        status("Creating MSEARCH request to 239.255.255.250 on port 1900...");
		/* create a packet from our data destined for 239.255.255.250:1900 */
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), 1900);

        status("Sending multicast SSDP MSEARCH request...");
		/* send packet to the socket we're creating */
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.send(sendPacket);

        status("Waiting for network response...");
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
        status("Found Roku at " + address);

		/* return the IP */
        return address;
    }
}
