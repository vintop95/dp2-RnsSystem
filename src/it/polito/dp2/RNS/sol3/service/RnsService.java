package it.polito.dp2.RNS.sol3.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.DatatypeConfigurationException;

import it.polito.dp2.RNS.FactoryConfigurationError;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.sol2.PathFinderRns;
import it.polito.dp2.RNS.sol2.lab2.BadStateException;
import it.polito.dp2.RNS.sol2.lab2.ModelException;
import it.polito.dp2.RNS.sol2.lab2.PathFinderException;
import it.polito.dp2.RNS.sol2.lab2.ServiceException;
import it.polito.dp2.RNS.sol2.lab2.UnknownIdException;
import it.polito.dp2.RNS.sol3.service.exceptions.
		RnsServiceException;
import it.polito.dp2.RNS.sol3.jaxb.AddVehicleResponse;
import it.polito.dp2.RNS.sol3.jaxb.Connection;
import it.polito.dp2.RNS.sol3.jaxb.Gate;
import it.polito.dp2.RNS.sol3.jaxb.GateType;
import it.polito.dp2.RNS.sol3.jaxb.NewVehicle;
import it.polito.dp2.RNS.sol3.jaxb.ObjectFactory;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.PlacePath;
import it.polito.dp2.RNS.sol3.jaxb.RnsRootResponse;
import it.polito.dp2.RNS.sol3.jaxb.Road;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;
import it.polito.dp2.RNS.sol3.jaxb.VehicleState;
import it.polito.dp2.RNS.sol3.jaxb.VehicleType;
import it.polito.dp2.RNS.sol3.service.db.RnsDB;
import it.polito.dp2.RNS.sol3.service.exceptions.
		EntranceRefusedRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.NotFoundRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.
		VehicleNotFoundRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.WrongPlaceRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.
		RoadNotFoundRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.SyntaxRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.
		PlaceNotFoundRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.UnauthorizedRnsException;

public class RnsService {
	
	private final boolean DEBUG_MODE = false;
	
