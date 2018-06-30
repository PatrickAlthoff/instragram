<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueID = $myfile->User[0]->PostID;
$valueNAME = $myfile->User[0]->userName;
$valuePIC = $myfile->User[0]->profilbild;

$valueOLDNAME = $myfile->User[0]->oldName;
$valueOLDPIC = $myfile->User[0]->oldprofilbild;

$valueNAME = $valueNAME.":";
$valuePIC = $valuePIC.":";
$valueOLDPIC = $valueOLDPIC.":";
$valueOLDNAME = $valueOLDNAME.":";

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$getPostNames= $conn->prepare("SELECT username FROM kommentare WHERE postID= $valueID ;");
$getPostPics= $conn->prepare("SELECT userPics FROM kommentare WHERE postID= $valueID ;");

if($getPostNames->execute()){
    $result=$getPostNames->fetchAll(PDO::FETCH_ASSOC);
    
    foreach ($result as $row){
        foreach ($row as $key => $value){
            $returnName = $value;
            $insertName = str_replace($valueOLDNAME, $valueNAME, $returnName);
        }
    }
}
if($getPostPics->execute()){
    $result=$getPostPics->fetchAll(PDO::FETCH_ASSOC);
    
    foreach ($result as $row){
        foreach ($row as $key => $value){
            $returnPic = $value;
            $insertPic = str_replace($valueOLDPIC, $valuePIC, $returnPic);
        }
    }
    }

    if($valuePIC === $valueOLDPIC){
      $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
      $updateUser = "UPDATE kommentare SET username= '$insertName' WHERE postID=$valueID";
      // use exec() because no results are returned
      $conn->exec($updateUser);  
      
      echo "Same Pic";
    }
    else{
       try {
            // set the PDO error mode to exception
            $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $updateUser = "UPDATE kommentare SET username= '$insertName', userPics= '$insertPic' WHERE postID=$valueID";
            // use exec() because no results are returned
            $conn->exec($updateUser);
   
            echo "Update erfolgreich.";
        }
        catch(PDOException $e){
        echo $updateUser . "<br>" . $e->getMessage();
        } 
    }
       
    
    
echo $insertName.":".$insertPic;

          
$conn = null;  

?>