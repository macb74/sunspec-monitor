package de.martinbussmann.sunspecmonitor;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;

public class SunspecMeter {

	public static void main(String[] args) {
		ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder("localhost").build();
		ModbusTcpMaster master = new ModbusTcpMaster(config);

		CompletableFuture<ReadHoldingRegistersResponse> future =
		        master.sendRequest(new ReadHoldingRegistersRequest(0, 2), 1);
		
		
		future.thenAccept(response -> {
		    System.out.println("Response: " + ByteBufUtil.hexDump(response.getRegisters()));

		    ReferenceCountUtil.release(response);
		});

        //master.disconnect();		
	}

}
