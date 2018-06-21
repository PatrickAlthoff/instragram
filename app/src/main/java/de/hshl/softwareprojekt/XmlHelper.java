package de.hshl.softwareprojekt;

import android.provider.DocumentsContract;

public class XmlHelper {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DocumentsContract.Document document = null;

    XmlHelper(){

    }

    public static String buildXmlMessage(int id, String name, String base64, String titel, String hashtags, String date, boolean liked, long userKey) {

        int like = liked ? 1:0;
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<pictures>" +
                "<picture>" +
                "<id>" + id + "</id>" +
                "<name>" + name + "</name>" +
                "<base64>" + base64 + "</base64>" +
                "<titel>" + titel + "</titel>"+
                "<hashtags>" + hashtags + "</hashtags>" +
                "<datum>" + date + "</datum>" +
                "<like>" + like + "</like>" +
                "<userKey>" + userKey + "</userKey>" +
                "</picture>" +
                "</pictures>" +
                "</data>";


        return xml;
    }
    public static String buildXmlUser(String username, String email, String password, String base64){

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<users>" +
                "<user>" +
                "<timestamp>" + System.currentTimeMillis() + "</timestamp>" +
                "<username>" + username + "</username>" +
                "<email>" + email + "</email>" +
                "<password>" + password + "</password>" +
                "<imagedata>" + base64 + "</imagedata>" +
                "</user>" +
                "</users>" +
                "</data>";
        return xml;
    }

    public static String buildXMLCheck(String email){

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<user>" +
                "<email>" + email + "</email>" +
                "</user>" +
                "</data>";
        return xml;
    }

}
