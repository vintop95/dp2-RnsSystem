package it.polito.dp2.RNS.sol1.readers;

import java.util.Set;

import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReaderException;

public abstract class PlaceReaderImpl extends IdentifiedEntityReaderImpl
implements PlaceReader 
{
	private int capacity;
	private Set<PlaceReader> nextPlaces;
	
	public PlaceReaderImpl(String id, 
			int capacity, 
			Set<PlaceReader> nextPlaces) 
			throws RnsReaderException 
	{
		super(id);
		
		if(nextPlaces == null)
			throw new RnsReaderException();
		
		this.capacity = capacity;
		this.nextPlaces = nextPlaces;
	}
	
	@Override
	public int getCapacity() {		
		return capacity;
	}

	@Override
	public Set<PlaceReader> getNextPlaces() {
		return nextPlaces;
	}
}
