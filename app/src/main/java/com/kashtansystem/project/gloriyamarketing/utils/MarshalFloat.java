package com.kashtansystem.project.gloriyamarketing.utils;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by FlameKaf on 09.07.2017.
 * -----------------------------------
 */

public class MarshalFloat implements Marshal
{
    @Override
    public Object readInstance(XmlPullParser xmlPullParser, String s, String s1, PropertyInfo propertyInfo)
        throws IOException, XmlPullParserException
    {
        return Double.parseDouble(xmlPullParser.nextText());
    }

    @Override
    public void writeInstance(XmlSerializer xmlSerializer, Object o) throws IOException
    {
        xmlSerializer.text(o.toString());
    }

    @Override
    public void register(SoapSerializationEnvelope cm)
    {
        cm.addMapping(cm.xsd, "float", Double.class, this);
    }
}