package com.ftc.modelo;

public class FacturaCabecera {
    
    private String persona;
    private String cdDocumento;
    private String archivo;
    private String locacion;
    private String serie;
    private String folio;
    private String tipo;
    private String strFecha;
    private String formaDePago;
    private String strSubTotal;
    private double subTotal;
    private String strDescuento;
    private double descuento;
    private String tipoCambio;
    private String moneda;
    private String strTotal;
    private double total;
    private String metodoDePago;
    private String lugarExpedicion;
    private String rfc;
    private String nombre;
    private String strTotalImpuestosTrasladados;
    private double totalImpuestosTrasladados;
    private String uuid;
    private String strFechaTimbrado;
    private String rfcReceptor;
    private String nombreReceptor;
    private String iva_strTasa;
    private double iva_tasa;
    private String iva_strImporte;
    private double iva_importe;
    private String ieps_strTasa;
    private double ieps_tasa;
    private String ieps_strImporte;
    private double ieps_importe;
    
    public FacturaCabecera(){}

    public String getCdDocumento() {
        return cdDocumento;
    }

    public void setCdDocumento(String cdDocumento) {
        this.cdDocumento = cdDocumento;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getLocacion() {
        return locacion;
    }

    public void setLocacion(String locacion) {
        this.locacion = locacion;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStrFecha() {
        return strFecha;
    }

    public void setStrFecha(String strFecha) {
        this.strFecha = strFecha;
    }

    public String getFormaDePago() {
        return formaDePago;
    }

    public void setFormaDePago(String formaDePago) {
        this.formaDePago = formaDePago;
    }

    public String getStrSubTotal() {
        return strSubTotal;
    }

    public void setStrSubTotal(String strSubTotal) {
        this.strSubTotal = strSubTotal;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public String getStrDescuento() {
        return strDescuento;
    }

    public void setStrDescuento(String strDescuento) {
        this.strDescuento = strDescuento;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getTipoCambio() {
        if(tipoCambio!=null && !tipoCambio.isEmpty()){
            return tipoCambio;
        }else{
            return "0";
        }
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getStrTotal() {
        return strTotal;
    }

    public void setStrTotal(String strTotal) {
        this.strTotal = strTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(String metodoDePago) {
        this.metodoDePago = metodoDePago;
    }

    public String getLugarExpedicion() {
        return lugarExpedicion;
    }

    public void setLugarExpedicion(String lugarExpedicion) {
        this.lugarExpedicion = lugarExpedicion;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getStrTotalImpuestosTrasladados() {
        return strTotalImpuestosTrasladados;
    }

    public void setStrTotalImpuestosTrasladados(String strTotalImpuestosTrasladados) {
        this.strTotalImpuestosTrasladados = strTotalImpuestosTrasladados;
    }

    public double getTotalImpuestosTrasladados() {
        return totalImpuestosTrasladados;
    }

    public void setTotalImpuestosTrasladados(double totalImpuestosTrasladados) {
        this.totalImpuestosTrasladados = totalImpuestosTrasladados;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStrFechaTimbrado() {
        return strFechaTimbrado;
    }

    public void setStrFechaTimbrado(String strFechaTimbrado) {
        this.strFechaTimbrado = strFechaTimbrado;
    }

    public String getRfcReceptor() {
        return rfcReceptor;
    }

    public void setRfcReceptor(String rfcReceptor) {
        this.rfcReceptor = rfcReceptor;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    public String getIva_strTasa() {
        return iva_strTasa;
    }

    public void setIva_strTasa(String iva_strTasa) {
        this.iva_strTasa = iva_strTasa;
    }

    public double getIva_tasa() {
        return iva_tasa;
    }

    public void setIva_tasa(double iva_tasa) {
        this.iva_tasa = iva_tasa;
    }

    public String getIva_strImporte() {
        return iva_strImporte;
    }

    public void setIva_strImporte(String iva_strImporte) {
        this.iva_strImporte = iva_strImporte;
    }

    public double getIva_importe() {
        return iva_importe;
    }

    public void setIva_importe(double iva_importe) {
        this.iva_importe = iva_importe;
    }

    public String getIeps_strTasa() {
        return ieps_strTasa;
    }

    public void setIeps_strTasa(String ieps_strTasa) {
        this.ieps_strTasa = ieps_strTasa;
    }

    public double getIeps_tasa() {
        return ieps_tasa;
    }

    public void setIeps_tasa(double ieps_tasa) {
        this.ieps_tasa = ieps_tasa;
    }

    public String getIeps_strImporte() {
        return ieps_strImporte;
    }

    public void setIeps_strImporte(String ieps_strImporte) {
        this.ieps_strImporte = ieps_strImporte;
    }

    public double getIeps_importe() {
        return ieps_importe;
    }

    public void setIeps_importe(double ieps_importe) {
        this.ieps_importe = ieps_importe;
    }
    
    
}
