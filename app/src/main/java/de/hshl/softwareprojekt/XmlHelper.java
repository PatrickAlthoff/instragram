package de.hshl.softwareprojekt;

public class XmlHelper {

    XmlHelper(){

    }

    public static String uploadPost(int id, String name, String base64, String titel, String hashtags, String date, boolean liked, long userKey, String userPic) {

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
                "<userPic>" + userPic + "</userPic>" +
                "</picture>" +
                "</pictures>" +
                "</data>";


        return xml;
    }
    public static String uploadUser(String username, String email, String password, String base64){

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

    public static String checkEmail(String email){

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<user>" +
                "<email>" + email + "</email>" +
                "</user>" +
                "</data>";
        return xml;
    }

    public static String getUsers(long id){

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<user>" +
                "<ID>" + id + "</ID>" +
                "</user>" +
                "</data>";
        return xml;
    }
    public static String updateFollows(long id, long followerID){

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<user>" +
                "<ID>" + id + "</ID>" +
                "<FID>" + followerID + "</FID>" +
                "</user>" +
                "</data>";
        return xml;
    }
    public static String sendSearchRequest(String query){

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<search>" +
                "<query>" + query + "</query>" +
                "</search>" +
                "</data>";
        return xml;
    }
    public static String updateStatus(int status, long id){

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<update>" +
                "<status>" + status + "</status>" +
                "<ID>" + id + "</ID>" +
                "</update>" +
                "</data>";
        return xml;
    }
    public static String uploadStory(long id, long userKey, String titels, String base64){
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<story>" +
                "<id>" + id + "</id>" +
                "<userKey>" + userKey + "</userKey>" +
                "<titels>" + titels + "</titels>" +
                "<base64>" + base64 + "</base64>" +
                "</story>" +
                "</data>";

        return xml;
    }
    public static String uploadKommentar(long id, String username, String userPic, String kommentar, long postTime, long userKey){
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<Kommentar>" +
                "<postid>" + id + "</postid>" +
                "<username>" + username + "</username>" +
                "<userPic>" + userPic + "</userPic>" +
                "<komment>" + kommentar + "</komment>" +
                "<postTime>" + postTime + "</postTime>" +
                "<userKey>" + userKey + "</userKey>" +
                "</Kommentar>" +
                "</data>";

        return xml;
    }
    public static String getKommentar(long id){
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<Kommentar>" +
                "<postid>" + id + "</postid>" +
                "</Kommentar>" +
                "</data>";

        return xml;
    }
    public static String updateData(long id, String userName, String userMail){
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<User>" +
                "<UserID>" + id + "</UserID>" +
                "<UserName>" + userName + "</UserName>" +
                "<UserMail>" + userMail + "</UserMail>" +
                "</User>" +
                "</data>";

        return xml;
    }
    public static String getFullPost(String id){
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<post>" +
                "<ID>" + id + "</ID>" +
                "</post>" +
                "</data>";
        return xml;
    }
    public static String updateProfilData(long id, String username, String beschreibung, String base64){
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<User>" +
                "<UserID>" + id + "</UserID>" +
                "<UserName>" + username + "</UserName>" +
                "<Beschreibung>" + beschreibung + "</Beschreibung>" +
                "<profilbild>" + base64 + "</profilbild>" +
                "</User>" +
                "</data>";
        return xml;
    }
    public static String updateAllKomms(long id, String username, String base64, String oldName, String oldBase){
        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<data>" +
                "<User>" +
                "<PostID>" + id + "</PostID>" +
                "<userName>" + username + "</userName>" +
                "<profilbild>" + base64 + "</profilbild>" +
                "<oldName>" + oldName + "</oldName>" +
                "<oldprofilbild>" + oldBase + "</oldprofilbild>" +
                "</User>" +
                "</data>";
        return xml;
    }



}
