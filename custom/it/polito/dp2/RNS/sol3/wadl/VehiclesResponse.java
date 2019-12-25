
package it.polito.dp2.RNS.sol3.wadl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="count" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/>
 *         &lt;element name="totalPages" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="next" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="vehicles">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="vehicle" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element name="entryTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                             &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="positionUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element name="originUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element name="destinationUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element name="placePathUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element ref="{}uriPath"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "count",
    "totalPages",
    "page",
    "next",
    "vehicles"
})
@XmlRootElement(name = "vehiclesResponse")
public class VehiclesResponse {

    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger count;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger totalPages;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger page;
    @XmlSchemaType(name = "anyURI")
    protected String next;
    @XmlElement(required = true)
    protected VehiclesResponse.Vehicles vehicles;

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCount(BigInteger value) {
        this.count = value;
    }

    /**
     * Gets the value of the totalPages property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the value of the totalPages property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalPages(BigInteger value) {
        this.totalPages = value;
    }

    /**
     * Gets the value of the page property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPage(BigInteger value) {
        this.page = value;
    }

    /**
     * Gets the value of the next property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNext() {
        return next;
    }

    /**
     * Sets the value of the next property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNext(String value) {
        this.next = value;
    }

    /**
     * Gets the value of the vehicles property.
     * 
     * @return
     *     possible object is
     *     {@link VehiclesResponse.Vehicles }
     *     
     */
    public VehiclesResponse.Vehicles getVehicles() {
        return vehicles;
    }

    /**
     * Sets the value of the vehicles property.
     * 
     * @param value
     *     allowed object is
     *     {@link VehiclesResponse.Vehicles }
     *     
     */
    public void setVehicles(VehiclesResponse.Vehicles value) {
        this.vehicles = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="vehicle" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element name="entryTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
     *                   &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="positionUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element name="originUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element name="destinationUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element name="placePathUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element ref="{}uriPath"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "vehicle"
    })
    public static class Vehicles {

        @XmlElement(nillable = true)
        protected List<VehiclesResponse.Vehicles.Vehicle> vehicle;

        /**
         * Gets the value of the vehicle property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the vehicle property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVehicle().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VehiclesResponse.Vehicles.Vehicle }
         * 
         * 
         */
        public List<VehiclesResponse.Vehicles.Vehicle> getVehicle() {
            if (vehicle == null) {
                vehicle = new ArrayList<VehiclesResponse.Vehicles.Vehicle>();
            }
            return this.vehicle;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;element name="entryTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
         *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="positionUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;element name="originUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;element name="destinationUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;element name="placePathUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;element ref="{}uriPath"/>
         *       &lt;/sequence>
         *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "self",
            "entryTime",
            "type",
            "state",
            "positionUri",
            "originUri",
            "destinationUri",
            "placePathUri",
            "uriPath"
        })
        public static class Vehicle {

            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String self;
            @XmlElement(required = true)
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar entryTime;
            @XmlElement(required = true)
            protected String type;
            @XmlElement(required = true)
            protected String state;
            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String positionUri;
            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String originUri;
            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String destinationUri;
            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String placePathUri;
            @XmlElement(required = true)
            protected UriPath uriPath;
            @XmlAttribute(name = "id")
            protected String id;

            /**
             * Gets the value of the self property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSelf() {
                return self;
            }

            /**
             * Sets the value of the self property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSelf(String value) {
                this.self = value;
            }

            /**
             * Gets the value of the entryTime property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getEntryTime() {
                return entryTime;
            }

            /**
             * Sets the value of the entryTime property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setEntryTime(XMLGregorianCalendar value) {
                this.entryTime = value;
            }

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setType(String value) {
                this.type = value;
            }

            /**
             * Gets the value of the state property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getState() {
                return state;
            }

            /**
             * Sets the value of the state property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setState(String value) {
                this.state = value;
            }

            /**
             * Gets the value of the positionUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPositionUri() {
                return positionUri;
            }

            /**
             * Sets the value of the positionUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPositionUri(String value) {
                this.positionUri = value;
            }

            /**
             * Gets the value of the originUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOriginUri() {
                return originUri;
            }

            /**
             * Sets the value of the originUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOriginUri(String value) {
                this.originUri = value;
            }

            /**
             * Gets the value of the destinationUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDestinationUri() {
                return destinationUri;
            }

            /**
             * Sets the value of the destinationUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDestinationUri(String value) {
                this.destinationUri = value;
            }

            /**
             * Gets the value of the placePathUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPlacePathUri() {
                return placePathUri;
            }

            /**
             * Sets the value of the placePathUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPlacePathUri(String value) {
                this.placePathUri = value;
            }

            /**
             * Gets the value of the uriPath property.
             * 
             * @return
             *     possible object is
             *     {@link UriPath }
             *     
             */
            public UriPath getUriPath() {
                return uriPath;
            }

            /**
             * Sets the value of the uriPath property.
             * 
             * @param value
             *     allowed object is
             *     {@link UriPath }
             *     
             */
            public void setUriPath(UriPath value) {
                this.uriPath = value;
            }

            /**
             * Gets the value of the id property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getId() {
                return id;
            }

            /**
             * Sets the value of the id property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setId(String value) {
                this.id = value;
            }

        }

    }

}
