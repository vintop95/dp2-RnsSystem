package it.polito.dp2.RNS.sol3.admClient;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.VehicleReader;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.lab3.AdmClient;
import it.polito.dp2.RNS.lab3.AdmClientException;
import it.polito.dp2.RNS.lab3.ServiceException;
import it.polito.dp2.RNS.sol1.readers.RnsReaderImpl;
import it.polito.dp2.RNS.sol1.readers.VehicleReaderImpl;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;
import it.polito.dp2.RNS.sol3.jaxb.VehiclesResponse;
import it.polito.dp2.RNS.sol3.rnsClient.RnsClient;
import it.polito.dp2.RNS.sol3.rnsClient.RnsClientException;

/**
* AdmClient is an interface for interacting with the RNS service as
*  administrator.
* AdmClient extends RnsReader: an implementation of AdmClient is expected
*  to contact the RNS service at initialization time, and get from the
*  service the information about places and their connections.
* This information is stored in the implementation object and remains
*  fixed for the lifetime of the object.
* For what concerns vehicles, an implementation of AdmClient always
*  returns fresh information through getUpdatedVehicles or
*  getUpdatedVehicle. Whenever one of these methods is called,
*  the implementation contacts the service and returns the fresh
*  information just obtained from the service.
* Instead, when the getVehicles or getVehicle inherited from RnsReader
*  are called, an implementation of AdmClient does not return any
*  information about vehicles (i.e. it returns, respectively,
*  an empty set or null).
*/
public class AdmClientImpl extends RnsReaderImpl implements AdmClient
{
	/**
	 * TEST MAIN
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) 
			throws AdmClientException, ServiceException 
	{
		System.out.println("+++ ATTENTION, THIS IS A TEST FOR AdmClient +++");
		
		System.setProperty("it.polito.dp2.RNS.lab3.AdmClientFactory",
				"it.polito.dp2.RNS.sol3.admClient.AdmClientFactory");
		AdmClient adm = AdmClientFactory.newInstance().newAdmClient();
		
		Set<VehicleReader> updVehs = adm.getUpdatedVehicles("Gate2");
		VehicleReader updVeh = adm.getUpdatedVehicle("AAA");
		updVeh = adm.getUpdatedVehicle("AAA");
		
		Set<ConnectionReader> conns = adm.getConnections();
		
		Set<PlaceReader> places = adm.getPlaces("S");
		PlaceReader place = adm.getPlace("Gate0"); 
		Set<RoadSegmentReader> rss = adm.getRoadSegments("SS0");
		Set<String> servicesReq = new HashSet<String>();
		servicesReq.add("Toilet");
		Set<ParkingAreaReader> pas = adm.getParkingAreas(servicesReq);
		Set<GateReader> grs = adm.getGates(GateType.IN);
	}

///////////////////////////////////////////////////////////////////////////////
	
	private RnsClient rnsClient;
	private Map<String,PlaceReader> placeReadersByUri = 
			new HashMap<String, PlaceReader>();
	
	public AdmClientImpl(Map<String,PlaceReader> placeReadersById,
			Map<String,PlaceReader> placeReadersByUri,
			Set<ConnectionReader> connReadersById,
			MediaType mediaType) 
			throws AdmClientException, RnsReaderException
	{
		super(placeReadersById, connReadersById, 
				new HashMap<String,VehicleReader>());
		this.placeReadersByUri = placeReadersByUri;
		
		try {
			rnsClient = new RnsClient(mediaType, "adminToken");
		} catch (RnsClientException e) {
			throw new AdmClientException(e);
		}
	}

///////////////////////////////////////////////////////////////////////////////
	
	private static VehicleReader buildVehicleReader(Vehicle v,
			Map<String,PlaceReader> placeReadersByUri) 
			throws ServiceException
	{
		VehicleType type = VehicleType.valueOf(v.getType().toString());
		VehicleState state = VehicleState.valueOf(v.getState().toString());;
		VehicleReader vr;
		try {
			vr = new VehicleReaderImpl(
				v.getId(),
				(Calendar) v.getEntryTime().toGregorianCalendar(),
				type,
				state,
				placeReadersByUri.get(v.getPositionUri()),
				placeReadersByUri.get(v.getOriginUri()),
				placeReadersByUri.get(v.getDestinationUri())
			);
		} catch (RnsReaderException e) {
			throw new ServiceException("vehicle read not consistent");
		}
		return vr;
	}
	
	/**
	 * Gets readers for all the vehicles that are currently in the
	 *  place with the given id, in the RNS system.
	 * If no place is specified, return the readers of all the vehicles
	 *  that are currently in the system
	 * @param place the place for which we want to get vehicle readers
	 *  or null to get readers for all places.
	 * @return a set of interfaces for reading the selected vehicles.
	 * @throws ServiceException if the operation cannot be completed 
	 *  because the RNS service is not reachable or not working
	 */
	@Override
	public Set<VehicleReader> getUpdatedVehicles(String placeId)
			throws ServiceException
	{
		System.out.println("GETTING VEHICLES FROM PLACE " + placeId);
		Set<VehicleReader> updatedVehicles = new HashSet<VehicleReader>();
		
		WebTarget target;
		if(placeId == null){
			//GET /rns/vehicles
			String url = rnsClient.getLocationFromRoot(Vehicle.class);
			target = rnsClient.buildTarget(url);
		}else{
			//GET /rns/places/{placeId}/vehicles
			String url = rnsClient.getLocationFromRoot(Place.class);
			target = rnsClient.buildTarget(url).path(placeId);
			Place p = rnsClient.getRequest(target, Place.class);
			if(p == null || p.getVehiclesUri() == null){
				throw new ServiceException();
			}
			target = rnsClient.buildTarget(p.getVehiclesUri());
		}
		
		VehiclesResponse vehiclesResponse = 
				rnsClient.getRequest(target,VehiclesResponse.class);
		
		for(Vehicle v : vehiclesResponse.getVehicles().getVehicle()){
			VehicleReader vr = buildVehicleReader(v, placeReadersByUri);
			updatedVehicles.add(vr);
		}
		
		return updatedVehicles;
	}

