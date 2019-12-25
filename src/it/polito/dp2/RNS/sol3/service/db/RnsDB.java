package it.polito.dp2.RNS.sol3.service.db;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.FactoryConfigurationError;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RnsReaderFactory;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.sol3.jaxb.Connection;
import it.polito.dp2.RNS.sol3.jaxb.GateType;
import it.polito.dp2.RNS.sol3.jaxb.NewVehicle;
import it.polito.dp2.RNS.sol3.jaxb.ObjectFactory;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.RnsRootResponse;
import it.polito.dp2.RNS.sol3.jaxb.Road;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;
import it.polito.dp2.RNS.sol3.jaxb.VehicleState;
import it.polito.dp2.RNS.sol3.jaxb.VehicleType;
import it.polito.dp2.RNS.sol3.service.exceptions.RnsServiceException;

/**
 * This class reads the places and connections from a RnsReader and
 * stores them in the resources of the web service
 * @author s253137
 *
 * Vehicles interfaces:
 * + addVehicle(newVehicle)
 * + getVehiclesInPlace(placeId)
 * + getVehicles(...)
 * + getVehicleById(id)
 * + getVehicleByUri(uri)
 */
public class RnsDB {

	/**
	 * TEST MAIN
	 * @throws DatatypeConfigurationException 
	 */
	public static void main(String[] args) throws RnsReaderException, FactoryConfigurationError, DatatypeConfigurationException {
		//<sysproperty key="it.polito.dp2.RNS.Random.seed" value="${seed}"/>
	 	//<sysproperty key="it.polito.dp2.RNS.Random.testcase" value="${testcase}"/>
	 	//<sysproperty key="it.polito.dp2.RNS.RnsReaderFactory" value="${testRnsReaderFactory}"/>
	 	
		System.out.println("+++ ATTENTION, THIS IS A TEST FOR RnsDB +++");
		
	 	System.setProperty("it.polito.dp2.RNS.Random.seed","50");
	 	System.setProperty("it.polito.dp2.RNS.Random.testcase","1");
	 	System.setProperty("it.polito.dp2.RNS.RnsReaderFactory","it.polito.dp2.RNS.Random.RnsReaderFactoryImpl");
	 	
		RnsDB rnsDB = new RnsDB(UriBuilder.fromUri("http://localhost:8080/something"));
		for(Road r: rnsDB.getRoads()){
			System.out.println("r: " + r.getRoadName());
		}
		for(Place p: rnsDB.getPlaces(null)){
			System.out.print("p: " + p.getId());
			if(p.getRoadSegment() != null){
				System.out.print(" " + p.getRoadSegment().getRoadName());
			}
			System.out.println();
		}
		for(Connection c: rnsDB.getConnections()){
			System.out.println("c: " + c.getFromUri() + "," + c.getToUri());
		}
	}

/////////////////////////////////////////////////////////////////////////////////
	
	private static RnsDB instance;
	
	private static Logger logger = Logger.getLogger(RnsReader.class.getName());
	private final RnsReader rnsReader;
	private final UriBuilder uriBuilder;
	private final DatatypeFactory datatypeFactory;
	private RnsRootResponse basicRnsRoot;
	private RnsRootResponse adminRnsRoot;
	
	/*
	 * each get operation on these are thread safe because
	 * roads, places and connections do not vary
	 * SET ARE SYNCHRONIZED BECAUSE THEY ARE CALLED ONCE.
	 * TO AVOID PROBLEMS IF CALLED MORE THAN ONCE
	 */
	private Map<String,Road> roadsByUri = 
			new ConcurrentHashMap<String, Road>();
	private Map<String,Place> placesByUri = 
			new ConcurrentHashMap<String, Place>();
	private Map<String,Connection> connectionsByUri = 
			new ConcurrentHashMap<String, Connection>();
	/*
	 * it may change: THE ONLY SHARED DATA
	 */
	private Map<String,Vehicle> vehiclesByUri = 
			new ConcurrentHashMap<String, Vehicle>();
	private Date vehiclesLastModifiedDate = new Date();
	
