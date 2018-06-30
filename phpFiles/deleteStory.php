<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueKEY = $myfile->user[0]->ID;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
$getStory= $conn->prepare("SELECT * FROM stories WHERE userKey= $valueKEY;");
$deleteStory= $conn->prepare("DELETE FROM stories WHERE userKey= $valueKEY");

if($getStory->execute()){
        $result=$getStory->fetchAll(PDO::FETCH_ASSOC);
        if(count($result)==0){
            echo "NoStoryToDelete";
        }   
        else {
            try {
                // set the PDO error mode to exception
                $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

                 // sql to delete a record
                $deleteStory = "DELETE FROM stories WHERE userKey=$valueKEY";

                // use exec() because no results are returned
                $conn->exec($deleteStory);
                echo "Record deleted successfully";
                }
                catch(PDOException $e)
                {
                echo $deleteStory . "<br>" . $e->getMessage();
                }

        }
    }
    else{
        echo "Error";
    }
$conn = null;
?>