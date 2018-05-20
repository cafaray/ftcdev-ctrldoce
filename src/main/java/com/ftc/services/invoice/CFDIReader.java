package com.ftc.services.invoice;

import com.ftc.modelo.CFDICabecera;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CFDIReader {

    public static final String XML_ATRIBUTO_VERSION = "Version";
    public static final String VERSION_33 = "3.3";
    
    static public CFDICabecera procesaXML(String file) throws IOException,
            ParserConfigurationException, SAXException {
        File fXmlFile = new File(file);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = null;
        try{
            doc = dBuilder.parse(fXmlFile);
        }catch(SAXException e){
            e.printStackTrace(System.out);
            throw new SAXException(e);
        }
        doc.getDocumentElement().normalize();
        boolean esCFDI = true;
        esCFDI = doc.getDocumentElement().getNodeName().toUpperCase().startsWith("CFDI");
        
        String prefijo = (esCFDI ? "cfdi:" : "");

        NodeList nList = doc.getElementsByTagName(prefijo + "Comprobante");
        
        CFDICabecera cabecera = new CFDICabecera();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                
                String version = eElement.getAttribute(XML_ATRIBUTO_VERSION);
                if (version!=null && version.equals(VERSION_33)){
                    cabecera.setSerie(eElement.getAttribute("Serie"));
                    cabecera.setFolio(eElement.getAttribute("Folio"));
                    cabecera.setStrFecha(eElement.getAttribute("Fecha"));
                    cabecera.setFormaDePago(eElement.getAttribute("FormaPago"));
                    cabecera.setStrSubTotal(eElement.getAttribute("SubTotal"));
                    cabecera.setStrDescuento("0.0"); //eElement.getAttribute("descuento"));
                    cabecera.setTipoCambio("1.0"); //eElement.getAttribute("TipoCambio"));
                    cabecera.setMoneda(eElement.getAttribute("Moneda"));
                    cabecera.setStrTotal(eElement.getAttribute("Total"));
                    cabecera.setMetodoDePago(eElement.getAttribute("MetodoPago"));
                    cabecera.setLugarExpedicion(eElement
                            .getAttribute("LugarExpedicion"));
                } else {                        
                    cabecera.setSerie(eElement.getAttribute("serie"));
                    cabecera.setFolio(eElement.getAttribute("folio"));
                    cabecera.setStrFecha(eElement.getAttribute("fecha"));
                    cabecera.setFormaDePago(eElement.getAttribute("formaDePago"));
                    cabecera.setStrSubTotal(eElement.getAttribute("subTotal"));
                    cabecera.setStrDescuento(eElement.getAttribute("descuento"));
                    cabecera.setTipoCambio(eElement.getAttribute("TipoCambio"));
                    cabecera.setMoneda(eElement.getAttribute("Moneda"));
                    cabecera.setStrTotal(eElement.getAttribute("total"));
                    cabecera.setMetodoDePago(eElement.getAttribute("metodoDePago"));
                    cabecera.setLugarExpedicion(eElement
                            .getAttribute("LugarExpedicion"));
                }
            }
        }

        nList = doc.getElementsByTagName(prefijo + "Emisor");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                cabecera.setRfc(eElement.getAttribute("rfc") + eElement.getAttribute("Rfc"));
                cabecera.setNombre(eElement.getAttribute("nombre") + eElement.getAttribute("Nombre"));
            }
        }

        nList = doc.getElementsByTagName(prefijo + "Receptor");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                cabecera.setRfcReceptor(eElement.getAttribute("rfc") + eElement.getAttribute("Rfc"));
                cabecera.setNombreReceptor(eElement.getAttribute("nombre") + eElement.getAttribute("Nombre"));
            }
        }

        nList = doc.getElementsByTagName(prefijo + "Impuestos");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                cabecera.setStrTotalImpuestosTrasladados(eElement
                        .getAttribute("totalImpuestosTrasladados") + eElement.getAttribute("TotalImpuestosTrasladados"));
            }
        }

        nList = doc.getElementsByTagName(prefijo + "Traslados");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList nodes = nNode.getChildNodes();
                for (int ximpuesto = 0; ximpuesto < nodes.getLength(); ximpuesto++) {
                    Node nNodeImpuesto = nodes.item(ximpuesto);
                    if (nNodeImpuesto.getNodeType() == Node.ELEMENT_NODE) {
                        Element xElement = (Element) nNodeImpuesto;
                        String impuesto, tasa, importe;
                        impuesto = xElement.getAttribute("impuesto") + xElement.getAttribute("Impuesto");
                        tasa = xElement.getAttribute("tasa") + xElement.getAttribute("TasaOCuota");
                        importe = xElement.getAttribute("importe") + xElement.getAttribute("Importe");
                        if (impuesto.equals(CFDICabecera.IMPUESTO_IVA) || impuesto.equals(CFDICabecera.IMPUESTO_IVA_CODIGO)) {
                            cabecera.setIva_strTasa(tasa);
                            cabecera.setIva_strImporte(importe);
                        } else if (impuesto.equals(CFDICabecera.IMPUESTO_IEPS)) {
                            cabecera.setIeps_strTasa(tasa);
                            cabecera.setIeps_strImporte(importe);
                        } else {                            
                            cabecera.setIeps_strTasa(tasa);
                            cabecera.setIeps_strImporte(importe);
                        }
                    }
                }
            }
        }

        nList = doc.getElementsByTagName("tfd:TimbreFiscalDigital");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                cabecera.setUuid(eElement.getAttribute("UUID"));
                cabecera.setStrFechaTimbrado(eElement
                        .getAttribute("FechaTimbrado"));
            }
        }
        return cabecera;
    }

}
