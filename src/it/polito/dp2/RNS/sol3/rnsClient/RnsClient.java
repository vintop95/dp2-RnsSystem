package it.polito.dp2.RNS.sol3.rnsClient;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.RNS.lab3.EntranceRefusedException;
import it.polito.dp2.RNS.lab3.ServiceException;
import it.polito.dp2.RNS.lab3.UnknownPlaceException;
import it.polito.dp2.RNS.lab3.WrongPlaceException;
import it.polito.dp2.RNS.sol3.jaxb.Connection;
import it.polito.dp2.RNS.sol3.jaxb.ErrorBody;
import it.polito.dp2.RNS.sol3.jaxb.Place;
import it.polito.dp2.RNS.sol3.jaxb.RnsErrorType;
import it.polito.dp2.RNS.sol3.jaxb.RnsRootResponse;
import it.polito.dp2.RNS.sol3.jaxb.Vehicle;
import it.polito.dp2.RNS.sol3.rnsClient.RnsClientException;

public class RnsClient {

	Client client;
	WebTarget baseTarget;
	JAXBContext jc;
	javax.xml.validation.Validator validator;
	
	RnsRootResponse rnsRootResponse;
	MediaType mediaType;
	String authToken;
	
	final String xsdFile = "xsd/RnsSystem.xsd";
	final String jaxbPackage = "it.polito.dp2.RNS.sol3.jaxb";
	
////////////////////////////////////////////////////////////////////////
	
	public RnsClient(MediaType mediaType, String authToken) 
			throws RnsClientException
	{
		initWebClasses();
		if(mediaType == null)
			this.mediaType = MediaType.APPLICATION_XML_TYPE;
		
		this.mediaType = mediaType;
		this.authToken = authToken;
	}
	
	private static URI getBaseURI() {
		String uri = System.getProperty("it.polito.dp2.RNS.lab3.URL");
		if(uri == null){
			uri = "http://localhost:8080/RnsSystem/rest";
		}
	    return UriBuilder.fromUri(uri).build();
	}
	
	public WebTarget getBaseTarget(){
		return baseTarget;
	}
	
	private void initWebClasses() throws RnsClientException{
		try{
			// Build the JAX-RS client object and get the 
			// target to the base URI
			client = ClientBuilder.newClient();
			baseTarget = client.target(getBaseURI());
			
			// Create validator that uses the schema 
		    SchemaFactory sf = 
		    		SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
		    Schema schema;
			
			schema = sf.newSchema(new File(xsdFile));
		    validator = schema.newValidator();
		    // validator.setErrorHandler(new MyErrorHandler());
		  
			// Create JAXB context related to the classes generated
		    jc = JAXBContext.newInstance(jaxbPackage);	
		}catch(SAXException e){
			throw new RnsClientException(e);
		}catch(JAXBException e){
			throw new RnsClientException(e);
		}
	}
	
	private String getAuthToken(){
		if(this.authToken == null)
			return "";
		
		return this.authToken;
	}
	
	public void setAuthToken(String authToken){
		this.authToken = authToken;
	}	
	
	public WebTarget buildTarget(String url){
		return client.target(url);
	}
	
////////////////////////////////////////////////////////////////////////////
	
	/**
	 * It validates any kind of JAXB-Class element against the schema
	 * defined in the JAXBContext
	 * USELESS if interceptor/provider validator is registered
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
	
////////////////////////////////////////////////////////////////////////////
	
	/**
	* It sends a GET request to the connectionTarget and we pass 
	* the type of the postResponse with the second parameter
	*/
	public <U> U getRequest(WebTarget connectionTarget, Class<U> c)
			throws ServiceException
	{
		U res = getRequestWithNull(connectionTarget,c);
		if(res == null){
			throw new ServiceException("404");
		}
		return res;
	}
	
	public <U> U getRequestWithNull(
			WebTarget connectionTarget, Class<U> c)
					throws ServiceException
	{
		Response response = connectionTarget
			.queryParam("authToken", authToken)
			.request()
			.accept(mediaType)
			.get();
		
		if (response.getStatus() == 404) {
			return null;
		}
		else if (response.getStatus() != 200) {
			String errorMsg = "Error in remote operation: " +
			response.getStatus() + " " + response.getStatusInfo();
			System.err.println(errorMsg);
			throw new ServiceException(errorMsg);
		}				
		
		U bodyResponse = response.readEntity(c);
		
		validateElement(bodyResponse);
		
		return bodyResponse;
	}
	