	/**
	 * TEST MAIN: must activate DEBUG_MODE
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws RnsServiceException {
		//<sysproperty key="it.polito.dp2.RNS.Random.seed" value="${seed}"/>
	 	//<sysproperty key="it.polito.dp2.RNS.Random.testcase" value="${testcase}"/>
	 	//<sysproperty key="it.polito.dp2.RNS.RnsReaderFactory" value="${testRnsReaderFactory}"/>
	 	
		System.out.println("+++ ATTENTION, THIS IS A TEST FOR RnsService +++");
		
	 	System.setProperty("it.polito.dp2.RNS.Random.seed","50");
	 	System.setProperty("it.polito.dp2.RNS.Random.testcase","1");
	 	System.setProperty("it.polito.dp2.RNS.RnsReaderFactory",
	 			"it.polito.dp2.RNS.Random.RnsReaderFactoryImpl");
	 	System.setProperty("it.polito.dp2.RNS.lab3.Neo4JURL",
	 			"http://localhost:7474/db");
	 	
	 	
		RnsService rnsService = 
		  new RnsService(UriBuilder.fromUri("http://localhost:8080/something"));
		
		
		// we do not set self
		
		String adminToken = "adminToken";
		
		try{
			RnsRootResponse rnsRoot = rnsService.getRnsRoot(null);
			RnsRootResponse rnsAdminRoot = rnsService.getRnsRoot(adminToken);
			List<Road> roads = rnsService.getRoads(adminToken);
			List<Place> places = rnsService.getPlaces(null, adminToken);
			List<Place> roadSegments = rnsService.getRoadSegments(null, adminToken);
			List<Place> parkingAreas = rnsService.getParkingAreas(null, adminToken);
			List<Place> gates = rnsService.getGates(null, adminToken);
			List<Connection> connections = rnsService.getConnections(adminToken);
			
			NewVehicle newVehicle = new NewVehicle();
			newVehicle.setId("AAAAA??"); //validation on name not done here
			newVehicle.setOriginId("Gate0");
			newVehicle.setDestinationId("SP0-S1");
			newVehicle.setType(VehicleType.CAR);
			NewVehicle newVehicle2 = new NewVehicle();
			newVehicle2.setId("BBBB"); //validation on name not done here
			newVehicle2.setOriginId("Gate2");
			newVehicle2.setDestinationId("SS0-S1");
			newVehicle2.setType(VehicleType.SHUTTLE);
			NewVehicle newVehicle3 = new NewVehicle();
			newVehicle3.setId("CCCC"); //validation on name not done here
			newVehicle3.setOriginId("Gate0");
			newVehicle3.setDestinationId("SP0-S1");
			newVehicle3.setType(VehicleType.CARAVAN);
			
			String newVehicleId = newVehicle.getId();
			String newVehicle2Id = newVehicle2.getId();
			String vehicleToken = "authTokenOf" + newVehicleId;
			
			AddVehicleResponse vehRes = rnsService.addVehicle(newVehicle, null);
			AddVehicleResponse vehRes2 = rnsService.addVehicle(newVehicle2, null);
			//AddVehicleResponse vehRes3EntranceRefusedFull = rnsService.addVehicle(newVehicle3);
			//AddVehicleResponse vehResEntranceRefusedExists = rnsService.addVehicle(newVehicle);
			Vehicle v2 = rnsService.getVehicle(newVehicle2Id, adminToken);
			
			///// no new path - new position in old path
			// List<String> noNewPath_InOldPath = rnsService.moveVehicle(newVehicleId,"SP0-S0",adminToken);
			///// no new path- new position NOT in old path
			// List<String> noNewPath_NotInOldPath = rnsService.moveVehicle(newVehicleId,"SS0-S4",vehicleToken);
			/// YES new path - new position NOT in old path
			List<String> newPath_notInOldPath = rnsService.moveVehicle(newVehicleId,"PA000",vehicleToken);
			List<String> newPathNotMoving = rnsService.moveVehicle(newVehicleId,"PA000",vehicleToken);
			//List<String> newPathUnavailable = rnsService.moveVehicle(newVehicleId,"Gate0",vehicleToken);
			rnsService.changeVehicleState(newVehicleId, VehicleState.PARKED, vehicleToken);
			rnsService.changeVehicleState(newVehicleId, VehicleState.IN_TRANSIT, vehicleToken);
			rnsService.changeVehicleState(newVehicleId, VehicleState.PARKED, vehicleToken);
			
			Calendar myCal = new GregorianCalendar();
			myCal.setTime(new Date());
			myCal.add(Calendar.SECOND, -30);
			List<VehicleType> vTypes = new ArrayList<VehicleType>();
			vTypes.add(VehicleType.CAR);
			List<Vehicle> vehicles = rnsService.getVehicles(myCal, vTypes, 
					VehicleState.PARKED, adminToken);
			
			Place v1pos = rnsService.getPositionOfVehicle(newVehicleId, adminToken);
			Place v2orig = rnsService.getOriginOfVehicle(newVehicle2Id, adminToken);
			Place v1dest = rnsService.getDestinationOfVehicle(newVehicleId, adminToken);
			
			PlacePath sgP1 = rnsService.getSuggestedPathOfVehicle(newVehicleId, adminToken);
			
			Road ss0 = rnsService.getRoad("SS0", adminToken);
			
			List<Place> places2 = rnsService.getPlaces("SS0", adminToken);
			List<Place> places3 = rnsService.getPlaces("Gat", adminToken);
			
			List<Place> roadSegs2 = rnsService.getRoadSegments("SS0", adminToken);
			boolean roadSegOk = places2.containsAll(roadSegs2);
			List<String> svcReq = new ArrayList<String>();
			svcReq.add("Toilet");
			svcReq.add("Bar");
			List<Place> parkAreas2 = rnsService.getParkingAreas(svcReq, adminToken);
			
			List<Place> gatez2 = rnsService.getGates(GateType.IN, adminToken);
			
			Place pa000 = rnsService.getPlace("PA000", adminToken);
			
			List<Place> nextPA000 = rnsService.getNextPlaces("PA000", adminToken);
			List<Vehicle> vehInPA000 = rnsService.getVehiclesInPlace("PA000", null,null,null,adminToken);
			List<Vehicle> vehInGate2 = rnsService.getVehiclesInPlace("Gate2", null,null,null,adminToken);
			List<Vehicle> vehInss0s0 = rnsService.getVehiclesInPlace("SS0-S0", null,null,null,adminToken);
			
			List<Connection> conn1s = rnsService.getConnections(adminToken);
			List<Connection> conn2s = rnsService.getConnections("Gate0", adminToken);
			Connection conn = rnsService.getConnection("Gate0", "SP0-S0", adminToken);
			
			List<Vehicle> vehs = rnsService.getVehicles(null, null, null, adminToken);
			
			List<String> path66 = rnsService.moveVehicle(newVehicleId,"Gate1",vehicleToken);
			rnsService.removeVehicle(newVehicleId, "Gate1", vehicleToken);
			//// GATE UNREACHABLE
			// rnsService.removeVehicle(newVehicleId, "Gate2", vehicleToken);
			rnsService.removeVehicle(newVehicle2Id , null, adminToken);
			System.out.println("+++++ GOOD ENDING :) +++++");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("+++++ BAD ENDING :( +++++");
		}
		
	}
	
	private final PathFinderRns pathFinder;
	private final String xsdNeo4j = "xsd/rns4neo4j.xsd";
	
	//rnsDB must be thread safe
	private final RnsDB rnsDB;
	//TODO: how much?
	private final int pathMaxLength = 1000000;
	
	public RnsService(UriBuilder baseUriBuilder) throws RnsServiceException{
		try {
			rnsDB = RnsDB.getInstance(baseUriBuilder);
		} catch (RnsReaderException | FactoryConfigurationError | DatatypeConfigurationException e) {
			throw new RnsServiceException("Cannot instantiate rns reader"
					+ "into the rns DB", e);
		}
		
		try {
			if( !DEBUG_MODE )
				pathFinder = PathFinderRns.getInstance();
			else
				pathFinder = PathFinderRns.getNewInstance(xsdNeo4j);
			
			if(!pathFinder.isModelLoaded()){
				pathFinder.reloadModel(rnsDB.getRnsReader());
			}
		} catch (PathFinderException e) {
			throw new RnsServiceException("Cannot instantiate path finder", e);
		} catch (ServiceException | ModelException e) {
			throw new RnsServiceException("Cannot load path finder model", e);
		}
	}
	
	public Object getVehicleLock(){
		return rnsDB.getVehicleLock();
	}
	
	public Date getVehiclesLastModifiedDate(){
		return rnsDB.getVehiclesLastModifiedDate();
	}
	
	//// AUTHENTICATION METHODS
	/**
	 * Token used by admin to request privileged operations
	 */
	private String getAdminToken(){
		return "adminToken";
	}
	/**
	 * Token used by a vehicle to request operations
	 */
	private String getVehicleToken(String vehicleId){
		return "authTokenOf" + vehicleId;
	}
	/**
	 * It's needed to authenticate the admin with a token
	 */
	private boolean validateAdminToken(String authToken){
		return getAdminToken().equals(authToken);
	}
	/**
	 * It's needed to authenticate the vehicle (or admin)
	 */
	private boolean validateVehicleToken(String vehicleId, String authToken){
		return getVehicleToken(vehicleId).equals(authToken);
	}
	
