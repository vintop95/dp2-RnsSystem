
package it.polito.dp2.RNS.sol3.wadl;

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
@XmlRootElement(name = "connection")
public class Connection {

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
