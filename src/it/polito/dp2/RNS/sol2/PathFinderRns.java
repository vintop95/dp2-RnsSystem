package it.polito.dp2.RNS.sol2;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polito.dp2.RNS.*;
import it.polito.dp2.RNS.sol2.jaxb.ObjectFactory;
import it.polito.dp2.RNS.sol2.jaxb.Path;
import it.polito.dp2.RNS.sol2.jaxb.PostNodeRequest;
import it.polito.dp2.RNS.sol2.jaxb.PostNodeResponse;
import it.polito.dp2.RNS.sol2.jaxb.PostRelationshipRequest;
import it.polito.dp2.RNS.sol2.jaxb.PostRelationshipResponse;
import it.polito.dp2.RNS.sol2.jaxb.ServiceRootResponse;
import it.polito.dp2.RNS.sol2.jaxb.ShortestPathRequest;
import it.polito.dp2.RNS.sol2.jaxb.ShortestPathRequest.Relationships;
// import it.polito.dp2.RNS.sol2.*;
import it.polito.dp2.RNS.sol2.lab2.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * ASSIGNMENT 3B VERSION
 * The client to be developed must take the form of a Java library that
 * implements the interface it.polito.dp2.RNS.lab2.PathFinder,
 * available in source form in the package of this assignment,
 * along with its documentation.
 * 
 * @author s253137
 */
public class PathFinderRns implements PathFinder
{	
	private static PathFinderRns instance;
	
	private Client client;
	private WebTarget baseTarget;
	private JAXBContext jc;
	private javax.xml.validation.Validator validator;
	private Logger logger;
	
	private String xsdFileLocation = "xsd/rns4neo4j.xsd";
	private final String xsdResourceLocation = "/xsd/rns4neo4j.xsd";
	private final String jaxbPackage = "it.polito.dp2.RNS.sol2.jaxb";
	
	// key: placeId, value: response of the node post with URIs
	private Map<String,PostNodeResponse> placeNodeResponse;
	// key: (fromId,toId), value: response of the relationship 
	// post with URIs
	private Map<PairType<String,String>,PostRelationshipResponse> 
		connectionRelationshipResponse;
	
	private boolean modelLoaded = false;
	private ServiceRootResponse serviceRootResponse;
	
	public static PathFinderRns getInstance() 
			throws PathFinderException{
		if(instance == null){
			instance = new PathFinderRns(InputStream.class, null);
		}
		return instance;
	}
	
	public static PathFinderRns getNewInstance(String xsdFileLocation) 
			throws PathFinderException{
		return new PathFinderRns(File.class, xsdFileLocation);
	}
	
	private <T> PathFinderRns(Class<T> schemaForm, String xsdFileLocation) 
			throws PathFinderException
	{
		if(xsdFileLocation != null)
			this.xsdFileLocation = xsdFileLocation;
		
		try{
			initWebClasses(schemaForm);
		}catch(SAXException e){
			logger.log(Level.SEVERE,"SAXEXCEPTION.");
			throw new PathFinderException(e);
		} catch (JAXBException e) {
			logger.log(Level.SEVERE,"JAXBEXCEPTION.");
			throw new PathFinderException(e);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"IOEXCEPTION.");
			throw new PathFinderException(e);
		} 
		
