
package it.polito.dp2.RNS.sol3.wadl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="connections">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="connection" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element name="fromUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                             &lt;element name="toUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *                           &lt;/sequence>
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
    "connections"
})
@XmlRootElement(name = "connectionsResponse")
public class ConnectionsResponse {

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
    protected ConnectionsResponse.Connections connections;

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
     * Gets the value of the connections property.
     * 
     * @return
     *     possible object is
     *     {@link ConnectionsResponse.Connections }
     *     
     */
    public ConnectionsResponse.Connections getConnections() {
        return connections;
    }

    /**
     * Sets the value of the connections property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConnectionsResponse.Connections }
     *     
     */
    public void setConnections(ConnectionsResponse.Connections value) {
        this.connections = value;
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
     *         &lt;element name="connection" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="self" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element name="fromUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
     *                   &lt;element name="toUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
        "connection"
    })
    public static class Connections {

        @XmlElement(nillable = true)
        protected List<ConnectionsResponse.Connections.Connection> connection;

        /**
         * Gets the value of the connection property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the connection property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getConnection().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ConnectionsResponse.Connections.Connection }
         * 
         * 
         */
        public List<ConnectionsResponse.Connections.Connection> getConnection() {
            if (connection == null) {
                connection = new ArrayList<ConnectionsResponse.Connections.Connection>();
            }
            return this.connection;
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
         *         &lt;element name="fromUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
         *         &lt;element name="toUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
            "self",
            "fromUri",
            "toUri"
        })
        public static class Connection {

            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String self;
            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String fromUri;
            @XmlElement(required = true)
            @XmlSchemaType(name = "anyURI")
            protected String toUri;

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
             * Gets the value of the fromUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFromUri() {
                return fromUri;
            }

            /**
             * Sets the value of the fromUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFromUri(String value) {
                this.fromUri = value;
            }

            /**
             * Gets the value of the toUri property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getToUri() {
                return toUri;
            }

            /**
             * Sets the value of the toUri property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setToUri(String value) {
                this.toUri = value;
            }

        }

    }

}