	public Date getVehiclesLastModifiedDate(){
		return vehiclesLastModifiedDate;
	}
	
	private RnsDB(UriBuilder inputUriBuilder) throws
		RnsReaderException, FactoryConfigurationError, 
		DatatypeConfigurationException
	{
		uriBuilder = inputUriBuilder;
		rnsReader = RnsReaderFactory.newInstance().newRnsReader();
		datatypeFactory = DatatypeFactory.newInstance();
		init(rnsReader);
		logger.info("RnsDB Initialization from "
				+ "internal RnsReader Completed Successfully");
	}
	
	/**
	 * init is done at the beginning, so the performance of
	 * synchronizing is not our concern
	 */
	private void init(RnsReader inputRnsReader){
		synchronized(this.getVehicleLock()){
			setRnsRoot();
			//connections first because they are needed for
			//computing next places
			setConnections(inputRnsReader); 
			setRoads(inputRnsReader);
			setPlaces(inputRnsReader);		
		}
	}
	
	public Object getVehicleLock(){
		return vehiclesByUri;
	}
	
	public static RnsDB getInstance(UriBuilder inputUriBuilder) 
		throws RnsReaderException, FactoryConfigurationError,
		DatatypeConfigurationException
	{
		if(instance == null){
			instance = new RnsDB(inputUriBuilder);
		}
		return instance;
	}
	
	public RnsReader getRnsReader(){
		if (rnsReader == null){
			throw new RuntimeException("rnsReader uninitialized");
		}
		return rnsReader;
	}
	
	private UriBuilder getUriBuilder(){
		if (uriBuilder == null){
			throw new RuntimeException("uriBuilder uninitialized");
		}
		return uriBuilder.clone();
	}

	private XMLGregorianCalendar getNow(){
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		return datatypeFactory.newXMLGregorianCalendar(c);
	}
	
	private void setLastModifiedDateNow(Vehicle v){
		synchronized(this.getVehicleLock()){
			if(v != null){
				v.setLastModifiedDate(getNow());
				vehiclesLastModifiedDate = new Date(); //set now
			}
		}
	}
	
/////////////////////////////////////////////////////////////////////////////////
	
	//// 0.GENERIC RESOURCE CODE
	//enforce the possibility of calling them just once
	private void setBasicRnsRoot(){
		if(basicRnsRoot != null){
			throw new RuntimeException("Call it once");
		}
		basicRnsRoot = (new ObjectFactory()).createRnsRootResponse();
		UriBuilder rnsUri = getUriBuilder().path("rns");
		basicRnsRoot.setSelf(rnsUri.build().toString());
		basicRnsRoot.setVehicles(rnsUri.path("vehicles").build().toString());
	}
	
	private void setRnsRoot(){
		if(adminRnsRoot != null){
			throw new RuntimeException("Call it once");
		}
		if(basicRnsRoot == null){
			setBasicRnsRoot();
		}
		adminRnsRoot = (new ObjectFactory()).createRnsRootResponse();
		adminRnsRoot.setSelf(basicRnsRoot.getSelf());
		adminRnsRoot.setVehicles(basicRnsRoot.getVehicles());
		
		UriBuilder rootBuilder = UriBuilder.fromUri(adminRnsRoot.getSelf());
		adminRnsRoot.setRoads(rootBuilder.clone().path("roads").build().toString());
		adminRnsRoot.setPlaces(rootBuilder.clone().path("places").build().toString());
		adminRnsRoot.setGates(rootBuilder.clone().path("gates").build().toString());
		adminRnsRoot.setParkingAreas(rootBuilder.clone().path("parkingAreas").build().toString());
		adminRnsRoot.setRoadSegments(rootBuilder.clone().path("roadSegments").build().toString());		
		adminRnsRoot.setConnections(rootBuilder.clone().path("connections").build().toString());
	}
	