	public <T> String getLocationFromRoot(Class<T> c) 
			throws ServiceException
	{
		if(rnsRootResponse == null){
			rnsRootResponse = 
				getRequest(baseTarget.path("rns"), RnsRootResponse.class);
		}
		
		String location;
		if(Vehicle.class.equals(c)){
			location = rnsRootResponse.getVehicles();
		}else if(Place.class.equals(c)){
			location = rnsRootResponse.getPlaces();
		}else if(Connection.class.equals(c)){
			location = rnsRootResponse.getConnections();
		}else{
			throw new ServiceException("Class not available");
		}
		
		if(location == null || location.isEmpty()){
			rnsRootResponse = null;
			throw new ServiceException("Cannot read from service root.");
		}else{
			return location;
		}
	}
	
/////////////////////////////////////////////////////////////////////////////

	/**
	 * It sends a post request to the connectionTarget with a 
	 * body of type T (postRequest) and we pass the type of 
	 * the postResponse with the third parameter
	 */
	public <T,U> U postVehicleRequest(WebTarget connectionTarget,
			T postRequest, Class<U> c) throws EntranceRefusedException,
		UnknownPlaceException, WrongPlaceException, ServiceException
	{
		Response response = connectionTarget
				//useless but in future maybe not
				   .queryParam("authToken", getAuthToken()) 
				   .request(mediaType)
				   .accept(mediaType)
				   .post(Entity.xml(postRequest));
		
		if (response.getStatus() == 403) {
			String errorMsg = "Entrance refused: " +
				response.getStatus() + " " + response.getStatusInfo();
			System.out.println(errorMsg);
			throw new EntranceRefusedException(errorMsg);
		}else if (response.getStatus() == 409){
			ErrorBody err = response.readEntity(ErrorBody.class);
			if(err.getError() == null){
				throw new ServiceException();
			}
			String errorMsg;
			if(err.getError().equals(RnsErrorType.UNKNOWN_PLACE)){
				errorMsg = "Unknown origin/destination: " +
				   response.getStatus() + " " + response.getStatusInfo();
				System.out.println(errorMsg);
				throw new UnknownPlaceException(errorMsg);
			}else if(err.getError().equals(RnsErrorType.WRONG_PLACE)){
				errorMsg = "Wrong input gate: " +
				   response.getStatus() + " " + response.getStatusInfo();
				System.out.println(errorMsg);
				throw new WrongPlaceException(errorMsg);
			}
			
		}else if(response.getStatus() != 201){
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
	 * It sends a post request to the connectionTarget with a body of type T
	 * (postRequest) and we pass the type of the postResponse with the third parameter
	 */
	public <T,U> U putVehicleRequest(WebTarget connectionTarget,
			T putRequest, Class<U> c) throws
		UnknownPlaceException, WrongPlaceException, ServiceException
	{
		Response response = connectionTarget
				   .queryParam("authToken", getAuthToken())
				   .request(mediaType)
				   .accept(mediaType)
				   .put(Entity.xml(putRequest));
		
		if (response.getStatus() == 204){
			return null;
		}
		else if (response.getStatus() == 409){
			ErrorBody err = response.readEntity(ErrorBody.class);
			if(err.getError() == null){
				throw new ServiceException();
			}
			String errorMsg;
			if(err.getError().equals(RnsErrorType.UNKNOWN_PLACE)){
				errorMsg = "Unknown place: " +
				   response.getStatus() + " " + response.getStatusInfo();
				System.out.println(errorMsg);
				throw new UnknownPlaceException(errorMsg);
			}else if(err.getError().equals(RnsErrorType.WRONG_PLACE)){
				errorMsg = "Wrong place: " +
				   response.getStatus() + " " + response.getStatusInfo();
				System.out.println(errorMsg);
				throw new WrongPlaceException(errorMsg);
			}
			
		}else if(response.getStatus() != 200){
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
	 * It sends a delete request to the connectionTarget
	 */
	public void deleteVehicleRequest(
		WebTarget target, String outGateId) throws
		UnknownPlaceException, WrongPlaceException, ServiceException
	{
		Response response = target
				   .queryParam("authToken", getAuthToken())
				   .queryParam("outGateId", outGateId)
				   .request()
				   .delete();
		
		if (response.getStatus() == 409){
			ErrorBody err = response.readEntity(ErrorBody.class);
			if(err.getError() == null){
				throw new ServiceException();
			}
			String errorMsg;
			if(err.getError().equals(RnsErrorType.UNKNOWN_PLACE)){
				errorMsg = "Unknown place: " +
				   response.getStatus() + " " + response.getStatusInfo();
				System.out.println(errorMsg);
				throw new UnknownPlaceException(errorMsg);
			}else if(err.getError().equals(RnsErrorType.WRONG_PLACE)){
				errorMsg = "Wrong place: " +
				   response.getStatus() + " " + response.getStatusInfo();
				System.out.println(errorMsg);
				throw new WrongPlaceException(errorMsg);
			}
		}else if (response.getStatus() != 204) {
			String errorMsg = "Error in remote operation: "+
					response.getStatus() + " " + response.getStatusInfo();
			System.err.println(errorMsg);
			throw new ServiceException(errorMsg);
		}
	}

////////////////////////////////////////////////////////////////////////////

}