		placeNodeResponse = new HashMap<String,PostNodeResponse>();
		connectionRelationshipResponse = 
				new HashMap<PairType<String,String>,PostRelationshipResponse>();
		logger.log(Level.INFO, "PathFinder initialized successfully!!!");
	}
	
	private <T> void initWebClasses(Class<T> schemaForm)  
		throws IOException, SAXException, JAXBException
	{
		logger = Logger.getLogger(this.getClass().getName());
		InputStream schemaStream = null;

		try{
			// Build the JAX-RS client object and get the target to the base URI
			client = ClientBuilder.newClient();
			baseTarget = client.target(getBaseURI());
			
			// Create validator that uses the rns4neo4j schema 
		    SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		    
		    Schema schema;
		    if(schemaForm.equals(InputStream.class)){
				schemaStream = this.getClass().
						getResourceAsStream(xsdResourceLocation);
				if (schemaStream == null) {
					logger.log(Level.SEVERE, "xml schema file Not found.");
					throw new IOException();
				}
				schema = sf.newSchema(new StreamSource(schemaStream));
			}else if(schemaForm.equals(File.class)){
				schema = sf.newSchema(new File(xsdFileLocation));
			}else{
				throw new RuntimeException("schema form not valid");
			}
		    
		    validator = schema.newValidator();
		    // validator.setErrorHandler(new MyErrorHandler());
		    	
			// Create JAXB context related to the classes generated from the rns4neo4j schema
		    jc = JAXBContext.newInstance(jaxbPackage);		
		} finally {
			if (schemaStream!=null)
				schemaStream.close();
		}
	}
	
	private static URI getBaseURI() {
		String uri = System.getProperty("it.polito.dp2.RNS.lab3.Neo4JURL");
		if(uri == null){
			uri = "http://localhost:7474/db";
		}
	    return UriBuilder.fromUri(uri).build();
	}
	
	@Override
	public boolean isModelLoaded() {
		return modelLoaded;
	}

////////////////////////////////////////////////////////////////////////////
	
	/**
	 * It validates any kind of JAXB-Class element against the schema
	 * defined in the JAXBContext
	 */
	private <T> void validateElement(T element) throws ServiceException{
		//System.out.println("> Validating " + element.toString());
		try {
	    	JAXBSource jaxbsource = new JAXBSource(jc, element);
	    	validator.validate(jaxbsource);
	    	// System.out.println("+ Validation OK");
		} catch (org.xml.sax.SAXException se) {
			System.err.println("- Validation Failed. Details:");
		    Throwable t = se;
		    while (t != null) {
			    String message = t.getMessage();
			    if (message != null)
			  	  System.err.println(message);
			    t = t.getCause();
		    }
		    throw new ServiceException("Validation Failed", se);
		} catch (IOException e) {
			throw new ServiceException("Unexpected I/O Exception", e);
		} catch (JAXBException e) {
			throw new ServiceException("Unexpected JAXB Esception", e);
		}
	}

	/**
	 * Validate paths received from neo4j service
	 */
	private void validateNeo4jPaths(List<Path> neo4jShortestPaths) 
			throws ServiceException{
		// System.out.println(">> Validating paths");

		for(Path path: neo4jShortestPaths){
	    	// System.out.println("> Validating path ("+
	    	// 		path.getStart() + ", " + path.getEnd() + ")" );
	    	validateElement(path);
		}
	    System.out.println("+ Validated Paths: " + neo4jShortestPaths.size());
	}
	