	/**
	 * thread safe
	 */
	public RnsRootResponse getBasicRnsRoot(){
		return basicRnsRoot;
	}
	
	/**
	 * thread safe
	 */
	public RnsRootResponse getAdminRnsRoot(){
		return adminRnsRoot;
	}

/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * We can build URI from ID, but not the opposite
	 * ID --------> URI
	 * URI --xxx--> ID
	 */
	private String buildUriFromId(String baseUri, String id){
		return baseUri + "/" + id;
	}
	
	private String buildRoadUriFromId(String roadId){
		return buildUriFromId(getAdminRnsRoot().getRoads(), roadId);
	}
	
	private String buildPlaceUriFromId(String placeId){
		return buildUriFromId(getAdminRnsRoot().getPlaces(), placeId);
	}
	
	private String buildConnectionUriFromId(String fromId, String toId){
		return buildUriFromId(
				getAdminRnsRoot().getConnections(),
				fromId + "/" + toId);
	}
	
	private String buildVehicleUriFromId(String vehicleId){
		return buildUriFromId(getAdminRnsRoot().getVehicles(), vehicleId);
	}
	
/////////////////////////////////////////////////////////////////////////////////
	
	/**
	* thread safe
	* Put a generic resource in the 
	* correct map with the correct uri
	*/
	private <TRes> void putResource(
		Map<String,TRes> mapUri, TRes resource, String uri)
	{
		mapUri.put(uri, resource);
	}
	
	/**
	 * thread safe
	 */
	private <TRes> void putResource(TRes resource){
		synchronized(this.getVehicleLock()){
			//Roads, places, connections, vehicles
			if(resource instanceof Road){
				Road r = (Road) resource;
				putResource(roadsByUri, r, r.getSelf());
			}else if(resource instanceof Place){
				Place p = (Place) resource;
				putResource(placesByUri, p, p.getSelf());
			}else if(resource instanceof Connection){
				Connection c = (Connection) resource;
				putResource(connectionsByUri, c, c.getSelf());
			}else if(resource instanceof Vehicle){
				Vehicle v = (Vehicle) resource;
				putResource(vehiclesByUri, v, v.getSelf());
			}
		}
	}
	
	/**
	 * thread safe
	 * Remove a generic resource from the map
	 * with the given uri
	 */
	private <TRes> void removeResource( 
		Map<String,TRes> mapUri, String uri)
	{
		mapUri.remove(uri);
	}

	public List<String> fromIdPathToUriPath(List<String> idPath){
		List<String> uriPath = new ArrayList<String>();
		for(String pathPlaceId: idPath){
			uriPath.add( getPlaceById(pathPlaceId).getSelf() );
		}
		return uriPath;
	}
	
	
/////////////////////////////////////////////////////////////////////////////////
	
	//// a.ROADS
	/**
	 * Thread safe
	 */
	public List<Road> getRoads() {
		return new ArrayList<Road>(roadsByUri.values());
	}

	/**
	 * Thread safe
	 */
	public Road getRoadById(String roadId){
		return roadsByUri.get(buildRoadUriFromId(roadId));
	}
	
	/**
	 * Thread safe
	 */
	public Road getRoadByUri(String roadUri){
		return roadsByUri.get(roadUri);
	}
	
	/**
	 * Exception and thread safe
	 */
	private void setRoads(RnsReader rnsReader) {
		// s is never null
		roadsByUri.clear();
		Set<RoadSegmentReader> s = rnsReader.getRoadSegments(null);
		
		for(RoadSegmentReader rsr : s){
			Road r = (new ObjectFactory()).createRoad();
			r.setRoadName(rsr.getRoadName());
			String roadUri = buildRoadUriFromId(rsr.getRoadName());
			r.setSelf(roadUri);
			if(!roadsByUri.containsKey(roadUri)){
				putResource(r);
			}
		}
	}

/////////////////////////////////////////////////////////////////////////////////
	
