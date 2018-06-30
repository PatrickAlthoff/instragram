<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");

$valueID = $myfile->user[0]->ID;
$valueName = $myfile->user[0]->Name;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$getAllPostIDs = $conn->prepare("SELECT _id FROM posts WHERE userKey= $valueID;");
$getPostPic = $conn->prepare("SELECT base64 FROM posts WHERE _id= $valueID AND shareName!='$valueName';");
if($getAllPostIDs->execute()){
    $resultIDs=$getAllPostIDs->fetchAll(PDO::FETCH_ASSOC);
    $countPosts = count($resultIDs);
    if($countPosts==0){

}   
    else {
       
        foreach ($resultIDs as $row){
           foreach ($row as $key => $value){
               $returnID = $returnID.":".$value;
             }
           }
        echo "PostIDs".":".$countPosts.$returnID;
}

}
else{
  echo "Error";
}
if($getPostPic->execute()){
    $result=$getPostPic->fetchAll(PDO::FETCH_ASSOC);
    if(count($result)>0){
        foreach ($result as $row){
            foreach ($row as $key => $value){
               $returnPic = $value;
            }
        }
       echo "PostPic:".$returnPic.":".$valueID;
    }

 
            }

$conn = null;
?>