////////////////////////////////////////////////////////////////////////////
	
	/**
	 * It sends a GET request to the connectionTarget and we pass 
	 * the type of the postResponse with the second parameter
	 */
	private <U> U getRequest(WebTarget connectionTarget, Class<U> c) throws ServiceException{
		Response response = baseTarget.path("data")
				   .request()
				   .accept(MediaType.APPLICATION_JSON)
				   .get();
		
		if (response.getStatus() != 200) {
			String errorMsg = "Error in remote operation: " +
					response.getStatus() + " " + response.getStatusInfo();
			System.err.println(errorMsg);
			throw new ServiceException(errorMsg);
		}				
		
		U bodyResponse = response.readEntity(c);
		
		validateElement(bodyResponse);
		
		return bodyResponse;
	}
	
	/**
	 * It sends a post request to the connectionTarget with a body of type T
	 * (postRequest) and we pass the type of the postResponse with the third parameter
	 */
	private <T,U> U postRequest(WebTarget target, T postRequest, Class<U> c) 
			throws ServiceException
	{
		Response response = target
				   .request(MediaType.APPLICATION_JSON_TYPE)
				   .accept(MediaType.APPLICATION_JSON)
				   .post(Entity.json(postRequest));
		
		if (response.getStatus() != 201) {
			String errorMsg = "Error in remote operation: " +
				response.getStatus() + " " + response.getStatusInfo();
			System.out.println(errorMsg);
			throw new ServiceException(errorMsg);
		}
		
		U bodyResponse = response.readEntity(c);
		
		validateElement(bodyResponse);
		
		return bodyResponse;
	}
	
	/**
	 * It sends a post request to the pathsTarget with 
	 * a body of type ShortestPathRequest (postRequest) 
	 */
	private List<Path> postRequest(WebTarget target, 
		ShortestPathRequest shortestPathRequest)
			throws ServiceException
	{
		// System.out.println("REQUEST SENT TO " +
		// pathsTarget.getUri().toString() + ".");	
		
		Response response = target
				   .request(MediaType.APPLICATION_JSON_TYPE)
				   .accept(MediaType.APPLICATION_JSON)
				   .post(Entity.json(shortestPathRequest));
		
		if (response.getStatus() != 200) {
			String errorMsg = 
					"Error in remote operation: " + response.getStatus() +
					" " + response.getStatusInfo();
			System.err.println(errorMsg);
			throw new ServiceException(errorMsg);
		}
		
		// use bufferEntity() if we must call response.readEntity more than once
		response.bufferEntity();

//		String strResponse = response.readEntity(String.class);
//		System.out.println("OK Response received: " + strResponse);	
		
		List<Path> paths = response.readEntity(
				new GenericType<List<Path>>() {});
		
		validateNeo4jPaths(paths);
		
		return paths;
	}
	
	/**
	 * It sends a delete request to the connectionTarget
	 */
	private void deleteRequest(WebTarget target) throws ServiceException{
		Response response = target
				   .request()
				   .delete();
		
		if (response.getStatus() != 204) {
			String errorMsg = "Error in remote operation: "+
					response.getStatus() + " " + response.getStatusInfo();
			System.err.println(errorMsg);
			throw new ServiceException(errorMsg);
		}
	}
	