	//// b.ROADS
	/**
	 * iteration is thread safe and places don't change
	 * @param idPrefix
	 * @return
	 */
	public List<Place> getPlaces(String idPrefix) {
		List<Place> placesRequested = new ArrayList<Place>();
		// iteration is thread safe
		for(Place p : placesByUri.values()){
			boolean prefixOk = true;
			if(idPrefix != null){
				prefixOk = p.getId().startsWith(idPrefix);
			}
			
			if(prefixOk){
				placesRequested.add(p);
			}
		}
		return placesRequested;
	}

	/**
	 * iteration is thread safe and places don't change
	 * @param idPrefix
	 * @return
	 */
	public List<Place> getRoadSegments(String roadNameRequested) {		
		List<Place> roadSegmentsRequested = new ArrayList<Place>();
		for(Place p: placesByUri.values()){
			boolean pIsRoadSegment = p.getRoadSegment() != null;
			
			if(pIsRoadSegment){
				boolean roadNameIsEqual = true;
				if(roadNameRequested != null){
					roadNameIsEqual = p.getRoadSegment().
									  getRoadName().equals(roadNameRequested);
				}
				
				if(roadNameIsEqual){
					roadSegmentsRequested.add(p);
				}	
			}
				
		}
		return roadSegmentsRequested;
	}
	
	/**
	 * iteration is thread safe and places don't change
	 * WARNING!
	 * You must retrieve all the parking areas
	 * having all the services given in the set servicesRequested,
	 * NOT having at least one of the services given in the set.
	 * @param idPrefix
	 * @return
	 */
	public List<Place> getParkingAreas(Collection<String> servicesRequested) {
		List<Place> parkingAreasRequested = new ArrayList<Place>();
		for(Place p: placesByUri.values()){
			boolean pIsParkingArea = p.getParkingArea() != null;
			if(pIsParkingArea){
				boolean hasServices = true;
				if(servicesRequested != null && !servicesRequested.isEmpty()){
					hasServices = p.getParkingArea().getServicesId().getServiceId()
							      .containsAll(
							    		  servicesRequested
							      );	
				}				
				if(hasServices){
					parkingAreasRequested.add(p);
				}
			}
			
		}
		return parkingAreasRequested;
	}
	
	/**
	 * iteration is thread safe and places don't change
	 * @param idPrefix
	 * @return
	 */
	public List<Place> getGates(GateType typeRequested) {
		List<Place> gatesRequested = new ArrayList<Place>();
		for(Place p: placesByUri.values()){
			boolean pIsGate = p.getGate() != null;
			if(pIsGate){
				boolean typeIsEqual = true;
				if(typeRequested != null){
					typeIsEqual = p.getGate().getType().
									equals(typeRequested);
				}					
				if(typeIsEqual){
					gatesRequested.add(p);
				}
			}
		}
		return gatesRequested;
	}
	
	/**
	 * thread safe
	 */
	public Place getPlaceById(String placeId){
		return placesByUri.get(buildPlaceUriFromId(placeId));
	}
	
	/**
	 * thread safe
	 */
	public Place getPlaceByUri(String placeUri){
		return placesByUri.get(placeUri);
	}
	
	/**
	 * iteration is thread safe
	 * we do not change places nor connections
	 */
	public List<Place> getNextPlaces(String placeId)
	{
		Place p = this.getPlaceById(placeId);
		
		List<Place> nextPlaces = new ArrayList<Place>();
		for(Connection c: connectionsByUri.values()){
			boolean pIsFrom = c.getFromUri().equals(p.getSelf());
			if(pIsFrom){
				nextPlaces.add(getPlaceByUri(c.getToUri()));
			}
		}
		
		return nextPlaces;
	}
	
