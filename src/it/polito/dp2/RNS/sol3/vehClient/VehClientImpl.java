package it.polito.dp2.RNS.sol3.vehClient;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.RNS.FactoryConfigurationError;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.lab3.EntranceRefusedException;
import it.polito.dp2.RNS.lab3.ServiceException;
import it.polito.dp2.RNS.lab3.UnknownPlaceException;
import it.polito.dp2.RNS.lab3.VehClient;
import it.polito.dp2.RNS.lab3.VehClientException;
import it.polito.dp2.RNS.lab3.WrongPlaceException;
import it.polito.dp2.RNS.sol3.jaxb.AddVehicleResponse;
import it.polito.dp2.RNS.sol3.jaxb.PlacesIdPath;
import it.polito.dp2.RNS.sol3.jaxb.ChangeStateRequest;
import it.polito.dp2.RNS.sol3.jaxb.MoveRequest;
import it.polito.dp2.RNS.sol3.jaxb.NewVehicle;
import it.polito.dp2.RNS.sol3.jaxb.ObjectFactory;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;
import it.polito.dp2.RNS.sol3.rnsClient.RnsClient;
import it.polito.dp2.RNS.sol3.rnsClient.RnsClientException;

/**
 * An Interface for interacting with the RNS service as a vehicle
 * Through this interface a vehicle can perform all the operations
 * allowed by the RNS service for that vehicle
 */
public class VehClientImpl implements VehClient {

	/**
	 * TEST MAIN
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws 
	VehClientException, FactoryConfigurationError
	{
		System.out.println("+++ ATTENTION, THIS IS A TEST FOR VehClient +++");
		
		System.setProperty("it.polito.dp2.RNS.lab3.VehClientFactory",
				"it.polito.dp2.RNS.sol3.vehClient.VehClientFactory");
		VehClient veh = VehClientFactory.newInstance().newVehClient();
		
		try {
List<String> path = veh.enter("VEH89",VehicleType.SHUTTLE,"Gate0", "Gate1");
			List<String> newPath = veh.move("Gate1");
			veh.changeState(VehicleState.PARKED);
			veh.exit("Gate2");
			
			System.out.println("+++++ GOOD ENDING :) +++++");
		} catch (EntranceRefusedException e) {
			System.out.println("+++++ BAD ENDING :( +++++");
			e.printStackTrace();
		} catch (UnknownPlaceException e) {
			System.out.println("+++++ BAD ENDING :( +++++");
			e.printStackTrace();
		} catch (WrongPlaceException e) {
			System.out.println("+++++ BAD ENDING :( +++++");
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.println("+++++ BAD ENDING :( +++++");
			e.printStackTrace();
		}
	}

///////////////////////////////////////////////////////////////////////////
	
	private RnsClient rnsClient;
	private AddVehicleResponse vehicleData = null;

	public VehClientImpl(MediaType mediaType) 
			throws VehClientException 
	{
		try {
			rnsClient = new RnsClient(mediaType, null);
		} catch (RnsClientException e) {
			throw new VehClientException(e);
		}
	}
	
	private void setVehicleData(AddVehicleResponse res){
		this.vehicleData = res;
		rnsClient.setAuthToken(vehicleData.getAuthToken());
	}
	
	private void removeVehicleData(){
		this.vehicleData = null;
		rnsClient.setAuthToken(null);
	}
	
	private boolean vehicleInRns(){
		return this.vehicleData != null;
	}
	
///////////////////////////////////////////////////////////////////////////
	
/**
 * Requests permission to the service to enter the system as a tracked vehicle
 * If permission is granted by the system, returns the suggested path to the 
 * desired destination
 * 
 * @param plateId the plate id of the vehicle that is requesting permission
 * @param inGate the id of the input gate for which the vehicle is requesting
 * permission
 * @param destination the destination place for which the vehicle is requesting
 * permission
 * @return the suggested path (the list of ids of the places of the suggested
 *  path, including source and destination)
 * @throws 500 ServiceException if the operation cannot be completed because
 *  the RNS service is not reachable or not working
 * @throws 409 UnknownPlaceException if the source or the destination is not
 *  a known place
 * @throws 409 WrongPlaceException if inGate is not the id of an IN or INOUT gate
 * @throws 403 EntranceRefusedException if permission to enter is not granted
 */
	@Override
	public List<String> enter(String vehicleId, VehicleType type,
			String inGateId, String destinationId)
			throws ServiceException, UnknownPlaceException,
			WrongPlaceException, EntranceRefusedException 
	{
		if(vehicleInRns()){
			throw new EntranceRefusedException("Vehicle already in rns");
		}
		
		String url = rnsClient.getLocationFromRoot(Vehicle.class);
		WebTarget target = rnsClient.buildTarget(url);
		
		NewVehicle newVehicle = (new ObjectFactory()).createNewVehicle();
		newVehicle.setId(vehicleId);
		it.polito.dp2.RNS.sol3.jaxb.VehicleType jaxbType;
		jaxbType = it.polito.dp2.RNS.sol3.
				   jaxb.VehicleType.valueOf(type.toString());
		newVehicle.setType(jaxbType);
		newVehicle.setOriginId(inGateId);
		newVehicle.setDestinationId(destinationId);
		
		setVehicleData(rnsClient.postVehicleRequest(target,
						newVehicle, AddVehicleResponse.class ));

		return vehicleData.getPlacesIdPath().getPlaceId();
	}

