<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueID = $myfile->user[0]->ID;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$stmt = $conn->prepare("SELECT userKey,base64 FROM user_pic WHERE userKey= $valueID;");

if($stmt->execute()){
    $result=$stmt->fetchAll(PDO::FETCH_ASSOC);
   if(count($result)==0){

      echo "UserNotChecked";
}   
    else {
       
        foreach ($result as $row){
            
           foreach ($row as $key => $value){
               $return = $return.":".$value;
             }
           }
        echo "UserPic".$return;
}
}
else{
  echo "Error";
}
$conn = null;
?>
