package de.hshl.softwareprojekt;

//Dient als Interface zur Weiterleitung der Anfragen der HttpConnection Klasse.
public interface AsyncResponse {
    void processFinish(String output);

}
