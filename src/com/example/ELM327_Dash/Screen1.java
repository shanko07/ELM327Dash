package com.example.ELM327_Dash;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.apache.http.util.EncodingUtils;

import com.example.ELM327_Dash.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Screen1 extends Activity implements OnItemClickListener {
	
	ArrayAdapter<String> listAdapter;
	//Button connectNew;
	ListView listView;
	TextView textView1;
	TextView textView2;
	TextView textView3;
	BluetoothAdapter btAdapter;
	Set<BluetoothDevice> devicesArray;
	ArrayList<String> pairedDevices;
	ArrayList<BluetoothDevice> devices;
	IntentFilter filter;
	BroadcastReceiver receiver;
	String currentMessage;
	ConnectedThread connectedThread;
	int crCounter;
	String[] lines;
	int line;
	boolean didIJustWrite;
	int messsageIndicator;
	String [] commands;
	String [] initializeCommands;
	boolean resetHasOccurred;
	int whichCommand;
	ConnectThread connect;
	
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	protected static final int COMPLETE_MESSAGE = 2;
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String s;
			switch(msg.what)
			{
			case SUCCESS_CONNECT:
				//do something on a connection
				//Intent intent = new Intent(getApplicationContext(),Connected.class);
				//startActivity(intent);
				connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
				Toast.makeText(getApplicationContext(), "CONNECT", Toast.LENGTH_SHORT).show();
				//s = "successful connection\n\r";
				//connectedThread.write(s.getBytes());
				//s = "how to we get input now?";
				//connectedThread.write(s.getBytes());
				connectedThread.start();
				s = "AT Z\r";
				connectedThread.write(s.getBytes());
				//sendInitializationCommands();
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[])msg.obj;
				
				compileMessage(readBuf);
				/*
				s = new String(readBuf);
				int something = readBuf[0];
				String x = Integer.toString(something);
				//Toast.makeText(getApplicationContext(), x, Toast.LENGTH_SHORT).show();
				*/
				break;
				//ELM always terminates with a CR 13 decimal 0x0D
				//
			//case COMPLETE_MESSAGE:
				//String a = "atz";
				//connectedThread.write(a.getBytes());
			}
		}

	};
	
	private void compileMessage(byte[] bytes) {
		// TODO Auto-generated method stub
		String s = new String(bytes);
		//Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
		for(int i = 0; i < s.length(); i++){
			char x = s.charAt(i);
			/*
			switch (x)
			{
			case 13:
				//append nothing finish message, display and then erase message
				Toast.makeText(getApplicationContext(), currentMessage, Toast.LENGTH_SHORT).show();
				currentMessage = "";
			break;
			default:
				currentMessage = currentMessage+x;
			}
			*/
			if(x < 33 || x > 127)
			{
				if(x == 13) // carriage return
				{
					if(currentMessage.contains("ELM327"))
					{
						resetHasOccurred = true;
						//String send = initializeCommands[1];
						//connectedThread.write(send.getBytes());
					}
					else if(currentMessage.contains("410C"))
					{
						if(currentMessage.length()==8)
						{
							String data = currentMessage.substring(4);
							int intData = Integer.parseInt(data,16);
							int decoded = (int) (intData/4);
							String finalData = Integer.toString(decoded);
							textView2.setText(finalData);
						}
					}
					else if(currentMessage.contains("410D"))
					{
						if(currentMessage.length()==6)
						{
							String data = currentMessage.substring(4);
							int intData = Integer.parseInt(data,16);
							int decoded = (int) (intData*.621371);
							String finalData = Integer.toString(decoded);
							textView1.setText(finalData);
						}
					}
					
					else if(currentMessage.contains("V"))
					{
						//if(currentMessage.length() >= 5)
						//{
							//String data = currentMessage.substring(4);
							//int intData = Integer.parseInt(data,16);
							//int decoded = (int) (intData/4);
							//String finalData = Integer.toString(decoded);
							textView3.setText(currentMessage);
						//}
					}
					
					
					
					/*
					if(didIJustWrite)
					{
						messsageIndicator = -1;
						//textView2.setText(lines[line]);
						
						didIJustWrite = false;
					}
					else if(messsageIndicator == -1)
					{
						if(currentMessage.length() == 6)
						{
							if(currentMessage.contains("410D"))
							{
								String data = currentMessage.substring(4);
								int intData = Integer.parseInt(data,16);
								int decoded = (int) (intData*.621371);
								String finalData = Integer.toString(decoded);
								textView2.setText(finalData);
							}
							else
							{
								textView2.setText(currentMessage);
							}
						}
						else
						{
							textView2.setText(currentMessage);
						}
						
						messsageIndicator = 10;
					}
					
					*/
					
					/*
					if(!(lines[line].length() < 1) && lines[line].charAt(lines[line].length()-1) == '>') //If the last character on the
						//current line is a >
					{
						//now free to send a new command
						String a;
						a = "010C\r";
						//a = "AT RV\r";
						connectedThread.write(a.getBytes());
					}
					*/
					
					//textView2.setText(lines[line]);
					if(line==9)
					{
						line = 0;
					}
					else
					{
						line++;
					}
					//lines[line] = "";
					currentMessage = "";
					//textView2.setText(currentMessage);
					//currentMessage = "";
					
					Log.d("@cr", Integer.toString(x));
				}
				else if(x == 10)
				{
					Log.d("@lf", Integer.toString(x));
				}
				else if(x == 0)
				{
					//Log.d("null", Integer.toString(x));
				}
				else
				{
					Log.d("oob", Integer.toString(x));
				}
				
			}
			else if(x == 62) // >
			{
				//if(resetHasOccurred)
				//{
					//String send = initializeCommands[1];
					//send = send+"\r";
					//connectedThread.write(send.getBytes());
				//}
				//else
				//{
					String send = commands[whichCommand];
					send = send+"\r";
					//a = "AT RV\r";
					connectedThread.write(send.getBytes());
					if(whichCommand == 2)
					{
						whichCommand = 0;
					}
					else
					{
						whichCommand++;
					}
					
				// }
				Log.d("@>", Integer.toString(x));
				
			}
			else
			{
				Log.d("@not oob, cr, or >", Integer.toString(x));
				//lines[line] = lines[line]+x;
				
				currentMessage = currentMessage+x;
				Log.d("@themessage", currentMessage);
			}
		}
		
	}

	/*
	protected void sendInitializationCommands() {
		// TODO Auto-generated method stub
		String send = initializeCommands[0];
		send = send+"\r";
		connectedThread.write(send.getBytes());
	}
	*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen1);
		init();
		if(btAdapter == null)
		{
			Toast.makeText(getApplicationContext(), "No Bluetooth Detected", 0).show();
			finish();
		}
		else
		{
			if(!btAdapter.isEnabled())
			{
				turnOnBT();
			}
			else
			{
				getPairedDevices();
				startDiscovery();
			}
			
		}
	}

	private void startDiscovery() {
		// TODO Auto-generated method stub
		btAdapter.cancelDiscovery();
		btAdapter.startDiscovery();
		
	}

	private void turnOnBT() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(intent, 1);
	}

	private void getPairedDevices() {
		// TODO Auto-generated method stub
		devicesArray = btAdapter.getBondedDevices();
		if(devicesArray.size()>0)
		{
			for(BluetoothDevice device:devicesArray)
			{
				pairedDevices.add(device.getName());
			}
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		//connectNew = (Button)findViewById(R.id.bConnectNew);
		listView = (ListView)findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		textView1 = (TextView)findViewById(R.id.TextView1);
		textView2 = (TextView)findViewById(R.id.TextView2);
		textView3 = (TextView)findViewById(R.id.TextView3);
		listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, 0);
		listView.setAdapter(listAdapter);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = new ArrayList<String>();
		devices = new ArrayList<BluetoothDevice>();
		currentMessage = "";
		crCounter = 0;
		line = 0;
		lines = new String[10];
		lines[0]="";lines[1]="";lines[2]="";lines[3]="";lines[4]="";lines[5]="";lines[6]="";lines[7]="";lines[8]="";lines[9]="";
		didIJustWrite = false;
		messsageIndicator = 0;
		commands = new String[]{"atrv", "010C", "010D"};
		initializeCommands = new String[]{"atz", "at sp 0"};
		resetHasOccurred = false;
		whichCommand = 0;
		
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if(BluetoothDevice.ACTION_FOUND.equals(action))
				{
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);					
					devices.add(device);
					
					String s = "";
					for (int a = 0; a < pairedDevices.size(); a++) 
					{
						if (device.getName().equals(pairedDevices.get(a))) 
						{
								s = "(Paired)";
								break;
						}
					}
					listAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());
				}
				else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
				{
					
				}
				else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
				{
					
				}
				else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
				{
					if(btAdapter.getState() == btAdapter.STATE_OFF)
					{
						turnOnBT();
					}
				}
			}
		};
		registerReceiver(receiver,filter);
		
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver,filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver,filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver,filter);
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_CANCELED)
		{
			Toast.makeText(getApplicationContext(), "Bluetooth must be enabled", Toast.LENGTH_SHORT);
			finish();
		}
		Log.d("ActivityResult", Integer.toString(resultCode));
		if(resultCode == RESULT_OK)
		{
			getPairedDevices();
			startDiscovery();
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
		
	}
	/*
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if (connect != null) {
        	connect.cancel(); 
        	connect = null;
        }

        if (connectedThread != null) {
        	connectedThread.cancel(); 
        	connectedThread = null;
        }
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!btAdapter.isEnabled())
		{
			turnOnBT();
		}
		getPairedDevices();
		startDiscovery();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
	}
	
	*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen1, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(btAdapter.isDiscovering())
		{
			btAdapter.cancelDiscovery();
		}
		if(listAdapter.getItem(arg2).contains("Paired"))
		{
			//Toast.makeText(getApplicationContext(), "Device is paired", Toast.LENGTH_SHORT).show();
			
			BluetoothDevice selectedDevice = devices.get(arg2);
			connect = new ConnectThread(selectedDevice);
			connect.start();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Device is NOT paired", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class ConnectThread extends Thread {
	    
		private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	    	mmDevice = device;
	        BluetoothSocket tmp = null;
	        
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) {  System.err.println("ABC123,Connect,createRfcomm: " + e.getMessage());}
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        btAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	        	 System.err.println("ABC123,Connect,no connect: " + connectException.getMessage());
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        //manageConnectedSocket(mmSocket);
	        mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
	    }
	    
	 
	    private void manageConnectedSocket(BluetoothSocket mmSocket2) {
			// TODO Auto-generated method stub
			
		}

		/** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { System.err.println("ABC123,Connected, get streams: " + e.getMessage()); }
	 
	        mmInStream = tmpIn;
	        //String s = mmInStream.toString();
	        //Toast.makeText(getApplicationContext(), s , Toast.LENGTH_LONG).show();
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        byte[] buffer;// = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()
	        	
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	        	Log.d("blocking","run method");
	            try {
	            	buffer = new byte[1024];
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                String ab = new String(buffer);
	                Log.d("@bytes in thread", ab);
	                // Send the obtained bytes to the UI activity
	                
	            } catch (IOException e) {
	            	 System.err.println("ABC123,Connected, read bytes: " + e.getMessage());
	                break;
	            }
	            mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	        	
	        	/*
	        	try {
	        	    Thread.sleep(100);
	        	} catch(InterruptedException ex) {
	        	    Thread.currentThread().interrupt();
	        	}
	        	*/
	        	
	            mmOutStream.write(bytes);
	            String s = new String(bytes);
	            if(s.charAt(s.length()-1) == 13)
	            {
	            	didIJustWrite = true;
	            }
	        } catch (IOException e) { }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	

}
