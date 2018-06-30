<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");

$valueID = $myfile->post[0]->ID;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$getFullPost = $conn->prepare("SELECT _id, base64, titel, hashtags, liked  FROM posts WHERE _id= $valueID;");

if($getFullPost->execute()){
    $result=$getFullPost->fetchAll(PDO::FETCH_ASSOC);
 
    foreach ($result as $row){
        foreach ($row as $key => $value){
            $return = $return." : ".$value;
        }
    }
        
}

else{
  echo "Error";
}

echo "FullPost".$return;
$conn = null;
?>

