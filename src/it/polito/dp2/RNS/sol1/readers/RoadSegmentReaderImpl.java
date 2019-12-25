package it.polito.dp2.RNS.sol1.readers;

import java.util.Set;

import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RoadSegmentReader;

public class RoadSegmentReaderImpl extends PlaceReaderImpl
	implements RoadSegmentReader 
{
	private String name;
	private String roadName;
	
	public RoadSegmentReaderImpl(String id,
			int capacity,
			Set<PlaceReader> nextPlaces,
			String name,
			String roadName) throws RnsReaderException 
	{
		super(id, capacity, nextPlaces);
		
		if(name == null || roadName == null)
			throw new RnsReaderException();
		
		this.name = name;
		this.roadName = roadName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getRoadName() {
		return roadName;
	}
}
