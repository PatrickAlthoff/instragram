<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueSTATUS = $myfile->update[0]->status;
$valueID = $myfile->update[0]->ID;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$getNumberLike = $conn->prepare("SELECT liked FROM posts WHERE _id= $valueID;");

if ($getNumberLike->execute()){
    $result=$getNumberLike->fetchAll(PDO::FETCH_ASSOC);
    foreach ($result as $row){
                foreach ($row as $key => $value){
                    $return = $value;
    }
    
                }
    if(count($result)>0){
        try {
        // set the PDO error mode to exception
       		$sum =$value+$valueSTATUS;
          $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
          $sql = "UPDATE posts SET liked=liked+$valueSTATUS WHERE _id=$valueID";
        // use exec() because no results are returned
        $conn->exec($sql);
        echo "Update erfolgreich.".$return;
        }
        catch(PDOException $e)
        {
        echo $sql . "<br>" . $e->getMessage();
        }

        $conn = null;
    }
    else{
	echo "Error1";
}
}
else{
	echo "Error2";
}
    


?>