	/**
	 * thread/exception safe
	 * (we are willing to exclude vehicles added during
	 * the execution of the method)
	 * @param placeId
	 * @return
	 * @throws RnsServiceException 
	 */
	public List<Vehicle> getVehiclesInPlace(String placeId,
			Calendar since, 
			Collection<VehicleType> typesReq,
			VehicleState stateReq) 
			throws RnsServiceException
	{
		List<Vehicle> vehiclesInPlace = new ArrayList<Vehicle>();
		Place p = this.getPlaceById(placeId);
		if(p==null){
			throw new RnsServiceException(
			"CALLER should have checked place existance");
		}
		
		String placeUri = p.getSelf();
		
		List<Vehicle> vehs = this.getVehicles(since, typesReq, stateReq);
		
		//it should be super thread-exception safe
		for(Vehicle v: vehs){
			String positionUri;
			synchronized(this.getVehicleLock()){
				if( v == null)
					throw new RnsServiceException();
				positionUri = v.getPositionUri();
				if( placeUri.equals(positionUri) ){
					vehiclesInPlace.add(v);
				}
			}		
		}
		return vehiclesInPlace;
	}
	
	/**
	* Exception safe
	*/
	private void setPlaces(RnsReader rnsReader){
		placesByUri.clear();
		setGates(rnsReader);
		setParkingAreas(rnsReader);
		setRoadSegments(rnsReader);
	}
	
	private List<String> getUriOfNextPlaces(String placeUri){
		List<String> uriOfNextPlaces = new ArrayList<String>();
		for(Connection c: connectionsByUri.values()){
			boolean pIsFrom = c.getFromUri().equals(placeUri);
			if(pIsFrom){
				uriOfNextPlaces.add(c.getToUri());
			}
		}
		return uriOfNextPlaces;
	}
	
	/**
	 * Thread/exception safe
	 * @param pr
	 * @return
	 */
	private Place buildPlace(PlaceReader pr){
		//id, self, capacity
		//roadSegment/parkingArea/gate
		//nextPlaces, vehiclesUri
		
		Place p = (new ObjectFactory()).createPlace();
		p.setId(pr.getId());
		p.setSelf(buildPlaceUriFromId(pr.getId()));
		p.setCapacity(BigInteger.valueOf(pr.getCapacity()));
		p.setNextPlacesUri(p.getSelf() + "/nextPlaces");
		p.setVehiclesUri(p.getSelf() + "/vehicles");
		p.setUriOfNextPlaces((new ObjectFactory()).createUriOfNextPlaces());
		p.getUriOfNextPlaces().getPlaceUri().addAll(
				getUriOfNextPlaces(p.getSelf()));
		return p;
	}
	
	/**
	* Exception safe
	*/
	private void setGates(RnsReader rnsReader){
		// s is never null
		Set<GateReader> s = rnsReader.getGates(null);
		for (GateReader gr : s){
			Place p = buildPlace(gr);
			
			p.setGate((new ObjectFactory()).createGate());
			String gRead = gr.getType().toString();
			p.getGate().setType(
					it.polito.dp2.RNS.sol3.jaxb.GateType.valueOf(gRead));
			
			putResource(p);
		}
	}
	
	/**
	* Exception safe
	*/
	private void setParkingAreas(RnsReader rnsReader){
		// s is never null
		Set<ParkingAreaReader> s = rnsReader.getParkingAreas(null);
		for (ParkingAreaReader par : s){
			Place p = buildPlace(par);
			
			p.setParkingArea((new ObjectFactory()).createParkingArea());
			p.getParkingArea().setServicesId(
					(new ObjectFactory()).createParkingAreaServicesId());
			p.getParkingArea().getServicesId().getServiceId().
				addAll( par.getServices() );
			
			putResource(p);
		}		
	}
	
	
	/**
	* Exception safe
	*/
	private void setRoadSegments(RnsReader rnsReader){
		// s is never null
		Set<RoadSegmentReader> s = rnsReader.getRoadSegments(null);
		for (RoadSegmentReader rsr : s){
			Place p = buildPlace(rsr);
			
			p.setRoadSegment((new ObjectFactory()).createRoadSegment());
			p.getRoadSegment().setName(rsr.getName());
			p.getRoadSegment().setRoadName(rsr.getRoadName());
			
			putResource(p);		
		}	
	}

