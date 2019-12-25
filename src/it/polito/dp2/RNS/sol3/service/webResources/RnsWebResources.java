package it.polito.dp2.RNS.sol3.service.webResources;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.DatatypeFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.RNS.sol3.jaxb.AddVehicleResponse;
import it.polito.dp2.RNS.sol3.jaxb.ChangeStateRequest;
import it.polito.dp2.RNS.sol3.jaxb.Connection;
import it.polito.dp2.RNS.sol3.jaxb.ConnectionsResponse;
import it.polito.dp2.RNS.sol3.jaxb.ErrorBody;
import it.polito.dp2.RNS.sol3.jaxb.GateType;
import it.polito.dp2.RNS.sol3.jaxb.MoveRequest;
import it.polito.dp2.RNS.sol3.jaxb.NewVehicle;
import it.polito.dp2.RNS.sol3.jaxb.ObjectFactory;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.PlacesIdPath;
import it.polito.dp2.RNS.sol3.jaxb.PlacesResponse;
import it.polito.dp2.RNS.sol3.jaxb.RnsErrorType;
import it.polito.dp2.RNS.sol3.jaxb.Road;
import it.polito.dp2.RNS.sol3.jaxb.RoadsResponse;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;
import it.polito.dp2.RNS.sol3.jaxb.VehicleState;
import it.polito.dp2.RNS.sol3.jaxb.VehicleType;
import it.polito.dp2.RNS.sol3.jaxb.VehiclesResponse;
import it.polito.dp2.RNS.sol3.service.RnsService;
import it.polito.dp2.RNS.sol3.service.exceptions.EntranceRefusedRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.NotFoundRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.PlaceNotFoundRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.RnsServiceException;
import it.polito.dp2.RNS.sol3.service.exceptions.RoadNotFoundRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.SyntaxRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.UnauthorizedRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.VehicleNotFoundRnsException;
import it.polito.dp2.RNS.sol3.service.exceptions.WrongPlaceRnsException;

/** Resource class hosted at the URI relative path "/rns"
 */
@Path("/rns")
@Api(value = "/rns", description = "Collection of the resource of RNS Service")
public class RnsWebResources {
	
	private final UriInfo uriInfo;	
    private final RnsService service;
    
    private final Logger logger;
    
    public RnsWebResources(@Context UriInfo uriInfo) throws RnsServiceException{
    	logger = Logger.getLogger(RnsWebResources.class.getName());
    	// logger.info(">>> INITIALIZING RnsRestResource");
    	this.uriInfo = uriInfo;
    	
    	try{
    		service = new RnsService(this.uriInfo.getBaseUriBuilder());
    	}catch(RnsServiceException e){
    		logger.info("RNS SERVICE EXCEPTION CAUGHT");
    		throw e;
    	}
    	// logger.info("RNS REST INITIALIZED");
    }
    
    /*
     * Exception					Code Description
     * BadRequestException			400	 Malformed message
     * NotAuthorizedException		401	 Authentication failure
     * ForbiddenException			403	 Not permitted to access
     * NotFoundException			404	 Couldn’t find resource
     * NotAllowedException			405	 HTTP method not supported
     * NotAcceptableException		406	 Client media type requested not supported
     * NotSupportedException		415	 Client posted media type not supported
     * InternalServerErrorException	500	 General server error
     */
    
    private Response errorResponse(RnsErrorType e){
    	ErrorBody errorBody = (new ObjectFactory()).createErrorBody();
		errorBody.setError(e);
		return Response.status(Response.Status.CONFLICT).
				entity(errorBody).build();
    }
    
    private Response checkLastModified(Request request, 
    		Date lastModifiedDate, Object res)
    {    	
    	// evaluate the if-modified-since header of the request
    	Response.ResponseBuilder evalResBuild = 
    			request.evaluatePreconditions(lastModifiedDate);
    	
    	 if (evalResBuild == null) {
    		 //last modified date didn't match    		 
    		 evalResBuild = Response.ok(res);
    	 }else{
    		 logger.info("RNS ROOT not modified (304)"); 
    	 }
    	 
    	 return evalResBuild.lastModified(lastModifiedDate).build();
    }
///////////////////////////////////////////////////////////////////////////////