	/**
	 * thread safe
	 * @param originId
	 * @param destinationId
	 * @throws RnsServiceException
	 */
	private List<String> findSuggestedPath(String originId, String destinationId)
			throws RnsServiceException
	{
		if(originId == null || destinationId == null){
			return null;
		}
		if(originId.equals(destinationId)){
			return new ArrayList<String>();
		}
		try{
			Set<List<String>> shortestPaths = 
				pathFinder.findShortestPaths(originId,
											destinationId,
											 pathMaxLength);
			Iterator<List<String>> it = shortestPaths.iterator();
			if(it.hasNext()){
				return it.next();
			}else{
				return new ArrayList<String>();
			}
		}catch(UnknownIdException | BadStateException | ServiceException e){
			throw new RnsServiceException("Neo4J error", e);
		}
	}

	
	// ADD ALL THE OPERATIONS OF THE REST API, BUT IN A LOGICAL LEVEL
	
	//// R1.Root
	/**
	 * thread safe
	 * @param authToken
	 */
	public RnsRootResponse getRnsRoot(String authToken){
		if(validateAdminToken(authToken)){
			return rnsDB.getAdminRnsRoot();
		}else{
			return rnsDB.getBasicRnsRoot();
		}
	}
	
	//// R2.Vehicles
	/**
	 * thread safe
	 * A new client vehicle requests to enter
	 * @param authToken 
	 */
	public AddVehicleResponse addVehicle(
			NewVehicle newVehicle, String authToken) throws 
		EntranceRefusedRnsException, PlaceNotFoundRnsException,
		WrongPlaceRnsException, RnsServiceException
	{		
		boolean vehicleAlreadyExists;
		synchronized(this.getVehicleLock()){
			vehicleAlreadyExists = rnsDB.getVehicleById(newVehicle.getId()) != null;
			if(vehicleAlreadyExists){
				throw new EntranceRefusedRnsException("Vehicle already in the system");
			}
		}
		
		Place origin = rnsDB.getPlaceById(newVehicle.getOriginId());
		Place destination = rnsDB.getPlaceById(newVehicle.getDestinationId());
		boolean unknownOriginOrDestination = 
				destination == null	|| origin == null;
		if(unknownOriginOrDestination){
			throw new PlaceNotFoundRnsException();
		}
		
		Gate g = origin.getGate();
		if (g == null){
			throw new WrongPlaceRnsException();
		}
		boolean validEnterPosition = 
				g.getType().equals(GateType.IN) ||
				g.getType().equals(GateType.INOUT);
		if(!validEnterPosition){
			throw new WrongPlaceRnsException();
		}
		
		boolean entranceAvailable = 
				rnsDB.placeAvailableById(newVehicle.getOriginId());
		if(!entranceAvailable){
			throw new EntranceRefusedRnsException("Place is full");
		}
		
		boolean sameOrigAndDest = 
			    newVehicle.getOriginId().equals(newVehicle.getDestinationId());
		List<String> idPath = new ArrayList<String>();
		if(!sameOrigAndDest){
			idPath = findSuggestedPath(
					newVehicle.getOriginId(), newVehicle.getDestinationId());
			if(idPath == null || idPath.isEmpty()){
				throw new EntranceRefusedRnsException("Destination unreachable");
			}
		}
		
		AddVehicleResponse response;
		Vehicle v;
		//we should be sure that entrance is available and block 
		//other modifications until inserting it
		synchronized(this.getVehicleLock()){
			vehicleAlreadyExists = rnsDB.getVehicleById(newVehicle.getId()) != null;
			if(vehicleAlreadyExists){
				throw new EntranceRefusedRnsException("Vehicle already in the system");
			}
			
			entranceAvailable = 
					rnsDB.placeAvailableById(newVehicle.getOriginId());
			if(!entranceAvailable){
				throw new EntranceRefusedRnsException("Place is full");
			}
			
			v = rnsDB.addVehicle(newVehicle);
			if(v==null){
				throw new RnsServiceException("NewVehicle not ok");
			}
			v.setUriPath((new ObjectFactory()).createUriPath());
			v.getUriPath().getPlaceUri().addAll(rnsDB.fromIdPathToUriPath(idPath));
			
			response = (new ObjectFactory()).createAddVehicleResponse();
			response.setVehicleUri(v.getSelf());
			response.setAuthToken(getVehicleToken(v.getId()));
			response.setPositionUri(v.getSelf() + "/position");
			response.setStateUri(v.getSelf() + "/state");
			response.setPlacesIdPath((new ObjectFactory()).createPlacesIdPath());
			response.getPlacesIdPath().getPlaceId().addAll(idPath);
			
			return response;
		}	
	}
	
