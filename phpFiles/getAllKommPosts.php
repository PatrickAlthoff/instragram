<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueID = $myfile->Kommentar[0]->userKey;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$getPostIDs= $conn->prepare("SELECT postID FROM kommentare WHERE userKey like '%$valueID%' ;");

if($getPostIDs->execute()){
        $result=$getPostIDs->fetchAll(PDO::FETCH_ASSOC);
        if(count($result)==0){
            echo "NoKommentare";
        }   
        else {
            foreach ($result as $row){
                foreach ($row as $key => $value){
                    $return = $return." : ".$value;
                }
            }
          
            echo "getPostIDs".$return;
        }
    }
    $conn = null;
?>

