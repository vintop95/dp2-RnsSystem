
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
 *         &lt;element name="places">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="place" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element name="roadSegment" type="{}RoadSegment" minOccurs="0"/>
 *                             &lt;element name="parkingArea" type="{}ParkingArea" minOccurs="0"/>
 *                             &lt;element name="gate" type="{}Gate" minOccurs="0"/>
 *                             &lt;element name="nextPlacesUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element name="vehiclesUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element ref="{}uriOfNextPlaces"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="capacity" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
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
    "places"
})
@XmlRootElement(name = "placesResponse")
public class PlacesResponse {

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
    protected PlacesResponse.Places places;

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
     * Gets the value of the places property.
     * 
     * @return
     *     possible object is
     *     {@link PlacesResponse.Places }
     *     
     */
    public PlacesResponse.Places getPlaces() {
        return places;
    }

    /**
     * Sets the value of the places property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlacesResponse.Places }
     *     
     */
    public void setPlaces(PlacesResponse.Places value) {
        this.places = value;
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
     *         &lt;element name="place" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element name="roadSegment" type="{}RoadSegment" minOccurs="0"/>
     *                   &lt;element name="parkingArea" type="{}ParkingArea" minOccurs="0"/>
     *                   &lt;element name="gate" type="{}Gate" minOccurs="0"/>
     *                   &lt;element name="nextPlacesUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element name="vehiclesUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element ref="{}uriOfNextPlaces"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="capacity" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
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
        "place"
    })
    public static class Places {

        @XmlElement(nillable = true)
        protected List<PlacesResponse.Places.Place> place;

        /**
         * Gets the value of the place property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the place property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPlace().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PlacesResponse.Places.Place }
         * 
         * 
         */
        public List<PlacesResponse.Places.Place> getPlace() {
            if (place == null) {
                place = new ArrayList<PlacesResponse.Places.Place>();
            }
            return this.place;
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
         *         &lt;element name="roadSegment" type="{}RoadSegment" minOccurs="0"/>
         *         &lt;element name="parkingArea" type="{}ParkingArea" minOccurs="0"/>
         *         &lt;element name="gate" type="{}Gate" minOccurs="0"/>
         *         &lt;element name="nextPlacesUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;element name="vehiclesUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;element ref="{}uriOfNextPlaces"/>
         *       &lt;/sequence>
         *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="capacity" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
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
            "roadSegment",
            "parkingArea",
            "gate",
            "nextPlacesUri",
            "vehiclesUri",
            "uriOfNextPlaces"
        })
        public static class Place {

            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String self;
            protected RoadSegment roadSegment;
            protected ParkingArea parkingArea;
            protected Gate gate;
            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String nextPlacesUri;
            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String vehiclesUri;
            @XmlElement(required = true)
            protected UriOfNextPlaces uriOfNextPlaces;
            @XmlAttribute(name = "id", required = true)
            protected String id;
            @XmlAttribute(name = "capacity", required = true)
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger capacity;

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
             * Gets the value of the roadSegment property.
             * 
             * @return
             *     possible object is
             *     {@link RoadSegment }
             *     
             */
            public RoadSegment getRoadSegment() {
                return roadSegment;
            }

            /**
             * Sets the value of the roadSegment property.
             * 
             * @param value
             *     allowed object is
             *     {@link RoadSegment }
             *     
             */
            public void setRoadSegment(RoadSegment value) {
                this.roadSegment = value;
            }

            /**
             * Gets the value of the parkingArea property.
             * 
             * @return
             *     possible object is
             *     {@link ParkingArea }
             *     
             */
            public ParkingArea getParkingArea() {
                return parkingArea;
            }

            /**
             * Sets the value of the parkingArea property.
             * 
             * @param value
             *     allowed object is
             *     {@link ParkingArea }
             *     
             */
            public void setParkingArea(ParkingArea value) {
                this.parkingArea = value;
            }

            /**
             * Gets the value of the gate property.
             * 
             * @return
             *     possible object is
             *     {@link Gate }
             *     
             */
            public Gate getGate() {
                return gate;
            }

            /**
             * Sets the value of the gate property.
             * 
             * @param value
             *     allowed object is
             *     {@link Gate }
             *     
             */
            public void setGate(Gate value) {
                this.gate = value;
            }

            /**
             * Gets the value of the nextPlacesUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNextPlacesUri() {
                return nextPlacesUri;
            }

            /**
             * Sets the value of the nextPlacesUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNextPlacesUri(String value) {
                this.nextPlacesUri = value;
            }

            /**
             * Gets the value of the vehiclesUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getVehiclesUri() {
                return vehiclesUri;
            }

            /**
             * Sets the value of the vehiclesUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setVehiclesUri(String value) {
                this.vehiclesUri = value;
            }

            /**
             * Gets the value of the uriOfNextPlaces property.
             * 
             * @return
             *     possible object is
             *     {@link UriOfNextPlaces }
             *     
             */
            public UriOfNextPlaces getUriOfNextPlaces() {
                return uriOfNextPlaces;
            }

            /**
             * Sets the value of the uriOfNextPlaces property.
             * 
             * @param value
             *     allowed object is
             *     {@link UriOfNextPlaces }
             *     
             */
            public void setUriOfNextPlaces(UriOfNextPlaces value) {
                this.uriOfNextPlaces = value;
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

            /**
             * Gets the value of the capacity property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getCapacity() {
                return capacity;
            }

            /**
             * Sets the value of the capacity property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setCapacity(BigInteger value) {
                this.capacity = value;
            }

        }

    }

}