	/**
	 * thread safe
	 * TODO is it safe to do two sync blocks?
	 * A client vehicle requests to exit from the system 
	 * (or admin deletes the vehicle)
	 */
	public void removeVehicle(String vehicleId, String outGateId, String authToken) 
		throws UnauthorizedRnsException, VehicleNotFoundRnsException,
		PlaceNotFoundRnsException, WrongPlaceRnsException, RnsServiceException
	{
		if(validateAdminToken(authToken)){
			synchronized(this.getVehicleLock()){
				this.getVehicle(vehicleId, authToken); //Check if vehicle exists
				rnsDB.removeVehicleById(vehicleId);
			}
		}
		else if(validateVehicleToken(vehicleId, authToken)){
			Place p = rnsDB.getPlaceById(outGateId);
			if(p == null){
				throw new PlaceNotFoundRnsException();
			}
			Gate g = p.getGate();
			if (g == null){
				// POSITION IS NOT A GATE, so surely it's not out/inout
				throw new WrongPlaceRnsException();
			}
				
			boolean validExitPosition = 
					g.getType().equals(GateType.INOUT) ||
					g.getType().equals(GateType.OUT);
			if(!validExitPosition){
				// POSITION IS NOT AN OUT/INOUT GATE
				throw new WrongPlaceRnsException();
			}
			
			String oldPositionId, oldPositionUri;
			synchronized(this.getVehicleLock()){
				Vehicle v = rnsDB.getVehicleById(vehicleId);
				if(v == null){
					throw new VehicleNotFoundRnsException();
				}			
				
				oldPositionUri = v.getPositionUri();
				oldPositionId = rnsDB.getPlaceByUri(oldPositionUri).getId();
			}
			
			//otherwise the vehicle is already in the exit gate
			//and the path would be empty
			boolean vehicleIsAlreadyInOutGate = outGateId.equals(oldPositionId);
			if (!vehicleIsAlreadyInOutGate){
				List<String> pathFromOldToExitPlace = 
						findSuggestedPath(oldPositionId, outGateId);
				if(pathFromOldToExitPlace == null || 
					pathFromOldToExitPlace.isEmpty()){
					throw new WrongPlaceRnsException("Exit gate unreachable");
				}
			}
			
			synchronized(this.getVehicleLock()){
				Vehicle v = rnsDB.getVehicleById(vehicleId);
				if(v == null){
					throw new VehicleNotFoundRnsException();
				}
				if(!oldPositionUri.equals(v.getPositionUri())){
					throw new RnsServiceException("Concurrent modify on vehicle");
				}
				
				rnsDB.removeVehicleById(vehicleId);
			}
		}
		else{
			throw new UnauthorizedRnsException();
		}
	}

