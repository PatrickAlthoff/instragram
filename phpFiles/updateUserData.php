<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueID = $myfile->User[0]->UserID;
$valueNAME = $myfile->User[0]->UserName;
$valueMAIL = $myfile->User[0]->UserMail;
$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$lookForEntry= $conn->prepare("SELECT * FROM users WHERE _id= $valueID;");
$lookForPosts= $conn->prepare("SELECT * FROM posts WHERE userKey=$valueID;");

if($lookForEntry->execute()){
    $result=$lookForEntry->fetchAll(PDO::FETCH_ASSOC);
    if(count($result)==0){
        echo "NoUserEntry";
    }   
    else {
        try {
                // set the PDO error mode to exception
                $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
                $updateUser = "UPDATE users SET username= '$valueNAME', email= '$valueMAIL' WHERE _id=$valueID";
                // use exec() because no results are returned
                $conn->exec($updateUser);
                echo "UserUpdated";
                }
                catch(PDOException $e){
                echo $updateUser . "<br>" . $e->getMessage();
                }
               
        
    }
        if($lookForPosts->execute()){
            $result=$lookForPosts->fetchAll(PDO::FETCH_ASSOC);
            if(count($result)>0){
                $updatePosts = "UPDATE posts SET username= '$valueNAME' WHERE userKey=$valueID";
                // use exec() because no results are returned
                $conn->exec($updatePosts);
               
            
            }
        }
        else{
            echo "NoToUpdate";
        }
}
$conn = null;  


?>
