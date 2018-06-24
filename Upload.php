<?php
$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");

$valueID = $myfile->pictures[0]->picture[0]->id;
$valueNAME = $myfile->pictures[0]->picture[0]->name;
$valueBASE = $myfile->pictures[0]->picture[0]->base64;
$valueTITEL = $myfile->pictures[0]->picture[0]->titel;
$valueHASHTAGS = $myfile->pictures[0]->picture[0]->hashtags;
$valueDATUM = $myfile->pictures[0]->picture[0]->datum;
$valueLIKE = $myfile->pictures[0]->picture[0]->like;
$valueUSERKEY = $myfile->pictures[0]->picture[0]->userKey;
$valueUSERPIC = $myfile->pictures[0]->picture[0]->userPic;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

try {
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $sql = "INSERT INTO posts (_id, username, base64, titel, hashtags, date, liked, userKey, userPic)
VALUES ($valueID, '$valueNAME', '$valueBASE', '$valueTITEL', '$valueHASHTAGS', '$valueDATUM', $valueLIKE, $valueUSERKEY, '$valueUSERPIC' )";
    // use exec() because no results are returned
    $conn->exec($sql);
    echo "New record created successfully";
    }
catch(PDOException $e)
    {
    echo $sql . "<br>" . $e->getMessage();
    }

$conn = null;

?>