	//// c.CONNECTIONS
	/**
	 * thread safe
	 * @return
	 */
	public List<Connection> getConnections() {
		return new ArrayList<Connection>(connectionsByUri.values());
	}
	
	/**
	 * thread safe
	 * @return
	 */
	public List<Connection> getConnections(String fromIdRequested) {
		List<Connection> connectionsRequested = new ArrayList<Connection>();
		for(Connection c: connectionsByUri.values()){
			String fromId = getPlaceByUri(c.getFromUri()).getId();
			if(fromId.equals(fromIdRequested)){
				connectionsRequested.add(c);
			}
		}
		return connectionsRequested;
	}
	
	/**
	 * thread safe
	 * @return
	 */
	public Connection getConnectionById(String fromId, String toId) {
		return connectionsByUri.get(buildConnectionUriFromId(fromId,toId));
	}
	
	/**
	 * thread safe
	 * @return
	 */
	public Connection getConnectionByUri(String connectionUri) {
		return connectionsByUri.get(connectionUri);
	}

	/**
	* Exception safe
	*/
	private void setConnections(RnsReader rnsReader){
		connectionsByUri.clear();
		// s is never null
		Set<ConnectionReader> s = rnsReader.getConnections();
		for (ConnectionReader cr : s){
			Connection c = (new ObjectFactory()).createConnection();
			String fromId = cr.getFrom().getId();
			String toId = cr.getTo().getId();
			c.setSelf( buildConnectionUriFromId(fromId, toId) );
			
			String fromUri = buildPlaceUriFromId(fromId);
			String toUri = buildPlaceUriFromId(toId);
			
			c.setFromUri(fromUri);
			c.setToUri(toUri);
			
			this.putResource(c);
		}
	}
/////////////////////////////////////////////////////////////////////////////
	
	//// d.VEHICLES
	/**
	 * It's thread safe 
	 * TODO it's ok with performance?
	 * Get vehicles with requested features
	 */
	public List<Vehicle> getVehicles
	(Calendar since, Collection<VehicleType> typesReq, VehicleState stateReq) {		
		List<Vehicle> vehiclesRequested = new ArrayList<Vehicle>();
		
		for(Vehicle v: vehiclesByUri.values()){
			synchronized(this.getVehicleLock()){
				boolean timeOk = true;		
				if(since != null){
					timeOk = v.getEntryTime().toGregorianCalendar().after(since);
				}
				
				boolean typeOk = true;
				if(typesReq != null && !typesReq.isEmpty()){
					typeOk = typesReq.contains(v.getType());
				}
				
				boolean stateOk = true;
				if(stateReq != null){
					stateOk = v.getState().equals(stateReq);
				}
				
//				String intTimeOk = timeOk ? "1" : "0";
//				String intTypeOk = typeOk ? "1" : "0";
//				String intStateOk = stateOk ? "1" : "0";
//				System.out.println("Vehicle values " + intTimeOk + intTypeOk + intStateOk);
				if(timeOk && stateOk && typeOk){
					vehiclesRequested.add(v);
				}		
			}
		}
		
		return vehiclesRequested;
	}
	
	/**
	 * thread safe
	 * @param vehicleId
	 * @return
	 */
	public Vehicle getVehicleById(String vehicleId){
		return vehiclesByUri.get(buildVehicleUriFromId(vehicleId));
	}
	
	/**
	 * thread safe
	 * @param vehicleUri
	 * @return
	 */
	public Vehicle getVehicleByUri(String vehicleUri){
		return vehiclesByUri.get(vehicleUri);
	}
	