	/**
	 * Gets a reader for a single vehicle, available in the RNS system,
	 *  given its plate id.
	 * The returned information is obtained from the remote service
	 *  when the method is called.
	 * @param id the plate id of the vehicle to get.
	 * @return an interface for reading the vehicle with the given plate
	 *  id or null if a vehicle with the given plate id is not available
	 *   in the system.
	 * @throws ServiceException if the operation cannot be completed
	 *  because the RNS service is not reachable or not working
	 */
	@Override
	public VehicleReader getUpdatedVehicle(String vehicleId)
			throws ServiceException
	{		
		if(vehicleId == null){
			//SHOULD NEVER HAPPEN
			throw new ServiceException("plateId inserted is null");
		}
		
		// GET /rns/vehicles/{id}
		System.out.println("GETTING VEHICLE " + vehicleId);
		String url = rnsClient.getLocationFromRoot(Vehicle.class);
		WebTarget target = rnsClient.buildTarget(url).path(vehicleId);
		Vehicle v = rnsClient.getRequestWithNull(target, Vehicle.class);
		if (v == null){
			return null;
		}
		
		return buildVehicleReader(v, placeReadersByUri);
	}
	
///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Note that, as documented, the getVehicles and getVehicle
	 * methods inherited from RnsReader should return an empty
	 * set or null).
	 */
	@Override
	public VehicleReader getVehicle(String arg0) {
		return null;
	}

	@Override
	public Set<VehicleReader> getVehicles(
			Calendar arg0, Set<VehicleType> arg1, VehicleState arg2) {
		return new HashSet<VehicleReader>();
	}
	
///////////////////////////////////////////////////////////////////////////////
	
}
