package com.sadna.app.webservice;

import android.util.Log;

import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by avihoo on 14/03/2015.
 */
public class WebService {
    private final String namespace = "http://interfaces.dbutils.app.sadna.com/";
    private final String url = "http://vmedu68.mtacloud.co.il:8080/sadna.dbutils-webservice/dbutils-webservice?wsdl";
    private final int TIMEOUT_IN_MILLISECONDS = 6000;

    private String methodName = "getUser";
    private String soapAction =  "http://interfaces.dbutils.app.sadna.com/getUser";

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

        for (int i = 0; i < params.length; i++)
        {
            PropertyInfo propInfo = new PropertyInfo();
            propInfo.name = "arg" + i;
            propInfo.type = PropertyInfo.STRING_CLASS;
            request.addPropertyIfValue(propInfo, params[i]);
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(this.url, TIMEOUT_IN_MILLISECONDS);
        androidHttpTransport.call(this.soapAction, envelope);
        SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
        if (resultsRequestSOAP != null)
        {
            return resultsRequestSOAP.toString();
        }
        return null;
    }


    public String executeToArray(ArrayList<String> ListOfStrings) throws XmlPullParserException, IOException {
        try {
            SoapObject request = new SoapObject(this.namespace, this.methodName);
            Gson gson = new Gson();
            String gsonList = gson.toJson(ListOfStrings);
            PropertyInfo propInfo = new PropertyInfo();
            propInfo.name = "arg0";
            propInfo.type = PropertyInfo.STRING_CLASS;
            request.addPropertyIfValue(propInfo, gsonList);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(this.url, TIMEOUT_IN_MILLISECONDS);
            androidHttpTransport.call(this.soapAction, envelope);
            SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
            if (resultsRequestSOAP != null)
            {
                return resultsRequestSOAP.toString();
            }
            return null;
        }
        catch (Throwable exception) {
            Log.e("WebService", exception.getMessage());
        }

        return "";
    }

}