	/**
	 * thread safe
	 * after moveVehicleById, it calls findPath that launches an exception
	 * if vehicle is removed during moving
	 * 
	 * A client vehicle requests to move into a new position 
	 * <<For simplicity, the service is not requested to guarantee that the maximum number
	 * of vehicles in a place is respected.>>
	 * @return Path of (id) places
	 */
	public List<String> moveVehicle(String vehicleId, String newPositionId, String authToken) throws
		UnauthorizedRnsException, VehicleNotFoundRnsException,
		PlaceNotFoundRnsException, WrongPlaceRnsException, RnsServiceException
	{
		if(!validateAdminToken(authToken) && !validateVehicleToken(vehicleId, authToken)){
			throw new UnauthorizedRnsException();
		}
		
		Place newPosition = rnsDB.getPlaceById(newPositionId);
		if(newPosition == null){
			throw new PlaceNotFoundRnsException("New position not found");
		}
		
		boolean moveIntoPath;
		Place oldPosition;
		String newPositionUri;
		String oldPositionId;
		String destinationId;
		Vehicle v;
		v = rnsDB.getVehicleById(vehicleId);
		
		synchronized(this.getVehicleLock()){
			if(v == null){
				throw new VehicleNotFoundRnsException();
			}
			oldPosition = rnsDB.getPlaceByUri(v.getPositionUri());
			Place destination = rnsDB.getPlaceByUri(v.getDestinationUri());
			if(destination == null || oldPosition == null){
				// it should never happen
				throw new RnsServiceException("Vehicle is not consistent");
			}
			
			newPositionUri = newPosition.getSelf();
			oldPositionId = oldPosition.getId();
			destinationId = destination.getId();
		
			if(v.getUriPath() == null){
				// it should never happen
				throw new RnsServiceException("Vehicle is not consistent");
			}
			moveIntoPath = v.getUriPath().getPlaceUri().contains(newPositionUri);
		}
		
		
		if(!moveIntoPath){
			boolean sameOldAndNewPlace = oldPositionId.equals(newPositionId);
			if (!sameOldAndNewPlace){
				List<String> pathFromOldToNewPlace = 
						findSuggestedPath(oldPositionId, newPositionId);		
				if(pathFromOldToNewPlace == null ||
						pathFromOldToNewPlace.isEmpty()){
					throw new WrongPlaceRnsException("New position unreachable");
				}
			}
			
			List<String> newIdPathToDestination = 
					findSuggestedPath(newPositionId, destinationId);
			
			if(newIdPathToDestination != null && !newIdPathToDestination.isEmpty()){
				List<String> newUriPath = 
						rnsDB.fromIdPathToUriPath(newIdPathToDestination);
				//check again if vehicle exists because
				//method is not globally synchronized
				//the only things that can happen in conflict during
				//the two sync blocks are deletion and another move of vehicle
				synchronized(this.getVehicleLock()){
					Vehicle v2 = rnsDB.getVehicleById(vehicleId);
					if(v2 == null){
						throw new VehicleNotFoundRnsException();
					}
					if(!oldPosition.getSelf().equals(v2.getPositionUri())){
						throw new RnsServiceException("Concurrent modify");
					}
					rnsDB.moveVehicleById(vehicleId, newPositionId);
					v2.getUriPath().getPlaceUri().clear();
					v2.getUriPath().getPlaceUri().addAll(newUriPath);
				}				
			}else{
				synchronized(this.getVehicleLock()){
					Vehicle v2 = rnsDB.getVehicleById(vehicleId);
					if(v2 == null){
						throw new VehicleNotFoundRnsException();
					}
					if(!oldPosition.getSelf().equals(v2.getPositionUri())){
						throw new RnsServiceException("Concurrent modify");
					}
					rnsDB.moveVehicleById(vehicleId, newPositionId);
				}
			}
			
			return newIdPathToDestination;
		}else{
			synchronized(this.getVehicleLock()){
				Vehicle v2 = rnsDB.getVehicleById(vehicleId);
				if(v2 == null){
					throw new VehicleNotFoundRnsException();
				}
				if(!oldPosition.getSelf().equals(v2.getPositionUri())){
					throw new RnsServiceException("Concurrent modify");
				}
				rnsDB.moveVehicleById(vehicleId, newPositionId);
			}
			return null;
		}
	}

