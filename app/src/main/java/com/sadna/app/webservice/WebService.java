package com.sadna.app.webservice;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

/**
 * Created by avihoo on 14/03/2015.
 */
public class WebService {
    private final String namespace = "http://dbutils.app.sadna.com/";
    private final String url = "http://vmedu68.mtacloud.co.il:8081/ws/dbutils?wsdl";
    private String methodName = "getUser";
    private String soapAction =  "http://dbutils.app.sadna.com/getUser";

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public WebService (String methodName)
    {
        this.methodName = methodName;
        this.soapAction = this.namespace + this.methodName;
    }

    public String execute(String... params) throws XmlPullParserException, IOException {
        SoapObject request = new SoapObject(this.namespace, this.methodName);
        for (int i = 0; i < params.length; i+=2)
        {
            PropertyInfo propInfo = new PropertyInfo();
            propInfo.name = params[i];
            propInfo.type = PropertyInfo.STRING_CLASS;
            request.addPropertyIfValue(propInfo, params[i + 1]);
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(this.url);

        androidHttpTransport.call(this.soapAction, envelope);
        SoapPrimitive  resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
        return resultsRequestSOAP.toString();
    }
}
