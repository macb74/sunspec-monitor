package de.martinbussmann.sunspecmonitor;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;

import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;


public class ModbusReaderThread implements Runnable {

    private ServletContext context;
	private int unitID;
	private String host;
	private int port;

	@Override
	public void run() {
		while(true) {
			try {
				getModbusData(100, 111);
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
    
	public void getModbusData(int start, int length) {

		Register[] registers = null;
		
        TCPMasterConnection con = null;
        ModbusTCPTransaction trans = null;
        ModbusRequest request = null;
        ReadMultipleRegistersResponse response = null;
         
        JSONArray json = new JSONArray(context.getAttribute("sunspec.result").toString());
        JSONObject dataset = new JSONObject();
        
//        try {
//        	InetAddress addr = null;
//        	addr = InetAddress.getByName(host);
//        	con = new TCPMasterConnection(addr);
//        	con.setPort(port);
//        	con.setTimeout(2000);
//        	con.connect();
//        	
//        	request = new ReadMultipleRegistersRequest(start, length);
//        	request.setUnitID(unitID);
//        	
//        	trans = new ModbusTCPTransaction(con);
//        	trans.setRetries(0);
//        	trans.setRequest(request);
//        	trans.execute();
//        	
//        	response = (ReadMultipleRegistersResponse) trans.getResponse();
//        	registers = response.getRegisters();
//        	con.close();
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//    	if(registers != null) {
    	   		 
    		/*
    		String json = "{\"I_DC_Power\":" + toSignedInt16(registers[0].getValue()) + 
					", \"I_DC_Power_Scale\": " + toSignedInt16(registers[1].getValue()) +
	    			", \"M_AC_Power\": " + toSignedInt16(registers[106].getValue()) + 
	    			", \"M_AC_Power1\": " + toSignedInt16(registers[107].getValue()) + 
	    			", \"M_AC_Power2\": " + toSignedInt16(registers[108].getValue()) + 
	    			", \"M_AC_Power3\": " + toSignedInt16(registers[109].getValue()) + 
	    			", \"M_AC_Power_Scale\": " + toSignedInt16(registers[110].getValue()) + 
	    			"}";
    		*/
    		
//    		dataset.put("I_DC_Power", toSignedInt16(registers[0].getValue()));
//    		dataset.put("I_DC_Power_Scale", toSignedInt16(registers[1].getValue()));
//    		dataset.put("M_AC_Power", toSignedInt16(registers[106].getValue()));
//    		dataset.put("M_AC_Power1", toSignedInt16(registers[107].getValue()));
//    		dataset.put("M_AC_Power2", toSignedInt16(registers[108].getValue()));
//    		dataset.put("M_AC_Power3", toSignedInt16(registers[109].getValue()));
//    		dataset.put("M_AC_Power_Scale", toSignedInt16(registers[110].getValue()));
    		
    		dataset.put("I_DC_Power", 123);
    		dataset.put("I_DC_Power_Scale", 456);
    		dataset.put("M_AC_Power", 768);
    		dataset.put("M_AC_Power1", 321);
    		dataset.put("M_AC_Power2", 654);
    		dataset.put("M_AC_Power3", 987);
    		dataset.put("M_AC_Power_Scale", 789);
    		dataset.put("Date", getDate());
    		
        	json.put(dataset);
        	shift(json);
    		context.setAttribute("sunspec.result", json.toString());
//    	}
    }
        
	private void shift(JSONArray json) {
		while(json.length() > 40) {
			json.remove(0);
		}
		
	}

	private String getDate() {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss"); 
    	Date currentTime = new Date();
		return formatter.format(currentTime);
	}

	private static int toSignedInt16(int i) {
    	if(i > 32768) {
    		i = i - 65536;
    	}
		return i;
    }

	public void setContext(ServletContext context) {
		this.context = context;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;	
	}

	public void setHost(String host) {
		this.host = host;
		
	}

	public void setPort(int port) {
		this.port = port;
	}

}