    /**
     * For this one we implement HTTP Conditional Requests
     * because the client could not have implemented an internal 
     * caching method for saving these informations
     */
    @GET 
    @ApiOperation(value = "Get the RNS Service Root",
    			  notes = "Get the RNS Service root. If an admin is authenticated,"
    			  		+ " it gives the uri of all the resources available,"
    			  		+ " otherwise it gives just the uri of the vehicles.")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response getRns(@QueryParam("authToken") String authToken,
    		@Context Request request) 
    {
    	logger.info(">> GETTING RNS ROOT WITH TOKEN " + authToken); 
    	// a random old date because the rnsRoot will never change
    	GregorianCalendar lastModifiedCal = new GregorianCalendar();
    	lastModifiedCal.set(1944, 6, 6, 0, 0, 0);
    	Date lastModifiedDate = lastModifiedCal.getTime();
    	
    	return checkLastModified(request, 
    			 lastModifiedDate, service.getRnsRoot(authToken));
    }

    
///////////////////////////////////////////////////////////////////////////////
    @POST
    @Path("/vehicles")
    @ApiOperation(value = "Vehicle requests to enter into the RNS",
    notes = "A vehicle that wants to enter the system has to request"
  		+ " permission to the system. In the permission request"
  		+ " the vehicle must specify the entrance (IN or INOUT)"
  		+ " gate where it is, and the destination place it wants"
  		+ " to go to. The system may grant permission or not."
  		+ " If the destination is not reachable, the system does"
  		+ " not grant permission, but there may be other reasons"
  		+ " (e.g. congestion or other system policies)"
  		+ " for not granting permission. If the permission is"
  		+ " granted, the system computes a suggested path to the"
  		+ " destination, adds the vehicle to the set of vehicles"
  		+ " that are tracked in the system, with its current"
  		+ " position set at the entrance gate and its initial"
  		+ " state set to IN_TRANSIT, and communicates the suggested"
  		+ " path to the vehicle. If instead the permission is not"
  		+ " granted, the system communicates the reason for not"
  		+ " granting the access to the vehicle, and the vehicle"
  		+ " is not added to the set of vehicles that are tracked"
  		+ " in the system.")
    @ApiResponses(value = {
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 400, message = "Invalid body"),
		@ApiResponse(code = 403, message = 
			"Entrance refused (Congestion OR Vehicle already exists OR"
			+ " other system policies)"),
		@ApiResponse(code = 409, message = "Unknown origin/destination"
			+ " OR Wrong input Gate"),
		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response addVehicle(NewVehicle newVehicle,
    		@QueryParam("authToken") String authToken) {
    	// authToken parameter not used but may be useful in future
    	if(newVehicle == null || 
    	   newVehicle.getId() == null ||
           newVehicle.getDestinationId() == null ||
           newVehicle.getOriginId() == null ||
           newVehicle.getType() == null){
    		throw new BadRequestException("Invalid message");
    	}

    	logger.info(">> ADDING VEHICLE " + newVehicle.getId()); 
    	
    	try {
			AddVehicleResponse vehRes = service.addVehicle(newVehicle, authToken);
			return Response.created(URI.create(vehRes.getVehicleUri())).
					entity(vehRes).build();
		} catch (EntranceRefusedRnsException e) {
			throw new ForbiddenException();
		} catch (PlaceNotFoundRnsException e) {
			return this.errorResponse(RnsErrorType.UNKNOWN_PLACE);
		} catch (WrongPlaceRnsException e) {
			return this.errorResponse(RnsErrorType.WRONG_PLACE);
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}
    }
    
	@DELETE
	@Path("/vehicles/{id}")
	@ApiOperation(value = "Remove a vehicle", 
		notes = "IN ORDER TO DISTINGUISH ADMINISITRATOR AND VEHICLE OPERATION,"
				+ " the administrator must insert a query parameter 'authToken’,"
				+ " while the vehicle must insert the authToken received in the"
				+ " POST operation. 4. When a tracked vehicle is in an OUT or"
				+ " INOUT gate, it can decide to exit from the system through"
				+ " that gate. When doing so, it contacts the system and"
				+ " communicates to the system that it has left the system."
				+ " Upon receiving this information, the system removes the"
				+ " vehicle from the set of vehicles that are tracked in the"
				+ " system and forgets about it. 7. At any time, an administrator"
				+ " can request to remove a vehicle from the set of vehicles that"
				+ " are tracked in the system and the request is always accepted"
				+ " by the system.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Operation completed succesfully"),
		@ApiResponse(code = 401, message = "Unauthorized"),
	  	@ApiResponse(code = 404, message = "Not Found"),
	  	@ApiResponse(code = 409, message = "Unknown OR Wrong outGate"),
	 	@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response removeVehicle(
		@PathParam("id") String vehicleId, @QueryParam("outGateId") String outGateId,
		@QueryParam("authToken") String authToken )
	{
		logger.info(">> REMOVING VEHICLE " + vehicleId);
		if(outGateId != null){
			logger.info("FROM " + outGateId);
		}
		
	  	try {
			service.removeVehicle(vehicleId, outGateId, authToken);
			return Response.noContent().build(); 
	  	} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (VehicleNotFoundRnsException e) {
			throw new NotFoundException();
		} catch (PlaceNotFoundRnsException e) {
			return this.errorResponse(RnsErrorType.UNKNOWN_PLACE);
		} catch (WrongPlaceRnsException e) {
			return this.errorResponse(RnsErrorType.WRONG_PLACE);
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}		
	}
    
	@PUT
	@Path("/vehicles/{id}/position")
	@ApiOperation(	value = "Vehicle requests to move into a new place",
		notes = "Whenever a tracked vehicle moves from a place to another one,"
				+ " it informs the system aboutthe new place where it is, and"
				+ " the system records the new current position of the vehicle."
				+ " If the new place is not on the path suggested by the system"
				+ " for that vehicle, the system first checks if the new place"
				+ " is reachable from the previous current position of the vehicle."
				+ " If so, it computes a new path from the new current position of"
				+ " the vehicle to the destination, and communicates this new path"
				+ " to the vehicle. If the path cannot be computed (e.g. because"
				+ " the destination is not reachable from the new current place),"
				+ " the vehicle remains without a suggested path. Finally, if the"
				+ " new place is not reachable from the previous current position"
				+ " of the vehicle, the request from the vehicle is considered"
				+ " wrong by the system, and nothing changes."
	)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Vehicle succesfully moved into"
				+ " a new position OUT of the suggested path."
				+ " Suggested path recalculated."),
		@ApiResponse(code = 204, message = "Vehicle succesfully moved into"
				+ " a new position into the current suggested path."),
		@ApiResponse(code = 400, message = "Invalid body"),
		@ApiResponse(code = 401, message = "Unauthorized"),
		@ApiResponse(code = 404, message = "Not found"),
		@ApiResponse(code = 409, message = "Unknown OR Wrong place"),
	  	@ApiResponse(code = 500, message = "Internal Server Error")})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response moveVehicle(
		@PathParam("id") String vehicleId, @QueryParam("authToken") String authToken,
		MoveRequest moveRequest) 
	{
		String newPositionId = moveRequest.getNewPositionId();
		logger.info(
				">> MOVING VEHICLE " + vehicleId + " TO " + newPositionId
		);
		
	  	try {
			List<String> newIdPath = 
					service.moveVehicle(vehicleId, newPositionId, authToken);
			if(newIdPath == null){
				return Response.noContent().build();
			}else{
				PlacesIdPath placesIdPath = 
						(new ObjectFactory()).createPlacesIdPath();
				placesIdPath.getPlaceId().addAll(newIdPath);
				return Response.ok(placesIdPath).build();
			}
	  	} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (VehicleNotFoundRnsException e) {
			throw new NotFoundException();
		} catch (PlaceNotFoundRnsException e) {
			return this.errorResponse(RnsErrorType.UNKNOWN_PLACE);
		} catch (WrongPlaceRnsException e) {
			return this.errorResponse(RnsErrorType.WRONG_PLACE);
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}	
	}
	
	//OK
	@PUT
	@Path("/vehicles/{id}/state")
	@ApiOperation(	value = "Vehicle requests to change its state",
		notes = "5. tracked vehicle can request the system to change its"
			+ " state at any time. The request is always accepted by"
			+ " the system. The new state set is the opposite of the old one."
	)
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Vehicle succesfully"
				+ " changed its state."),
		@ApiResponse(code = 400, message = "Invalid body"),
		@ApiResponse(code = 401, message = "Unauthorized"),
		@ApiResponse(code = 404, message = "Not found"),
	  	@ApiResponse(code = 500, message = "Internal Server Error")})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response changeVehicleState(
		@PathParam("id") String vehicleId, @QueryParam("authToken") String authToken,
		ChangeStateRequest changeStateRequest) 
	{
		VehicleState newState = changeStateRequest.getState();
		logger.info(">> CHANGING VEHICLE " + vehicleId + " STATE TO " + newState);
		
	  	try {
	  		service.changeVehicleState(vehicleId, newState, authToken);
	  		return Response.noContent().build();
	  	} catch (SyntaxRnsException e) {
			throw new BadRequestException();
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (VehicleNotFoundRnsException e) {
			throw new NotFoundException();
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}	
	}
///////////////////////////////////////////////////////////////////////////////
	
	//OK
	@GET 
	@Path("/vehicles")
    @ApiOperation(value = "Get all the vehicles that respects the constraints")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 400, message = "Bad query params"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response getVehicles(
    		@QueryParam("authToken") String authToken, 
    		@QueryParam("since") String since,
    		@QueryParam("types") List<String> typesReqStr,
    		@QueryParam("state") String stateReqStr,
    		@Context Request request) 
	{
		logger.info(">> GETTING VEHICLES WITH ");
		logger.info("SINCE " + since);		
		for(String type: typesReqStr){
			logger.info("TYPE " + type);
		}
		logger.info("STATE " + stateReqStr);
		
		//set calendar
		GregorianCalendar sinceCal = null;
		try{
			if(since != null && !since.isEmpty()){
				sinceCal = 
						DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(since)
						.toGregorianCalendar();
			}
		}catch(Exception e){
			throw new BadRequestException("Error during setting of calendar");
		}
		
		VehicleState stateReq = null;
		List<VehicleType> typesReq = new ArrayList<VehicleType>();
		try{
			if(stateReqStr != null){
				stateReq = VehicleState.valueOf(stateReqStr);
			}
			for(String typeStr: typesReqStr){
				typesReq.add(VehicleType.valueOf(typeStr));
			}
		}catch(Exception e){
			throw new BadRequestException("Bad query params");
		}
		
		try {
			// no need to synchronize
			Date lastModifiedDate = service.getVehiclesLastModifiedDate();
			
			VehiclesResponse res = (new ObjectFactory()).createVehiclesResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setVehicles((new ObjectFactory()).createVehiclesResponseVehicles());
			res.getVehicles().getVehicle().addAll(
					service.getVehicles(sinceCal, typesReq, stateReq, authToken)
			);
			res.setCount(BigInteger.valueOf(res.getVehicles().getVehicle().size()));
			
			return checkLastModified(request,lastModifiedDate,res);
			
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		}
    }
	
	//OK
	@GET 
	@Path("/vehicles/{id}")
    @ApiOperation(value = "Get the vehicle")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 404, message = "Not found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response getVehicle(@PathParam("id") String vehicleId, 
    		@QueryParam("authToken") String authToken, 
    		@Context Request request) 
	{
		logger.info(">> GETTING VEHICLE " + vehicleId);
		try {
			synchronized(service.getVehicleLock()){
				Vehicle v = service.getVehicle(vehicleId, authToken);
				Date lastModifiedDate = v.getLastModifiedDate()
						.toGregorianCalendar().getTime();
				
				return checkLastModified(request,lastModifiedDate,v);
			}
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (VehicleNotFoundRnsException e) {
			throw new NotFoundException();
		}
    }
	
	@GET 
	@Path("/vehicles/{id}/position")
    @ApiOperation(value = "Get the position of the vehicle {id}")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 404, message = "Not found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response getPositionOfVehicle(@PathParam("id") String vehicleId, 
    		@QueryParam("authToken") String authToken,
    		@Context Request request) 
	{
		logger.info(">> GETTING POSITION OF VEHICLE " + vehicleId);
		try {
			synchronized(service.getVehicleLock()){
				Vehicle v = service.getVehicle(vehicleId, authToken);
				Date lastModifiedDate = v.getLastModifiedDate()
						.toGregorianCalendar().getTime();
				
				return checkLastModified(request,lastModifiedDate,
						service.getPositionOfVehicle(vehicleId, authToken));
			}
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (VehicleNotFoundRnsException e) {
			throw new NotFoundException();
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}
    }
	
	@GET 
	@Path("/vehicles/{id}/origin")
    @ApiOperation(value = "Get the origin of the vehicle {id}")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 404, message = "Not found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response getOriginOfVehicle(@PathParam("id") String vehicleId, 
    		@QueryParam("authToken") String authToken,
    		@Context Request request) 
	{
		logger.info(">> GETTING ORIGIN OF VEHICLE " + vehicleId);
		try {
			synchronized(service.getVehicleLock()){
				Vehicle v = service.getVehicle(vehicleId, authToken);
				Date lastModifiedDate = v.getLastModifiedDate()
						.toGregorianCalendar().getTime();
				
				return checkLastModified(request,lastModifiedDate,
						service.getOriginOfVehicle(vehicleId, authToken));
			}
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (VehicleNotFoundRnsException e) {
			throw new NotFoundException();
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}
    }
	
	@GET 
	@Path("/vehicles/{id}/destination")
    @ApiOperation(value = "Get the destination of the vehicle {id}")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 404, message = "Not found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response getDestinationOfVehicle(@PathParam("id") String vehicleId, 
    		@QueryParam("authToken") String authToken,
    		@Context Request request) 
	{
		logger.info(">> GETTING DESTINATION OF VEHICLE " + vehicleId);
		try {
			synchronized(service.getVehicleLock()){
				Vehicle v = service.getVehicle(vehicleId, authToken);
				Date lastModifiedDate = v.getLastModifiedDate()
						.toGregorianCalendar().getTime();
				
				return checkLastModified(request,lastModifiedDate,
						service.getDestinationOfVehicle(vehicleId, authToken));
			}
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (VehicleNotFoundRnsException e) {
			throw new NotFoundException();
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}
    }
	
	@GET 
	@Path("/vehicles/{id}/suggestedPath")
    @ApiOperation(value = "Get the suggested path of the vehicle {id}")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 404, message = "Not found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response getSuggestedPathOfVehicle(@PathParam("id") String vehicleId, 
    		@QueryParam("authToken") String authToken,
    		@Context Request request) 
	{
		logger.info(">> GETTING SUGGESTED PATH OF VEHICLE " + vehicleId);
		try {
			synchronized(service.getVehicleLock()){
				Vehicle v = service.getVehicle(vehicleId, authToken);
				Date lastModifiedDate = v.getLastModifiedDate()
						.toGregorianCalendar().getTime();
				
				return checkLastModified(request,lastModifiedDate,
						service.getSuggestedPathOfVehicle(vehicleId, authToken));
			}
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (VehicleNotFoundRnsException e) {
			throw new NotFoundException();
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}
    }
	
///////////////////////////////////////////////////////////////////////////////	

	@GET 
	@Path("/roads")
    @ApiOperation(value = "Get all the roads")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public RoadsResponse getRoads(@QueryParam("authToken") String authToken) 
	{
		logger.info(">> GETTING ALL THE ROADS");
		try {			
			RoadsResponse res = (new ObjectFactory()).createRoadsResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setRoads((new ObjectFactory()).createRoadsResponseRoads());
			res.getRoads().getRoad().addAll(
					service.getRoads(authToken)
			);
			res.setCount(BigInteger.valueOf(res.getRoads().getRoad().size()));
			return res;
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		}
    }
	
	@GET 
	@Path("/roads/{roadName}")
    @ApiOperation(value = "Get the {roadName} road")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 404, message = "Not found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Road getRoad(@PathParam("roadName") String roadName, 
    		@QueryParam("authToken") String authToken) 
	{
		logger.info(">> GETTING ROAD " + roadName);
		try {
			return service.getRoad(roadName, authToken);
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (RoadNotFoundRnsException e) {
			throw new NotFoundException();
		}
    }
///////////////////////////////////////////////////////////////////////////////		
	@GET 
	@Path("/places")
    @ApiOperation(value = "Get all the places with prefix idPrefix")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public PlacesResponse getPlaces(@QueryParam("authToken") String authToken,
    		@QueryParam("idPrefix") String idPrefix) 
	{
		logger.info(">> GETTING ALL THE PLACES WITH PREFIX " + idPrefix);
		try {			
			PlacesResponse res = (new ObjectFactory()).createPlacesResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setPlaces((new ObjectFactory()).createPlacesResponsePlaces());
			res.getPlaces().getPlace().addAll(
					service.getPlaces(idPrefix, authToken)
			);
			res.setCount(BigInteger.valueOf(res.getPlaces().getPlace().size()));
			return res;
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		}
    }
	
	@GET 
	@Path("/places/roadSegments")
    @ApiOperation(value = "Get all the road segments [with roadName]")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public PlacesResponse getRoadSegments(@QueryParam("authToken") String authToken,
    		@QueryParam("roadName") String roadName) 
	{
		logger.info(">> GETTING ALL THE ROAD SEGMENTS WITH ROADNAME " + roadName);
		try {			
			PlacesResponse res = (new ObjectFactory()).createPlacesResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setPlaces((new ObjectFactory()).createPlacesResponsePlaces());
			res.getPlaces().getPlace().addAll(
					service.getRoadSegments(roadName, authToken)
			);
			res.setCount(BigInteger.valueOf(res.getPlaces().getPlace().size()));
			return res;
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		}
    }
	
	@GET 
	@Path("/places/parkingAreas")
    @ApiOperation(value = "Get all the parking areas [with the services]")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public PlacesResponse getParkingAreas(@QueryParam("authToken") String authToken,
    		@QueryParam("services") List<String> servicesReq) 
	{
		logger.info(">> GETTING ALL THE PARKING AREAS WITH SERVICES:");
		if(servicesReq != null){
			for(String service: servicesReq){
				logger.info("+ " + service);
			}
		}
		
		try {			
			PlacesResponse res = (new ObjectFactory()).createPlacesResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setPlaces((new ObjectFactory()).createPlacesResponsePlaces());
			res.getPlaces().getPlace().addAll(
					service.getParkingAreas(servicesReq, authToken)
			);
			res.setCount(BigInteger.valueOf(res.getPlaces().getPlace().size()));
			return res;
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		}
    }
	
	@GET 
	@Path("/places/gates")
    @ApiOperation(value = "Get all the gates [of type]")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public PlacesResponse getGates(@QueryParam("authToken") String authToken,
    		@QueryParam("type") GateType typeReq) 
	{
		logger.info(">> GETTING ALL THE GATES WITH TYPE  " + typeReq);
		
		try {			
			PlacesResponse res = (new ObjectFactory()).createPlacesResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setPlaces((new ObjectFactory()).createPlacesResponsePlaces());
			res.getPlaces().getPlace().addAll(
					service.getGates(typeReq, authToken)
			);
			res.setCount(BigInteger.valueOf(res.getPlaces().getPlace().size()));
			return res;
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		}
    }
	
	@GET 
	@Path("/places/{id}")
    @ApiOperation(value = "Get the {id} place")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 404, message = "Not found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Place getPlace(@PathParam("id") String placeId, 
    		@QueryParam("authToken") String authToken) 
	{
		logger.info(">> GETTING PLACE " + placeId);
		try {
			return service.getPlace(placeId, authToken);
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (PlaceNotFoundRnsException e) {
			throw new NotFoundException();
		}
    }
	
	@GET 
	@Path("/places/{id}/nextPlaces")
    @ApiOperation(value = "Get all the NEXT places from the place {id}")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 404, message = "Place Not found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public PlacesResponse getNextPlaces(@PathParam("id") String placeId, 
    		@QueryParam("authToken") String authToken) 
	{
		logger.info(">> GETTING ALL THE PLACES NEXT TO " + placeId);
		try {			
			PlacesResponse res = (new ObjectFactory()).createPlacesResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setPlaces((new ObjectFactory()).createPlacesResponsePlaces());
			res.getPlaces().getPlace().addAll(
					service.getNextPlaces(placeId, authToken)
			);
			res.setCount(BigInteger.valueOf(res.getPlaces().getPlace().size()));
			return res;
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (PlaceNotFoundRnsException e) {
			throw new NotFoundException();
		}
    }
	
	@GET 
	@Path("/places/{id}/vehicles")
    @ApiOperation(value = "Get all the vehicles in the place {id}"
    		+ "that respect the constraints")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 400, message = "Bad query params"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response getVehiclesInPlace(
    		@PathParam("id") String placeId,
    		@QueryParam("authToken") String authToken, 
    		@QueryParam("since") String since,
    		@QueryParam("types") List<String> typesReqStr,
    		@QueryParam("state") String stateReqStr,
    		@Context Request request)
	{
		logger.info(">> GETTING VEHICLES IN PLACE "+ placeId +" WITH ");
		logger.info("SINCE " + since);		
		for(String type: typesReqStr){
			logger.info("TYPE " + type);
		}
		logger.info("STATE " + stateReqStr);
		
		//set calendar
		GregorianCalendar sinceCal = null;
		try{
			if(since != null && !since.isEmpty()){
				sinceCal = 
						DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(since)
						.toGregorianCalendar();
			}
		}catch(Exception e){
			throw new BadRequestException("Error during setting of calendar");
		}
		
		VehicleState stateReq = null;
		List<VehicleType> typesReq = new ArrayList<VehicleType>();
		try{
			if(stateReqStr != null){
				stateReq = VehicleState.valueOf(stateReqStr);
			}
			for(String typeStr: typesReqStr){
				typesReq.add(VehicleType.valueOf(typeStr));
			}
		}catch(Exception e){
			throw new BadRequestException("Bad query params");
		}
		
		try {
			// no need to synchronize
			Date lastModifiedDate = service.getVehiclesLastModifiedDate();
			
			VehiclesResponse res = (new ObjectFactory()).createVehiclesResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setVehicles((new ObjectFactory()).createVehiclesResponseVehicles());
			res.getVehicles().getVehicle().addAll(
					service.getVehiclesInPlace(placeId, sinceCal,
							typesReq, stateReq, authToken)
			);
			res.setCount(BigInteger.valueOf(res.getVehicles().getVehicle().size()));
			
			return checkLastModified(request,lastModifiedDate,res);
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (PlaceNotFoundRnsException e) {
			throw new NotFoundException();
		} catch (RnsServiceException e) {
			throw new InternalServerErrorException();
		}
    }
	
///////////////////////////////////////////////////////////////////////////////		
	@GET 
	@Path("/connections")
    @ApiOperation(value = "Get all the connections")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public ConnectionsResponse getConnections(
    		@QueryParam("authToken") String authToken) 
	{
		logger.info(">> GETTING ALL THE CONNECTIONS");
		try {			
			ConnectionsResponse res = (new ObjectFactory()).createConnectionsResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setConnections((new ObjectFactory()).createConnectionsResponseConnections());
			res.getConnections().getConnection().addAll(
					service.getConnections(authToken)
			);
			res.setCount(BigInteger.valueOf(res.getConnections().getConnection().size()));
			return res;
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		}
    }
	
	@GET 
	@Path("/connections/{from}")
    @ApiOperation(value = "Get all the connections from {from}")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public ConnectionsResponse getConnectionsFrom(
    		@PathParam("from") String fromId,
    		@QueryParam("authToken") String authToken) 
	{
		logger.info(">> GETTING ALL THE CONNECTIONS FROM " + fromId);
		try {			
			ConnectionsResponse res = (new ObjectFactory()).createConnectionsResponse();
			res.setTotalPages(BigInteger.valueOf(1));
			res.setPage(BigInteger.valueOf(1));
			res.setConnections((new ObjectFactory()).createConnectionsResponseConnections());
			res.getConnections().getConnection().addAll(
					service.getConnections(fromId, authToken)
			);
			res.setCount(BigInteger.valueOf(res.getConnections().getConnection().size()));
			return res;
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		}
    }
	
	@GET 
	@Path("/connections/{from}/{to}")
    @ApiOperation(value = "Get the connection from {from} to {to}")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 401, message = "Unauthorized"),
    		@ApiResponse(code = 500, message = "Internal Server Error")})
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Connection getConnection(
    		@PathParam("from") String fromId,
    		@PathParam("to") String toId,
    		@QueryParam("authToken") String authToken) 
	{
		logger.info(">> GETTING THE CONNECTION FROM " + fromId + " TO " + toId);
		try {			
			return service.getConnection(fromId, toId, authToken);
		} catch (UnauthorizedRnsException e) {
			throw new NotAuthorizedException("token-invalid");
		} catch (NotFoundRnsException e) {
			throw new NotFoundException();
		}
    }
	
///////////////////////////////////////////////////////////////////////////////			
    
}
