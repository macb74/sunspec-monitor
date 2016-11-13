package de.martinbussmann.sunspecmonitor;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	private JSONObject json;

	@Override
	public void run() {
		while (true) {
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

		json = new JSONObject(context.getAttribute("sunspec.result").toString());

		try {
			InetAddress addr = null;
			addr = InetAddress.getByName(host);
			con = new TCPMasterConnection(addr);
			con.setPort(port);
			con.setTimeout(2000);
			con.connect();

			request = new ReadMultipleRegistersRequest(start, length);
			request.setUnitID(unitID);

			trans = new ModbusTCPTransaction(con);
			trans.setRetries(0);
			trans.setRequest(request);
			trans.execute();

			response = (ReadMultipleRegistersResponse) trans.getResponse();
			registers = response.getRegisters();
			con.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (registers != null) {

			ArrayList<Integer> inverterData = getSignetInt16(registers);

			double iPower = inverterData.get(0) * getDivisor(inverterData.get(1));
			double mPower = inverterData.get(106) * getDivisor(inverterData.get(110));
			json.getJSONArray("I_DC_Power").put(iPower);
			json.getJSONArray("M_AC_Power").put(mPower);
			json.getJSONArray("M_AC_Power1").put(inverterData.get(107) * getDivisor(inverterData.get(110)));
			json.getJSONArray("M_AC_Power2").put(inverterData.get(108) * getDivisor(inverterData.get(110)));
			json.getJSONArray("M_AC_Power3").put(inverterData.get(109) * getDivisor(inverterData.get(110)));
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

	private void shift(JSONArray json) {
		while (json.length() > 40) {
			json.remove(0);
		}
	}

	private String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
		Date currentTime = new Date();
		return formatter.format(currentTime);
	}

	private ArrayList<Integer> getSignetInt16(Register[] registers) {
		ArrayList<Integer> data = new ArrayList<Integer>();
		for (int i = 0; i < registers.length; i++) {
			data.add(toSignedInt16(registers[i].getValue()));
		}
		return data;
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

}