	/**
	 * thread and exception safe
	 * @param vehicleId
	 * @return
	 * @throws RnsServiceException 
	 */
	public List<Place> getSuggestedPathOfVehicle(String vehicleId)
			throws RnsServiceException{
		List<Place> placePath = new ArrayList<Place>();
		
		List<String> uriPath;
		synchronized(this.getVehicleLock()){
			Vehicle v = getVehicleById(vehicleId);
			if (v == null || v.getUriPath() == null){
				throw new RnsServiceException(
				"caller should have checked vehicle existance");
			}
			uriPath = v.getUriPath().getPlaceUri();
		}
		
		
		for(String placeUri: uriPath){
			Place p = getPlaceByUri(placeUri);
			if(p == null)
				throw new RnsServiceException("Place in uriPath not found");
			placePath.add( p );
		}
		
		return placePath;
	}
	
	/**
	 * thread and exception safe because places don't change
	 * and getVehiclesInPlace is safe
	 * @param placeId
	 * @return
	 * @throws RnsServiceException 
	 */
	public boolean placeAvailableById(String placeId)
		throws RnsServiceException{
		Place place = getPlaceById(placeId);
		if(place == null)
			return false;
		long nOfVehiclesInPlace = 
			this.getVehiclesInPlace(placeId,null,null,null).size();
		long maxVehiclesInPlace = place.getCapacity().longValue();
		return nOfVehiclesInPlace < maxVehiclesInPlace;
	}
	
	/**
	 * thread safe because putResource is safe
	 * @param v
	 */
	private void addVehicle(Vehicle v){
		this.putResource(v);
	}
	
	/**
	 * thread and exception safe because addVehicle(Vehicle) is safe
	 */
	public Vehicle addVehicle(NewVehicle newVehicle){
		if (newVehicle.getId()==null || newVehicle.getOriginId()==null ||
			newVehicle.getDestinationId()==null || newVehicle.getType()==null)
			return null;
		
		Vehicle v = (new ObjectFactory()).createVehicle();
		
		v.setId(newVehicle.getId());
		v.setSelf(buildVehicleUriFromId(v.getId()));
		XMLGregorianCalendar now = getNow();

		v.setEntryTime(now);
		v.setOriginUri(this.buildPlaceUriFromId(newVehicle.getOriginId()));
		v.setDestinationUri(this.buildPlaceUriFromId(newVehicle.getDestinationId()));
		v.setPositionUri(v.getOriginUri());
		v.setType(newVehicle.getType());
		v.setState(VehicleState.IN_TRANSIT);
		v.setPlacePathUri(v.getSelf() + "/suggestedPath");
		setLastModifiedDateNow(v);
		synchronized(this.getVehicleLock()){
			this.addVehicle(v);
			return v;
		}
	}
	
	/**
	 * thread safe because removeResource is safe
	 */
	public void removeVehicleById(String vehicleId){
		removeResource(vehiclesByUri,	buildVehicleUriFromId(vehicleId));
	}
	
	/**
	 * thread and exception safe
	 * it throws a rnsServiceException error if v not existent
	 * because rnsService doesn't need it
	 * @throws RnsServiceException 
	 */
	public void moveVehicleById(String vehicleId, String newPositionId) 
			throws RnsServiceException
	{
		String newPositionUri = this.buildPlaceUriFromId(newPositionId);
		synchronized(this.getVehicleLock()){
			Vehicle v = this.getVehicleById(vehicleId);
			if(v == null){
				throw new RnsServiceException("Vehicle should exist");
			}
			v.setPositionUri(newPositionUri);
			setLastModifiedDateNow(v);
		}
	}
	
	/**
	 * thread and exception safe
	 * it throws a rnsServiceException error if v not existent
	 * because rnsService doesn't need it
	 * @throws RnsServiceException 
	 */
	public void changeVehicleStateById(String vehicleId, VehicleState state) 
			throws RnsServiceException{
		synchronized(this.getVehicleLock()){
			Vehicle v = this.getVehicleById(vehicleId);
			if(v == null){
				throw new RnsServiceException("Vehicle should exist");
			}
			v.setState(state);
			setLastModifiedDateNow(v);
		}
	}
}
