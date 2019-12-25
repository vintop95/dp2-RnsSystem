package it.polito.dp2.RNS.sol1.readers;

import java.util.Calendar;

import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.VehicleReader;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;

public class VehicleReaderImpl extends IdentifiedEntityReaderImpl
implements VehicleReader {

	private Calendar entryTime;
	private VehicleType type;
	private VehicleState state;
	private PlaceReader position;
	private PlaceReader origin;
	private PlaceReader destination;
	
	public VehicleReaderImpl(String id, Calendar entryTime,
			VehicleType type, VehicleState state,
			PlaceReader position,PlaceReader origin,
			PlaceReader destination) throws RnsReaderException 
	{
		super(id);
		if(entryTime == null || type == null || state == null ||
		   position == null || origin == null || destination == null)
			throw new RnsReaderException();
		
		this.entryTime = entryTime;
		this.type = type;
		this.state = state;
		this.position = position;
		this.origin = origin;
		this.destination = destination;
	}
	
////////////////////////////////////////////////////////////////////////////

	@Override
	public Calendar getEntryTime() {
		return entryTime;
	}

	@Override
	public VehicleType getType() {
		return type;
	}

	@Override
	public VehicleState getState() {
		return state;
	}

////////////////////////////////////////////////////////////////////////////

	@Override
	public PlaceReader getPosition() {
		return position;
	}

	@Override
	public PlaceReader getOrigin() {
		return origin;
	}

	@Override
	public PlaceReader getDestination() {
		return destination;
	}
}
