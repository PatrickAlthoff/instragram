<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueSEARCH = $myfile->post[0]->ID;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$searchPost= $conn->prepare("SELECT * FROM posts WHERE _id= $valueSEARCH;");


if($searchPost->execute()){
    $result=$searchPost->fetchAll(PDO::FETCH_ASSOC);
    if(count($result)==0){
        echo "NoPost";
    }   
    else {
        foreach ($result as $row){
            foreach ($row as $key => $value){
                $return = $return." : ".$value;
                    }
                }
        echo "FullPost".$return;
        }
    }


?>
