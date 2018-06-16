package de.hshl.softwareprojekt;

import android.graphics.Bitmap;
import android.provider.DocumentsContract;

import java.lang.annotation.Documented;
import java.sql.Timestamp;

public class XmlHelper {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DocumentsContract.Document document = null;

    XmlHelper(){

    }

    public static String buildXmlMessage(String title, String description, Bitmap image) {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<pictures>" +
                "<picture>" +
                "<title>" + title + "</title>"+
                "<description>" + description + "</description>" +
                "<timestamp>" + new Timestamp(System.currentTimeMillis()) + "</timestamp>" +
                "<imagedata>" + ImageHelper.bitmapToBase64(image) + "</imagedata>" +
                "</picture>" +
                "</pictures>" +
                "</data>";


        return xml;
    }

}
