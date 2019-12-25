package it.polito.dp2.RNS.sol3.admClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.lab3.AdmClient;
import it.polito.dp2.RNS.lab3.AdmClientException;
import it.polito.dp2.RNS.lab3.ServiceException;
import it.polito.dp2.RNS.sol1.readers.ConnectionReaderImpl;
import it.polito.dp2.RNS.sol1.readers.GateReaderImpl;
import it.polito.dp2.RNS.sol1.readers.ParkingAreaReaderImpl;
import it.polito.dp2.RNS.sol1.readers.RoadSegmentReaderImpl;
import it.polito.dp2.RNS.sol3.jaxb.Connection;
import it.polito.dp2.RNS.sol3.jaxb.ConnectionsResponse;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.PlacesResponse;
import it.polito.dp2.RNS.sol3.rnsClient.RnsClient;
import it.polito.dp2.RNS.sol3.rnsClient.RnsClientException;

public class AdmClientFactory 
	extends it.polito.dp2.RNS.lab3.AdmClientFactory 
{
	private RnsClient rnsClient;
	private MediaType mediaType = MediaType.APPLICATION_XML_TYPE;
	
	@Override
	public AdmClient newAdmClient() throws AdmClientException {		
		// System.out.println( "DEBUG - CALLING newAdmClient()" );
		
		try {
			rnsClient = new RnsClient(mediaType, "adminToken");
		} catch (RnsClientException e) {
			throw new AdmClientException(e);
		}
		
		Map<String,PlaceReader> placeReadersById = 
				new HashMap<String, PlaceReader>();
		Map<String,PlaceReader> placeReadersByUri = 
				new HashMap<String, PlaceReader>();
		Set<ConnectionReader> connectionReaders;
		
		setPlaceReaders(placeReadersById, placeReadersByUri);
		connectionReaders = buildConnections(placeReadersByUri);

		try {
			return new AdmClientImpl(placeReadersById, 
					placeReadersByUri, connectionReaders, mediaType);
		} catch (RnsReaderException e) {
			throw new AdmClientException(e);
		}
	}

///////////////////////////////////////////////////////////////////////////
	
	private void addPlaceReader(Map<String,PlaceReader> placeReadersById,
			Map<String,PlaceReader> placeReadersByUri, 
			String uri, PlaceReader p)
	{
		placeReadersByUri.put(uri, p);
		placeReadersById.put(p.getId(), p);
	}
	
	private PlaceReader buildPlaceReader(Place p, 
			Set<PlaceReader> nextPlaces) throws AdmClientException
	{
		PlaceReader pr;
		
		try{
			String id = p.getId();
			int capacity = p.getCapacity().intValue();
			if(p.getRoadSegment() != null){
				pr = new RoadSegmentReaderImpl(id,
							  capacity,
							  nextPlaces, 
							  p.getRoadSegment().getName(),
							  p.getRoadSegment().getRoadName());
			}else if (p.getParkingArea() != null){
				pr = new ParkingAreaReaderImpl(id,
						  capacity,
						  nextPlaces, 
						  new HashSet<String>(p.getParkingArea().
								  getServicesId().getServiceId()));
			}else if (p.getGate() != null){
				GateType type = 
					GateType.valueOf(p.getGate().getType().toString());
				pr = new GateReaderImpl(id,
						  capacity,
						  nextPlaces, 
						  type);
			}else{
				throw new AdmClientException();
			}
		}catch(RnsReaderException e){
			throw new AdmClientException(e);
		}
		
		return pr;
	}
	
	private void setPlaceReaders(Map<String,PlaceReader> placeReadersById,
			Map<String,PlaceReader> placeReadersByUri) 
					throws AdmClientException
	{
		System.out.println("SETTING PLACES");
		// GET /rns/places
		PlacesResponse res;
		try{
			String url = rnsClient.getLocationFromRoot(Place.class);
			WebTarget target = rnsClient.buildTarget(url);
			res = rnsClient.getRequest(
									target, PlacesResponse.class);
		}catch(ServiceException e){
			throw new AdmClientException(e);
		}
		
		//Key is placeId, temporarily needed
		Map<String, Set<PlaceReader>> nextPlacesMap = 
				new HashMap<String, Set<PlaceReader>>();
		
		//first set the places with an empty nextPlaces
		for(Place p: res.getPlaces().getPlace()){
			Set<PlaceReader> nextPlaces = new HashSet<PlaceReader>();
			nextPlacesMap.put(p.getId(), nextPlaces);
			
			PlaceReader pr = buildPlaceReader(p, nextPlaces);
			
			this.addPlaceReader(
					placeReadersById, placeReadersByUri, 
					p.getSelf(), pr
			);
		}
		
		//then set the nextPlaces
		for(Place p: res.getPlaces().getPlace()){			
			Set<PlaceReader> nextPlaces = nextPlacesMap.get(p.getId());
			List<String> uriNextPlaces = 
					p.getUriOfNextPlaces().getPlaceUri();
			for(String nextPlaceUri: uriNextPlaces){
				nextPlaces.add(placeReadersByUri.get(nextPlaceUri));
			}
		}
	}

//////////////////////////////////////////////////////////////////////
	
	private ConnectionReader buildConnectionReader(
			Connection c, Map<String,PlaceReader> placeReadersByUri) 
			throws AdmClientException
	{
		PlaceReader from = placeReadersByUri.get(c.getFromUri());
		PlaceReader to = placeReadersByUri.get(c.getToUri());
		try {
			return new ConnectionReaderImpl(from,to);
		} catch (RnsReaderException e) {
			throw new AdmClientException("Connection read not consistent");
		}
	}
	
	private Set<ConnectionReader> buildConnections(
			Map<String,PlaceReader> placeReadersByUri)
			throws AdmClientException
	{
		System.out.println("SETTING CONNECTIONS");
		Set<ConnectionReader> connectionReaders = 
				new HashSet<ConnectionReader>();
		
		//GET /rns/connections
		ConnectionsResponse res;
		try{
			String url = rnsClient.getLocationFromRoot(Connection.class);
			WebTarget target = rnsClient.buildTarget(url);
			res = rnsClient.getRequest(target, ConnectionsResponse.class);
		}catch(ServiceException e){
			throw new AdmClientException(e);
		}
		
		for(Connection c: res.getConnections().getConnection()){
			buildConnectionReader(c, placeReadersByUri);
		}
		return connectionReaders;
	}
}