////////////////////////////////////////////////////////////////////////////
	
	/**
	 * [21.4] GET SERVICE ROOT
	 * The service root is your starting point to discover the REST API.
	 * It contains the basic starting points for the database, 
	 * and some version and extension information.
	 * 
	 * Example request
	 *	• GET http://localhost:7474/db/data/
	 *	• Accept: application/json; charset=UTF-8
	 *	Example response
	 *	• 200: OK
	 *	• Content-Type: application/json; charset=UTF-8
	 */
	private String getNodeFromServiceRoot() throws ServiceException{

		if(serviceRootResponse==null){
			System.out.println("+++++ Getting the Service Root from NEO4J +++++");
			
			serviceRootResponse = 
				getRequest(baseTarget.path("data"), 
						    ServiceRootResponse.class);
		}
		
		String location = serviceRootResponse.getNode();
		
		if(location == null){
			throw new ServiceException("Cannot read node location from service root.");
		}else{
			// System.out.println("node: " + location);
			return location;
		}
	}
	
	/**
	 * [21.8] NODES
	 * Example request
	 *	• POST http://localhost:7474/db/data/node
	 *	• Accept: application/json; charset=UTF-8
	 *	• Content-Type: application/json
	 *	Example response
	 *	• 201: Created
	 *	• Content-Type: application/json; charset=UTF-8
	 *	• Location: http://localhost:7474/db/data/node/4
	 */
	private void postNodes(Set<PlaceReader> places) throws ServiceException
	{
		
		String nodesLocation = getNodeFromServiceRoot();
		WebTarget nodesTarget = client.target(nodesLocation);
		
		System.out.println("+++++ POST nodes into NEO4J (" + 
		nodesLocation + ") +++++");
		for(PlaceReader pr: places){
			String nodeId = pr.getId();
			
			System.out.println(">> Posting node " + nodeId);
			
			PostNodeRequest postNodeRequest = 
					(new ObjectFactory()).createPostNodeRequest();
			postNodeRequest.setId(nodeId);
			
			PostNodeResponse postNodeResponse =
					postRequest(nodesTarget, postNodeRequest, PostNodeResponse.class);
			
			placeNodeResponse.put(nodeId, postNodeResponse);
		}
	}
	
	
	/**
	 * [21.9] RELATIONSHIPS
	 * Example request
		• POST http://localhost:7474/db/data/node/1/relationships
		• Accept: application/json; charset=UTF-8
		• Content-Type: application/json
		{
			"to" : "http://localhost:7474/db/data/node/0",
			"type" : "LOVES"
		}
		Example response
		• 201: Created
		• Content-Type: application/json; charset=UTF-8
		• Location: http://localhost:7474/db/data/relationship/1
	 */
	private void postRelationships(
			Set<ConnectionReader> connections) throws ServiceException
	{	
		System.out.println("+++++ POST connections into NEO4J +++++");
		
		for(ConnectionReader pr: connections){
			String fromId = pr.getFrom().getId();
		    String toId = pr.getTo().getId();
		    
			System.out.println(">> Posting connection (" + 
								fromId + ", " + toId + ")");
			
			PostNodeResponse fromNode = placeNodeResponse.get(fromId);
			String toLocation = placeNodeResponse.get(toId).getSelf();
			PairType<String,String> connectionId = new PairType<String,String>(fromId, toId);
			
			WebTarget relationshipTarget = client.target(fromNode.getCreateRelationship());
			
			PostRelationshipRequest postRelationshipRequest = 
					(new ObjectFactory()).createPostRelationshipRequest();
			postRelationshipRequest.setTo(toLocation);
			postRelationshipRequest.setType("ConnectedTo");
			
			PostRelationshipResponse postRelationshipResponse =
					postRequest(relationshipTarget,
								postRelationshipRequest,
								PostRelationshipResponse.class);
			
			connectionRelationshipResponse.put(connectionId, postRelationshipResponse);
		}
	}
	
	/**
	 * Clear the connections from the db
	 */
	private void deleteRelationships() throws ServiceException{
		
		if(connectionRelationshipResponse.isEmpty()){
			return;
		}
		
		System.out.println("+++++ DELETING connections from NEO4J +++++");
		
		Set<PairType<String,String>> connectionsToRemove = 
				new HashSet<PairType<String,String>>(connectionRelationshipResponse.keySet());
		for (PairType<String,String> connectionId : connectionsToRemove) {
		    
		    String fromId = connectionId.getFirst();
		    String toId = connectionId.getSecond();
		    String connectionLoc = connectionRelationshipResponse.get(connectionId).getSelf();						

		    System.out.println("Deleting connection " + connectionLoc + "(" + 
					fromId + ", " + toId + ")");
			
			WebTarget relationshipTarget = client.target(connectionLoc);
			deleteRequest(relationshipTarget);
		
			connectionRelationshipResponse.remove(connectionId);
		}
	}
	
	/**
	 * Clear the nodes from the db
	 */
	private void deleteNodes() throws ServiceException{
		
		if(placeNodeResponse.isEmpty()){
			return;
		}
		
		System.out.println("+++++ DELETING nodes from NEO4J +++++");
		
		Set<String> placesToRemove = new HashSet<String>(placeNodeResponse.keySet());
		for (String placeId : placesToRemove) {
		    String placeLoc = placeNodeResponse.get(placeId).getSelf();
		    
		    System.out.println("Deleting place " + placeId + ": " + placeLoc);
			
			WebTarget nodeTarget = client.target(placeLoc);
			deleteRequest(nodeTarget);
			
			placeNodeResponse.remove(placeId);
		}
	}
	
	
	public void reloadModel(RnsReader rnsReader) 
			throws ServiceException, ModelException 
	{
		try{
			System.out.println("++++++++++ RELOADING MODEL... ++++++++++");
			///////// GET THE PLACES AND CONNECTIONS FROM RANDOM RNS READER
			Set<PlaceReader> places = rnsReader.getPlaces(null);
			Set<ConnectionReader> connections = rnsReader.getConnections();
			
			if(places.isEmpty() && connections.isEmpty() ){
				String msg = "Places and connections are empty";
				System.err.println(msg);
				throw new ModelException(msg);
			}
			
			///////// CLEAR NEO4J DB
			this.deleteRelationships();
			this.deleteNodes();
			
			///////// COPY PLACES AND CONNECTIONS IN NEO4J DB
			this.postNodes(places);
			this.postRelationships(connections);
			
			///////// MODEL (RE)LOADED
			this.modelLoaded = true;	
			
		}catch(ServiceException e){
			this.modelLoaded = false;
			throw e;
		}
	}
	
	/**
	 * Read the information about a set of places and about
	 * their connections, from the random generator already
	 * used in Assignment 1 and load the graph of these places
	 * with their connections into NEO4J by means of the Neo4J
	 * REST API as specified below
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * HOW TO INTERACT WITH NEO4J
	 * A graph node has to be created for each place,
	 * with a property named “id” with the value that is the place id;
	 * a relationship has to be created for each connection, connecting
	 * the nodes of the corresponding places, with type “ConnectedTo”.
	 * [21.4]: 	Use the endpoint with the service root
	 * [21.8]: 	Create and read nodes
	 * [21.9]: 	Create and read relationships
	 * [21.18]: Use Shortest Path Algorithm in NEO4J
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	@Override
	public void reloadModel() throws ServiceException, ModelException {
		try {
			this.reloadModel(RnsReaderFactory.newInstance().newRnsReader());
		} catch (RnsReaderException | FactoryConfigurationError e) {
			throw new ModelException(e);
		}
	}
	
////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Converts the shortest paths from neo4j form 
	 * to the contract interface form
	 */
	private Set<List<String>> convertShortestPaths(List<Path> neo4jShortestPaths){
		Set<List<String>> stringShortestPaths = new HashSet<List<String>>();
		
		// System.out.println("> Converting list of Shortest Paths...");
		for(Path path: neo4jShortestPaths){
			List<String> spList = new ArrayList<String>();
			for(String node: path.getNodes()){
				// FOR EACH NODE IN THE PATH FROM NEO4J ADD ITS CORRESPONDING PLACE ID
				// REVERSE SEARCH
				for(Map.Entry<String,PostNodeResponse> place: placeNodeResponse.entrySet()){
					//System.out.println(place.getValue() + " == " + node + "?");
					if(place.getValue().getSelf().equals(node)){
						spList.add(place.getKey());
						break;
					}
				}
			}
			stringShortestPaths.add(spList);
		}
		
		return stringShortestPaths;
	}
	
	/**
	 * Print paths in the form returned from the contract interface
	 */
	private void printPaths(Set<List<String>> shortestPathsOfPlaceId){
		// System.out.println("Shortest pathS made of placeIds: ");
		for(List<String> path: shortestPathsOfPlaceId){
			System.out.print("Path ("+
	    			path.get(0) + ", " + path.get(path.size()-1) + "): [" );
			for(String place: path){
				if(place.equals(path.get(0))){
					System.out.print(place);
				}else{
					System.out.print(", " + place );
				}
			}
			System.out.println("]");
		}
	}
	
	/**
	 * Answer queries about shortest paths between places,
	 * by properly getting this information from the 
	 * REST API as specified below
	 * 
	 * [21.18]: Use Shortest Path Algorithm in NEO4J
	 * Example request
	 *	• POST http://localhost:7474/db/data/node/26/paths
	 *	• Accept: application/json; charset=UTF-8
	 *	• Content-Type: application/json
	 *	REST API
	 *	402
	 *	{
	 *		"to" : "http://localhost:7474/db/data/node/21",
	 *		"max_depth" : 3,
	 *		"relationships" : {
	 *			"type" : "to",
	 *			"direction" : "out"
	 *		},
	 *		"algorithm" : "shortestPath"
	 *	}
	 *	Example response
	 *	• 200: OK
	 *	• Content-Type: application/json; charset=UTF-8
	 *	[ {
	 *		"directions" : [ "->", "->" ],
	 *		"start" : "http://localhost:7474/db/data/node/26",
	 *		"nodes" : [ "http://localhost:7474/db/data/node/26", 
	 *			"http://localhost:7474/db/data/node/25", 
	 *			"http://localhost:7474/db/data/node/21" ],
	 *		"length" : 2,
	 *		"relationships" : [ "http://localhost:7474/db/data/relationship/27",
	 *			 "http://localhost:7474/db/data/relationship/36" ],
	 *		"end": "http://localhost:7474/db/data/node/21"
	 *	} ]
	 */
	@Override
	public Set<List<String>> findShortestPaths
			(String source, String destination, int maxlength)
			throws UnknownIdException, BadStateException, ServiceException 
	{	
		if(!isModelLoaded()){
			throw new BadStateException("Model NOT loaded");
		}
		
		if(!placeNodeResponse.containsKey(source) ||
		   !placeNodeResponse.containsKey(destination)){
			throw new UnknownIdException("Source or destination not valid.");
		}
	    
		System.out.println("+++++ FINDING SHORTEST PATHS FROM " + 
							source + " TO " + destination + " +++++");
		
		String fromLocation = placeNodeResponse.get(source).getSelf();
		String toLocation = placeNodeResponse.get(destination).getSelf();
		
		// SERVER DOES NOT GIVE THE PATH FOR REQUESTING SHORTEST PATHS
		// WE CANNOT FOLLOW HATEOAS HERE
		WebTarget pathsTarget = client.target(fromLocation).path("paths");
		
		if (maxlength <= 0){
			maxlength=Integer.MAX_VALUE;
		}
		ShortestPathRequest shortestPathRequest = 
				(new ObjectFactory()).createShortestPathRequest();
		shortestPathRequest.setTo(toLocation);
		shortestPathRequest.setMaxDepth(BigInteger.valueOf(maxlength));
		shortestPathRequest.setAlgorithm("shortestPath");
		Relationships rels = new Relationships();
		
		rels.setDirection("out");
		rels.setType("ConnectedTo");
		shortestPathRequest.setRelationships(rels);
		
		List<Path> neo4jShortestPaths = 
				this.postRequest(pathsTarget, shortestPathRequest);
		
		Set<List<String>> shortestPathsOfPlaceId = 
				this.convertShortestPaths(neo4jShortestPaths);
		this.printPaths(shortestPathsOfPlaceId);
		
		return shortestPathsOfPlaceId;
	}
	
//	/**
//	 * Print paths in the form returned from the neo4j service
//	 */
//	@SuppressWarnings("unused")
//	private void printPaths(List<Path> neo4jShortestPaths){
//		// System.out.println("Shortest pathS from neo4j: ");
//		for(Path path: neo4jShortestPaths){
//			System.out.print("PATH ("+path.getStart() +
//					", " + path.getEnd()+") ");
//			System.out.println("Length: "+path.getLength());
//			
//			System.out.println("NODES: [");	
//			for(String node: path.getNodes()){
//				System.out.println(node);
//			}
//			System.out.println("]");
//		}
//	}
	
//	private void printPlaces(){
//		System.out.println("LIST OF PLACES:");
//		for (String placeId : placeNodeResponse.keySet()) {
//			System.out.println("place " + placeId + ": " + 
//				placeNodeResponse.get(placeId));
//		}
//	}
//	
//	private void printConnections(){
//		System.out.println("LIST OF CONNECTIONS:");
//		for (Pair<String,String> connectionId : connectionRelationshipResponse.keySet()) {
//			System.out.println("connection (" + connectionId.getKey() +
//					", " + connectionId.getValue() + ": " +
//					connectionRelationshipResponse.get(connectionId));
//		}
//	}
}