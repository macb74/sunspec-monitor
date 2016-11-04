package de.martinbussmann.sunspecmonitor.modbus;
import java.net.InetAddress;
import java.util.ArrayList;

import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;

public class ModbusReader {

    public ArrayList<Integer> getModbusData(int unitID, int start, int length) {

        TCPMasterConnection con = null;
        ModbusTCPTransaction trans = null;
        ModbusRequest request = null;
        ReadMultipleRegistersResponse response = null;
        ArrayList<Integer> result = null;
        
    	try {
            InetAddress addr = null;
            int port = 502;

            addr = InetAddress.getByName("localhost");
            con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();

            request = new ReadMultipleRegistersRequest(start, length);
            request.setUnitID(unitID);
            
            trans = new ModbusTCPTransaction(con);
            trans.setRetries(0);
            trans.setRequest(request);
            trans.execute();
            
            response = (ReadMultipleRegistersResponse) trans.getResponse();
            Register[] registers = response.getRegisters();

            result = getListFromRegisters(registers);
            
            con.close();
            
        } catch (Exception ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }

		return result;
    }
    
    
    private ArrayList<Integer> getListFromRegisters(Register[] registers) {
    	ArrayList<Integer> result = new ArrayList<Integer>();
    	for( int i = 0; i < registers.length; i++ ) {
    		result.add(i, registers[i].getValue());
    	}
		return result;
	}


	private static int toSignedInt16(int i) {
    	if(i > 32768) {
    		i = i - 65536;
    	}
		return i;
    }
   
}