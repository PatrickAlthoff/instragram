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

$countFollower = $conn->prepare("SELECT * FROM follows WHERE userKey LIKE '%$valueID%';");
$getFollows = $conn->prepare("SELECT follows FROM follows WHERE userKey = $valueID;");

if($countFollower->execute()){
    $result=$countFollower->fetchAll(PDO::FETCH_ASSOC);
   if(count($result)==0){
      $counter = count($result);
      echo "NoFollower";
}   
    else {
        $counter = count($result);
    }
}
else{
  echo "Error";
}
if($getFollows->execute()){
    $result = $getFollows->fetchAll(PDO::FETCH_ASSOC);
    if(count($result)==0){
        echo "NoFollows";
    }
    else{
        foreach ($result as $row){
           foreach ($row as $key => $value){
               $return = $value;
             }
           }
    }
}
else{
   echo "Error2";
}
echo $counter." : ".$return;

?>

