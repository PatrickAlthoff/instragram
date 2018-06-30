<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valuePOST = $myfile->post[0]->ID;
$valueNAME = $myfile->post[0]->shareName;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
$getPost= $conn->prepare("SELECT * FROM posts WHERE _id= $valuePOST;");
$deletePost= $conn->prepare("DELETE FROM posts WHERE _id= $valuePOST");

if($getPost->execute()){
        $result=$getPost->fetchAll(PDO::FETCH_ASSOC);
        if(count($result)==0){
            echo "NoPost";
        }   
        else {
            try {
                // set the PDO error mode to exception
                $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
				$deletePost = "DELETE FROM posts WHERE _id= $valuePOST AND shareName= '$valueNAME'";
                // use exec() because no results are returned
                $conn->exec($deletePost);
                echo "PostDeleted";
                }
                catch(PDOException $e)
                {
                echo $deletePost . "<br>" . $e->getMessage();
                }

        }
    }
    else{
        echo "Error";
    }

?>
