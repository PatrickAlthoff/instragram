<?php
$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");

$valueID = $myfile->story[0]->id;
$valueKEY = $myfile->story[0]->userKey;
$valueTITELS = $myfile->story[0]->titels;
$valueBASE = $myfile->story[0]->base64;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";
$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
$requestStory= $conn->prepare("SELECT * FROM stories WHERE userKey = $valueKEY;");
if($requestStory->execute()){
    $result=$requestStory->fetchAll(PDO::FETCH_ASSOC);
        if(count($result)>0){
            $updateStory = "UPDATE stories SET _id=$valueID, titels='$valueTITELS', base64='$valueBASE' WHERE userKey=$valueKEY";
            $conn->exec($updateStory);
          echo "UpdatedStory.";
            }
        else{
            try {
            // set the PDO error mode to exception
            $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $sql = "INSERT INTO stories (_id, userKey, titels, base64)
            VALUES ($valueID, $valueKEY, '$valueTITELS', '$valueBASE')";
            // use exec() because no results are returned
            $conn->exec($sql);
            echo "New story created successfully";
            }
            catch(PDOException $e){
            echo $sql . "<br>" . $e->getMessage();
            }
            }
            
}
$conn = null;

?>