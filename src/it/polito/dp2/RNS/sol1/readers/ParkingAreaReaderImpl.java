package it.polito.dp2.RNS.sol1.readers;

import java.util.Set;

import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReaderException;

public class ParkingAreaReaderImpl extends PlaceReaderImpl 
implements ParkingAreaReader 
{
	private Set<String> services;
	
	public ParkingAreaReaderImpl(String id,
			int capacity,
			Set<PlaceReader> nextPlaces,
			Set<String> services) throws RnsReaderException 
	{
		super(id, capacity, nextPlaces);
		
		if(services == null)
			throw new RnsReaderException();
		
		this.services = services;
	}

	@Override
	public Set<String> getServices() {
		return services;
	}
}
