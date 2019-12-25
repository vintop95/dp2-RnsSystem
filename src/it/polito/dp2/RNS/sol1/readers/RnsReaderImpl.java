package it.polito.dp2.RNS.sol1.readers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.VehicleReader;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;

public class RnsReaderImpl implements RnsReader {

	private Set<ConnectionReader> connReadersById = 
			new HashSet<ConnectionReader>();
	private Map<String,PlaceReader> placeReadersById = 
			new HashMap<String, PlaceReader>();
	private Map<String,VehicleReader> vehicleReadersById = 
			new HashMap<String, VehicleReader>();
	
	public RnsReaderImpl(
			Map<String,PlaceReader> placeReadersById,
			Set<ConnectionReader> connReadersById,
			Map<String,VehicleReader> vehicleReadersById) 
			throws RnsReaderException 
	{
		if (connReadersById == null || placeReadersById == null
			|| vehicleReadersById == null)
		{
			throw new RnsReaderException();
		}

		this.connReadersById = connReadersById;
		this.placeReadersById = placeReadersById;
		this.vehicleReadersById = vehicleReadersById;
	}
	
//////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Set<ConnectionReader> getConnections() {
		return connReadersById;
	}
	
///////////////////////////////////////////////////////////////////////////////
	
	@Override
	public Set<PlaceReader> getPlaces(String idPrefix) {
		Set<PlaceReader> placeReaders = new HashSet<PlaceReader>();
		for(PlaceReader pr: placeReadersById.values()){
			
			boolean prefixOk = true;
			if(idPrefix != null){
				prefixOk = pr.getId().startsWith(idPrefix);
			}
			
			if(prefixOk){
				placeReaders.add(pr);
			}	
		}
		return placeReaders;
	}

	@Override
	public PlaceReader getPlace(String placeId) {
		if(placeId == null)
			return null;
		
		return placeReadersById.get(placeId);
	}
	
	@Override
	public Set<RoadSegmentReader> getRoadSegments(String roadName) {
		Set<RoadSegmentReader> rsReaders = new HashSet<RoadSegmentReader>();
		for(PlaceReader pr: placeReadersById.values()){
			boolean pIsRoadSegment = pr instanceof RoadSegmentReader;
			
			if(pIsRoadSegment){
				RoadSegmentReader rs = (RoadSegmentReader) pr;
				
				boolean roadNameIsEqual = true;
				if(roadName != null){
					roadNameIsEqual = rs.getRoadName().equals(roadName);
				}	
				
				if(roadNameIsEqual){
					rsReaders.add(rs);
				}	
			}	
		}
		return rsReaders;
	}
	
	/**
	 * You must retrieve readers for all the parking areas
	 * having all the services given in the set.
	 */
	@Override
	public Set<ParkingAreaReader> getParkingAreas(
				Set<String> servicesReq) 
	{
		Set<ParkingAreaReader> pAreaReaders = new HashSet<ParkingAreaReader>();
		for(PlaceReader pr: placeReadersById.values()){
			boolean pIsParkingArea = pr instanceof ParkingAreaReader;
			
			if(pIsParkingArea){
				ParkingAreaReader par = (ParkingAreaReader) pr;
				
				boolean hasServices = true;
				if(servicesReq != null){
					hasServices = par.getServices().containsAll(servicesReq);
				}
				
				if(hasServices){
					pAreaReaders.add(par);
				}
			}
			
		}
		return pAreaReaders;
	}
	
	@Override
	public Set<GateReader> getGates(GateType typeReq) {
		Set<GateReader> gateReaders = new HashSet<GateReader>();
		for(PlaceReader pr: placeReadersById.values()){
			boolean pIsGate = pr instanceof GateReader;
			
			if(pIsGate){
				GateReader gr = (GateReader) pr;
				
				boolean typeIsEqual = true;
				if(typeReq != null){
					typeIsEqual = gr.getType().equals(typeReq);
				}	
				
				if(typeIsEqual){
					gateReaders.add(gr);
				}
			}
		}
		return gateReaders;
	}
	
//////////////////////////////////////////////////////////////////////////////
	
	@Override
	public VehicleReader getVehicle(String vehicleId) {
		if(vehicleId == null)
			return null;
		
		return vehicleReadersById.get(vehicleId);
	}

	@Override
	public Set<VehicleReader> getVehicles
		(Calendar since, Set<VehicleType> typesReq, VehicleState stateReq) 
	{
		Set<VehicleReader> vehicleReaders = new HashSet<VehicleReader>();
		for(VehicleReader vr: vehicleReadersById.values()){
			boolean timeOk = true;
			if(since != null){
				timeOk = vr.getEntryTime().after(since);
			}
			
			boolean typeOk = true;
			if(typesReq != null){
				typeOk = typesReq.contains(vr.getType());
			}
			
			boolean stateOk = true;
			if(stateReq != null){
				stateOk = vr.getState().equals(stateReq);
			}
			
//			String intTimeOk = timeOk ? "1" : "0";
//			String intTypeOk = typeOk ? "1" : "0";
//			String intStateOk = stateOk ? "1" : "0";
//			System.out.println("Vehicle values " + 
//				intTimeOk + intTypeOk + intStateOk);
			if(timeOk && stateOk && typeOk){
				vehicleReaders.add(vr);
			}		
		}
		return vehicleReaders;
	}
	
//////////////////////////////////////////////////////////////////////////////
	
}
