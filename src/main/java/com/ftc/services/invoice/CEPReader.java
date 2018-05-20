package com.ftc.services.invoice;

import com.ftc.modelo.CEPCabecera;
import com.ftc.modelo.CEPConcepto;
import com.ftc.modelo.CEPPago;
import com.ftc.modelo.CEPPagoDocumento;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CEPReader {

    private static final String XML_ATRIBUTO_VERSION = "Version";
    private static final String VERSION_33 = "3.3";

    public CEPCabecera procesaXML(String file) throws IOException, ParserConfigurationException, SAXException {
        File fXmlFile = new File(file);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = null;
        try {
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            boolean esCFDI = true;
            esCFDI = doc.getDocumentElement().getNodeName().toUpperCase().startsWith("CFDI");

            String prefijo = (esCFDI ? "cfdi:" : "");

            NodeList nList = doc.getElementsByTagName(prefijo + "Comprobante");

            CEPCabecera cabecera = new CEPCabecera();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String version = eElement.getAttribute(XML_ATRIBUTO_VERSION);
                    String ns = eElement.getAttribute("xmlns:pago10");
                    if (version != null && version.equals(VERSION_33)) {
                        cabecera.setVersion(version);
                        cabecera.setSerie(eElement.getAttribute("Serie"));
                        cabecera.setFolio(eElement.getAttribute("Folio"));
                        cabecera.setFecha(parseDate(eElement.getAttribute("Fecha")));
                        cabecera.setSubTotal(parseDouble(eElement.getAttribute("SubTotal")));
                        cabecera.setMoneda(eElement.getAttribute("Moneda"));
                        cabecera.setTotal(parseDouble(eElement.getAttribute("Total")));
                        cabecera.setLugarExpedicion(eElement.getAttribute("LugarExpedicion"));
                        cabecera.setXmlnsPago10(ns);
                        cabecera.setTipoDeComprobante(eElement.getAttribute("TipoDeComprobante"));
                    }
                }
            }

            nList = doc.getElementsByTagName(prefijo + "Emisor");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    cabecera.setRfcEmisor(eElement.getAttribute("Rfc"));
                    cabecera.setNombreEmisor(eElement.getAttribute("Nombre"));
                    cabecera.setRegimenFiscalEmisor(eElement.getAttribute("RegimenFiscal"));
                }
            }

            nList = doc.getElementsByTagName(prefijo + "Receptor");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    cabecera.setRfcReceptor(eElement.getAttribute("Rfc"));
                    cabecera.setNombreReceptor(eElement.getAttribute("Nombre"));
                    cabecera.setUsoCFDIReceptor(eElement.getAttribute("UsoCFDI"));
                }
            }


            nList = doc.getElementsByTagName("tfd:TimbreFiscalDigital");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    cabecera.setRfcProvCertif(eElement.getAttribute("RfcProvCertif"));
                    cabecera.setVersionTimbreFiscal(eElement.getAttribute("Version"));
                    cabecera.setUuid(eElement.getAttribute("UUID"));
                    cabecera.setFechaTimbrado(parseDate(eElement.getAttribute("FechaTimbrado")));
                    cabecera.setNoCertificadoSAT(eElement.getAttribute("NoCertificadoSAT"));
                } else if (nNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                    System.out.println(nNode.getNodeName());
                }
            }

            nList = doc.getElementsByTagName("cfdi:Concepto");
            List<CEPConcepto> conceptos = new LinkedList<CEPConcepto>();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    CEPConcepto concepto = new CEPConcepto();
                    concepto.setClaveProdServ(eElement.getAttribute("ClaveProdServ"));
                    concepto.setCantidad(parseIntger(eElement.getAttribute("Cantidad")));
                    concepto.setClaveUnidad(eElement.getAttribute("ClaveUnidad"));
                    concepto.setDescripcion(eElement.getAttribute("Descripcion"));
                    concepto.setValorUnitario(parseDouble(eElement.getAttribute("ValorUnitario")));
                    concepto.setImporte(parseDouble(eElement.getAttribute("Importe")));
                    conceptos.add(concepto);
                } else if (nNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                    System.out.println(nNode.getNodeName());
                }
            }
            cabecera.setConceptos(conceptos);

            nList = doc.getElementsByTagName("pago10:Pagos");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Version : " + eElement.getAttribute("Version"));
                    cabecera.setVersionPagos(eElement.getAttribute("Version"));
                } else if (nNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                    System.out.println(nNode.getNodeName());
                }
            }

            nList = doc.getElementsByTagName("pago10:Pago");
            List<CEPPago> pagos = new LinkedList<CEPPago>();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    CEPPago pago = new CEPPago();
                    pago.setFechaPago(parseDate(eElement.getAttribute("FechaPago")));
                    pago.setFormaDePago(eElement.getAttribute("FormaDePagoP"));
                    pago.setMoneda(eElement.getAttribute("MonedaP"));
                    pago.setMonto(parseDouble(eElement.getAttribute("Monto")));
                    pago.setRfcEmisorCtaOrd(eElement.getAttribute("RfcEmisorCtaOrd"));
                    pago.setCtaOrdenante(eElement.getAttribute("CtaOrdenante"));
                    pago.setRfcEmisorCtaBen(eElement.getAttribute("RfcEmisorCtaBen"));
                    pago.setCtaBeneficiario(eElement.getAttribute("CtaBeneficiario"));
                    CEPPagoDocumento documento = new CEPPagoDocumento();
                    documento.setPartida(temp+1);
                    pago.setDocumentoRelacionado(documento);
                    pagos.add(pago);
                } else if (nNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                    System.out.println(nNode.getNodeName());
                }
            }

            nList = doc.getElementsByTagName("pago10:DoctoRelacionado");
            List<CEPPagoDocumento> documentos = new LinkedList<CEPPagoDocumento>();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    CEPPagoDocumento documento = new CEPPagoDocumento();
                    documento.setPartida(temp+1);
                    documento.setIdDocumento(eElement.getAttribute("IdDocumento"));
                    documento.setFolio(eElement.getAttribute("Folio"));
                    documento.setSerie(eElement.getAttribute("Serie"));
                    documento.setMonedaDR(eElement.getAttribute("MonedaDR"));
                    documento.setMetodoDePagoDR(eElement.getAttribute("MetodoDePagoDR"));
                    documento.setNumParcialidad(parseIntger(eElement.getAttribute("NumParcialidad")));
                    documento.setSaldoAnt(parseDouble(eElement.getAttribute("ImpSaldoAnt")));
                    documento.setPagado(parseDouble(eElement.getAttribute("ImpPagado")));
                    documento.setSaldoInsoluto(parseDouble(eElement.getAttribute("ImpSaldoInsoluto")));
                    documentos.add(documento);
                } else if (nNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                    System.out.println(nNode.getNodeName());
                }
            }

            for (CEPPago pago : pagos){
                for(CEPPagoDocumento documento:documentos){
                    if (pago.getDocumentoRelacionado().getPartida()==documento.getPartida()){
                        pago.setDocumentoRelacionado(documento);
                        break;
                    }
                }
            }
            cabecera.setPagos(pagos);
            return cabecera;
        } catch (SAXException e) {
            e.printStackTrace(System.out);
            throw new SAXException(e);
        } catch (ParseException e) {
            e.printStackTrace(System.out);
            throw new SAXException(e);
        } catch (NumberFormatException e) {
            e.printStackTrace(System.out);
            throw new SAXException(e);
        }
    }

    private Date parseDate(String fecha) throws ParseException {
        // 2018-04-03T08:58:45
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date myDate = dateFormat.parse(fecha);
        return myDate;
    }

    private double parseDouble(String numero) throws NumberFormatException {
        return Float.parseFloat(numero);
    }

    private int parseIntger(String numero) throws NumberFormatException {
        return Integer.parseInt(numero);
    }
}