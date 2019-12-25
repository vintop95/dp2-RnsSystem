package it.polito.dp2.RNS.sol3.vehClient;

import javax.ws.rs.core.MediaType;

import it.polito.dp2.RNS.lab3.VehClient;
import it.polito.dp2.RNS.lab3.VehClientException;

public class VehClientFactory extends it.polito.dp2.RNS.lab3.VehClientFactory {

	private MediaType mediaType = MediaType.APPLICATION_XML_TYPE;
	
	@Override
	public VehClient newVehClient() throws VehClientException {
		// System.out.println( "DEBUG - CALLING newVehClient()" );
		return new VehClientImpl(mediaType);
	}

}
