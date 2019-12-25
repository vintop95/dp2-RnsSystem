package it.polito.dp2.RNS.sol1.readers;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReaderException;

public class ConnectionReaderImpl implements ConnectionReader {
	private PlaceReader from;
	private PlaceReader to;
	
	public ConnectionReaderImpl(PlaceReader from, PlaceReader to)
			throws RnsReaderException 
	{
		if(from == null || to == null)
			throw new RnsReaderException();
		
		this.from = from;
		this.to = to;
	}
	
	@Override
	public PlaceReader getFrom() {
		return from;
	}

	@Override
	public PlaceReader getTo() {
		return to;
	}

	@Override
	public String toString(){
		return "(" + from.getId() + "," + to.getId() + ")";
	}
}
