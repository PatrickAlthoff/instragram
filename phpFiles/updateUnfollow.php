<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueID = $myfile->user[0]->ID;
$valueFID = $myfile->user[0]->FID;
$insertID = $valueFID.":";
$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$lookForEntry= $conn->prepare("SELECT * FROM follows WHERE userKey= $valueID;");
$lookForFollow= $conn->prepare("SELECT follow FROM follows WHERE follow LIKE '%$valueFID%' AND userKey= $valueID;");

if($lookForEntry->execute()){
    $result=$lookForEntry->fetchAll(PDO::FETCH_ASSOC);
    if(count($result)==0){
        echo "NoFollowsEntry";
}   
    else {
        if($lookForFollow->execute()){
            $result=$lookForFollow->fetchAll(PDO::FETCH_ASSOC);
            if(count($result)>0){
                foreach ($result as $row){
                    foreach ($row as $key => $value){
                        $return = $value;
                    }
                }
                $newFollow = str_replace($insertID, "", $return);
                try {
                    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
                    $sql = "UPDATE follows SET follow='$newFollow' WHERE userKey=$valueID";
                    // use exec() because no results are returned
                    $conn->exec($sql);
                    echo "Unfollowed".$value;
                    }
                    catch(PDOException $e)
                    {
                    echo $sql . "<br>" . $e->getMessage();
                    }
            }else{
                echo "UnfollowNotPossible";
            }   
            
        }
    }
}
$conn = null;  


?>