	/**
	 * thread safe (if vehicle is deleted after check, 
	 * it returns RnsServiceException)
	 * Change state of vehicleId
	 * @throws RnsServiceException 
	 */
	public void changeVehicleState(
		String vehicleId, VehicleState newState, String authToken) throws
		SyntaxRnsException, UnauthorizedRnsException, 
		VehicleNotFoundRnsException, RnsServiceException 
	{
		if(!validateAdminToken(authToken) && !validateVehicleToken(vehicleId, authToken)){
			throw new UnauthorizedRnsException();
		}
		
		if(newState == null){
			throw new SyntaxRnsException();
		}
		
		synchronized(this.getVehicleLock()){
			Vehicle v = rnsDB.getVehicleById(vehicleId);
			if(v == null){
				throw new VehicleNotFoundRnsException();
			}
			rnsDB.changeVehicleStateById(vehicleId, newState);
		}
		
	}
	
	
	//// R3.AdminVehicles
	/**
	 * thread safe
	 * Get vehicles with requested features
	 */
	public List<Vehicle> getVehicles(
			Calendar since, 
			Collection<VehicleType> typesRequested,
			VehicleState stateRequested, String authToken) 
		throws UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		return rnsDB.getVehicles(since, typesRequested, stateRequested);
	}
	
	/**
	 * thread safe
	 * Get vehicle with vehicleId
	 */
	public Vehicle getVehicle(String vehicleId, String authToken) throws
		VehicleNotFoundRnsException, UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		synchronized(this.getVehicleLock()){
			Vehicle v = rnsDB.getVehicleById(vehicleId);
			if(v == null){
				throw new VehicleNotFoundRnsException();
			}
			return v;
		}
	}
	
	/**
	 * thread safe
	 * Get current position place of vehicleId
	 */
	public Place getPositionOfVehicle(String vehicleId, String authToken) throws
		UnauthorizedRnsException, VehicleNotFoundRnsException, RnsServiceException
	{
		Place p;
		synchronized(this.getVehicleLock()){
			Vehicle v = this.getVehicle(vehicleId, authToken);
			p = rnsDB.getPlaceByUri(v.getPositionUri());
		}
		if(p == null){
			// INTERNAL SERVER ERROR BECAUSE IT SHOULD NEVER HAPPEN
			throw new RnsServiceException("Invalid position");
		}
		return p;
	}
	
	/**
	 * thread safe
	 * Get origin place of vehicleId
	 */
	public Place getOriginOfVehicle(String vehicleId, String authToken) throws
		UnauthorizedRnsException, VehicleNotFoundRnsException, RnsServiceException
	{
		Place p;
		synchronized(this.getVehicleLock()){
			Vehicle v = this.getVehicle(vehicleId, authToken);
			p = rnsDB.getPlaceByUri(v.getOriginUri());
		}
		if(p == null){
			// INTERNAL SERVER ERROR BECAUSE IT SHOULD NEVER HAPPEN
			throw new RnsServiceException("Invalid origin");
		}
		
		return p;
	}
	
	/**
	 * thread safe
	 * Get destination place of vehicleId
	 */
	public Place getDestinationOfVehicle(String vehicleId, String authToken) throws
		VehicleNotFoundRnsException, RnsServiceException, UnauthorizedRnsException
	{
		Place p;
		synchronized(this.getVehicleLock()){
			Vehicle v = this.getVehicle(vehicleId, authToken);
			p = rnsDB.getPlaceByUri(v.getDestinationUri());
		}
		if(p == null){
			// INTERNAL SERVER ERROR BECAUSE IT SHOULD NEVER HAPPEN
			throw new RnsServiceException("Invalid destination");
		}
		
		return p;
	}
	
	/**
	 * thread safe (if vehicle is deleted after check, 
	 * it returns RnsServiceException)
	 * Get current suggested path of vehicleId
	 * @throws RnsServiceException 
	 */
	public PlacePath getSuggestedPathOfVehicle(String vehicleId, String authToken) throws
		UnauthorizedRnsException, VehicleNotFoundRnsException, RnsServiceException
	{
		//check if vehicle exists and authentication
		this.getVehicle(vehicleId, authToken); 
		
		PlacePath placePath = (new ObjectFactory()).createPlacePath();
		synchronized(this.getVehicleLock()){
			placePath.getPlace().addAll(rnsDB.getSuggestedPathOfVehicle(vehicleId));
			return placePath;
		}
	}
	
	//// R4.AdminRoads
	/**
	 * thread safe (roads don't change)
	 * Get all the roads
	 */
	public List<Road> getRoads(String authToken) throws
		UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		return rnsDB.getRoads();
	}
	
	/**
	 * thread safe (roads don't change)
	 * Get the road roadName
	 */
	public Road getRoad(String roadName, String authToken) throws 
		UnauthorizedRnsException, RoadNotFoundRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		Road r = rnsDB.getRoadById(roadName);
		if(r == null){
			throw new RoadNotFoundRnsException();
		}
		
		return r;
	}
	
	//// R5.AdminPlaces
	/**
	 * thread safe (places don't change)
	 * Get places starting with idPrefix
	 */
	public List<Place> getPlaces(String idPrefix, String authToken) 
		throws UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		return rnsDB.getPlaces(idPrefix);
	}
	
	/**
	 * thread safe (places don't change)
	 * Get all the roadSegments of roadNameRequested
	 */
	public List<Place> getRoadSegments(String roadNameRequested, String authToken) 
		throws UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		return rnsDB.getRoadSegments(roadNameRequested);
	}
	
	/**
	 * thread safe (places don't change)
	 * Get all the parkingAreas with all the servicesRequested
	 */
	public List<Place> getParkingAreas(Collection<String> servicesRequested, String authToken)
		throws UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		return rnsDB.getParkingAreas(servicesRequested);
	}
	
	/**
	 * thread safe (places don't change)
	 * Get all the gates with the typeRequested
	 */
	public List<Place> getGates(GateType typeRequested, String authToken)
		throws UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		return rnsDB.getGates(typeRequested);
	}
	
	/**
	 * thread safe (places don't change)
	 * Get the place with the placeId
	 */
	public Place getPlace(String placeId, String authToken) throws
		UnauthorizedRnsException, PlaceNotFoundRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		Place p = rnsDB.getPlaceById(placeId);
		if(p == null){
			throw new PlaceNotFoundRnsException();
		}
		
		return p;
	}
	
	/**
	 * thread safe (places don't change)
	 * Get all the nextPlaces of placeId
	 */
	public List<Place> getNextPlaces(String placeId, String authToken) throws
		PlaceNotFoundRnsException, UnauthorizedRnsException
	{		
		// check if place exists and authenticate
		this.getPlace(placeId, authToken);
		return rnsDB.getNextPlaces(placeId);
	}
	
	/**
	 * thread safe (places don't change)
	 * Get all the vehicles in the placeId
	 * @throws RnsServiceException 
	 */
	public List<Vehicle> getVehiclesInPlace(String placeId,
			Calendar since, 
			Collection<VehicleType> typesReq,
			VehicleState stateReq, String authToken) 
		throws PlaceNotFoundRnsException, UnauthorizedRnsException, RnsServiceException
	{
		// check if place exists
		this.getPlace(placeId, authToken);
		return rnsDB.getVehiclesInPlace(placeId, since, typesReq, stateReq);
	}
	
	//// R6.AdminConnections
	/**
	 * thread safe
	 * Get all the connections
	 */
	public List<Connection> getConnections(String authToken)
		throws UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		return rnsDB.getConnections();
	}
	
	/**
	 * thread safe
	 * Get all the connections starting from fromId
	 */
	public List<Connection> getConnections(String fromId, String authToken)
		throws UnauthorizedRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		return rnsDB.getConnections(fromId);
	}
	
	/**
	 * thread safe
	 * Get all the connection (fromId, toId)
	 * @throws UnauthorizedRnsException
	 * @throws NotFoundRnsException 
	 */
	public Connection getConnection(String fromId, String toId, String authToken) 
		throws UnauthorizedRnsException, NotFoundRnsException
	{
		if(!validateAdminToken(authToken)){
			throw new UnauthorizedRnsException();
		}
		
		Connection c = rnsDB.getConnectionById(fromId, toId);
		if(c == null){
			throw new NotFoundRnsException();
		}
		
		return c;
	}
}
