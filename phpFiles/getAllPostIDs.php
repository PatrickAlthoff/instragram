<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueFIDs = $myfile->post[0]->ID;

$FIDssplit = preg_split("/:/", $valueFIDs);

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);



for($i=0; $i < count($FIDssplit); $i++){
    $valueKey = $FIDssplit[$i];
    $getAllPostIDs= $conn->prepare("SELECT _id FROM posts WHERE userKey=$valueKey ;");
    if($getAllPostIDs->execute()){
            $result=$getAllPostIDs->fetchAll(PDO::FETCH_ASSOC);
            if(count($result)==0){
                
            }   
            else {
                foreach ($result as $row){
                    foreach ($row as $key => $value){
                        $return = $return.":".$value;
                    }
                }
          
                
            }
    }   
}
echo "AllPostIDs".$return;
$conn = null;
?>