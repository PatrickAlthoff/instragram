<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueID = $myfile->User[0]->UserID;
$valueNAME = $myfile->User[0]->UserName;
$valueDIS = $myfile->User[0]->Beschreibung;
$valuePIC = $myfile->User[0]->profilbild;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);


try {
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $updateUser = "UPDATE users SET username= '$valueNAME', beschreibung= '$valueDIS' WHERE _id=$valueID";
    // use exec() because no results are returned
    $conn->exec($updateUser);
    
    $updatePic = "UPDATE user_pic SET base64= '$valuePIC' WHERE userKey=$valueID";
    $conn->exec($updatePic);
  
  	$updatePosts = "UPDATE posts SET username= '$valueNAME', userPic= '$valuePIC' WHERE userKey=$valueID";
    // use exec() because no results are returned
    $conn->exec($updatePosts);
    
    echo "Update erfolgreich.";
}
catch(PDOException $e){
    echo $updateUser . "<br>" . $e->getMessage();
}
          
$conn = null;  

?>