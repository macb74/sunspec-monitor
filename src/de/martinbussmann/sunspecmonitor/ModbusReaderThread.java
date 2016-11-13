package de.martinbussmann.sunspecmonitor;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONObject;

import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;

public class ModbusReaderThread implements Runnable {

	private ServletContext context;
	private int unitID;
	private String host;
	private int port;
	private JSONObject json;
	private int graphLength;
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(5000);
				getModbusData(1, 111);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void getModbusData(int start, int length) {

		Register[] registers = null;
		TCPMasterConnection con = null;
		ArrayList<Integer> inverterData = new ArrayList<Integer>();

		json = new JSONObject(context.getAttribute("sunspec.result").toString());

		try {
			InetAddress addr = null;
			addr = InetAddress.getByName(host);
			con = new TCPMasterConnection(addr);
			con.setPort(port);
			con.setTimeout(2000);
			con.connect();

			registers = readRegisters(con, 83, 2);
			inverterData = getSignetInt16(inverterData, registers);
						
			registers = readRegisters(con, 206, 5);
			inverterData = getSignetInt16(inverterData, registers);
			
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (inverterData.size() == 7) {

			double iPower = inverterData.get(0) * getDivisor(inverterData.get(1));
			double mPower = inverterData.get(2) * getDivisor(inverterData.get(6));
			json.getJSONArray("I_DC_Power").put(iPower);
			json.getJSONArray("M_AC_Power").put(mPower);
			json.getJSONArray("M_AC_Power1").put(inverterData.get(3) * getDivisor(inverterData.get(6)));
			json.getJSONArray("M_AC_Power2").put(inverterData.get(4) * getDivisor(inverterData.get(6)));
			json.getJSONArray("M_AC_Power3").put(inverterData.get(5) * getDivisor(inverterData.get(6)));
			json.getJSONArray("My_Power").put(iPower - mPower);
			json.getJSONArray("Date").put(getDate());

			shift(json.getJSONArray("I_DC_Power"));
			shift(json.getJSONArray("M_AC_Power"));
			shift(json.getJSONArray("M_AC_Power1"));
			shift(json.getJSONArray("M_AC_Power2"));
			shift(json.getJSONArray("M_AC_Power3"));
			shift(json.getJSONArray("My_Power"));
			shift(json.getJSONArray("Date"));

			context.setAttribute("sunspec.result", json.toString());
		}
	}

	private Register[] readRegisters(TCPMasterConnection con, int start, int len) throws ModbusIOException, ModbusSlaveException, ModbusException {
		ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(start, len);
		request.setUnitID(unitID);

		ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
		trans.setRetries(0);
		trans.setRequest(request);
		trans.execute();

		ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) trans.getResponse();
		Register[] registers = response.getRegisters();
		return registers;
	}

	private void shift(JSONArray json) {
		while (json.length() > graphLength) {
			json.remove(0);
		}
	}

	private String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
		Date currentTime = new Date();
		return formatter.format(currentTime);
	}

	private ArrayList<Integer> getSignetInt16(ArrayList<Integer> inverterData, Register[] registers) {
		for (int i = 0; i < registers.length; i++) {
			inverterData.add(toSignedInt16(registers[i].getValue()));
		}
		return inverterData;
	}

	private static int toSignedInt16(int i) {
		if (i > 32768) {
			i = i - 65536;
		}
		return i;
	}

	private double getDivisor(int i) {
		double d = 1;
		switch (i) {
		case 2:
			d = 100;
			break;
		case 1:
			d = 10;
			break;
		case -1:
			d = 0.1;
			break;
		case -2:
			d = 0.01;
			break;
		case -3:
			d = 0.001;
			break;
		}
		return d;
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

	public void setGraphLength(int graphLength) {
		this.graphLength = graphLength;	
	}

}