package it.polito.dp2.RNS.sol1.readers;

import java.util.Set;

import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReaderException;

public class GateReaderImpl extends PlaceReaderImpl
	implements GateReader 
{
	private GateType type;
	
	public GateReaderImpl(String id,
			int capacity,
			Set<PlaceReader> nextPlaces,
			GateType type) 
			throws RnsReaderException 
	{
		super(id, capacity, nextPlaces);
		if(type == null)
			throw new RnsReaderException();
		
		this.type = type;
	}

	@Override
	public GateType getType() {
		return type;
	}
}