	/**
	 * Communicates to the service that the vehicle has changed its position
	 * to a new place
	 * Returns the new suggested path or null if the path has not changed
	 * 
	 * @param newPlace the id of the new place
	 * @return the new suggested path (the list of the ids of the places of
	 *  the new suggested path) or null if the suggested path has not changed
	 * @throws 500 ServiceException if the operation cannot be completed
	 *  because the RNS service is not reachable or not working
	 * @throws 409 UnknownPlaceException if newPlace is not the id of a known
	 *  place
	 * @throws 409 WrongPlaceException if newPlace is not is not the id of a
	 *  place reachable from the previous position of the vehicle
	 */
	@Override
	public List<String> move(String newPlaceId) throws 
		ServiceException, UnknownPlaceException, WrongPlaceException 
	{
		if(!vehicleInRns()){
			throw new ServiceException("Vehicle not in rns");
		}
		
		String url = vehicleData.getPositionUri();
		WebTarget target = rnsClient.buildTarget(url);
		
		MoveRequest req = (new ObjectFactory()).createMoveRequest();
		req.setNewPositionId(newPlaceId);
		
		PlacesIdPath path;
		path = rnsClient.putVehicleRequest(target, req, PlacesIdPath.class );
		
		return ( path == null ? null : path.getPlaceId() );
	}

	/**
	 * Communicates to the service the new state of the vehicle
	 * 
	 * @param newState the new state of the vehicle
	 * @throws 500 ServiceException if the operation cannot be 
	 * completed because the RNS service is not reachable or not working
	 */
	@Override
	public void changeState(VehicleState newState) throws ServiceException 
	{
		if(!vehicleInRns()){
			throw new ServiceException("Vehicle not in rns");
		}
		
		String url = vehicleData.getStateUri();
		WebTarget target = rnsClient.buildTarget(url);
		
		ChangeStateRequest req = (
				new ObjectFactory()).createChangeStateRequest();
		it.polito.dp2.RNS.sol3.jaxb.VehicleState jaxbState;
		jaxbState = it.polito.dp2.RNS.sol3.
				   jaxb.VehicleState.valueOf(newState.toString());
		req.setState(jaxbState);
		
		try {
			rnsClient.putVehicleRequest(target, req, PlacesIdPath.class );
		} catch (UnknownPlaceException | WrongPlaceException e) {
			// it should never happen, it's here because
			// we use only one function for put
			throw new ServiceException(e);
		}
	}

	/**
	 * Communicates to the service that the vehicle has exited the system
	 * 
	 * @param outGate the gate at which the vehicle has exited the system
	 * @throws 500 ServiceException if the operation cannot be completed 
	 *  because the RNS service is not reachable or not working
	 * @throws 409 UnknownPlaceException if outGate is not the id of a known
	 *  place
	 * @throws 409 WrongPlaceException if outGate is not the id of an OUT
	 *  or INOUT gate or is not reachable from the previous position of
	 *  the vehicle
	 */
	@Override
	public void exit(String outGateId) throws 
	ServiceException, UnknownPlaceException, WrongPlaceException 
	{
		if(!vehicleInRns()){
			throw new ServiceException("Vehicle not in rns");
		}
		
		String url = vehicleData.getVehicleUri();
		WebTarget target = rnsClient.buildTarget(url);
		rnsClient.deleteVehicleRequest(target, outGateId);
		
		removeVehicleData();
	}
///////////////////////////////////////////////////////////////////////////////
}
