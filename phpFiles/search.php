<?php

$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");
$valueSEARCH = $myfile->search[0]->query;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$searchUser = $conn->prepare("SELECT _id,username FROM users WHERE username LIKE '%$valueSEARCH%';");
$searchHash= $conn->prepare("SELECT * FROM posts WHERE hashtags LIKE '%$valueSEARCH%';");
$returnMultihash= $conn->prepare("SELECT _id FROM posts WHERE hashtags LIKE '%$valueSEARCH%';");
if(strpos($valueSEARCH,'#')!== false){
    if($searchHash->execute()){
        $result=$searchHash->fetchAll(PDO::FETCH_ASSOC);
        if(count($result)==0){

            echo "NoHash";
}   
    else {
		if(count($result)>1){
			if($returnMultihash->execute()){
				$resultMultihash=$returnMultihash->fetchAll(PDO::FETCH_ASSOC);
					foreach ($resultMultihash as $row){
						foreach ($row as $key => $value){
							$return = $return." : ".$value;
                    }
                }
				echo "HashInput".$return;
        
			}
		}
		else{
        foreach ($result as $row){
            
            foreach ($row as $key => $value){
                $return = $return." : ".$value;
                    }
                }
        echo "HashReturn".$return;
        }
		}
    }
}
else{
    if($searchUser->execute()){
        $result=$searchUser->fetchAll(PDO::FETCH_ASSOC);
        if(count($result)==0){

        echo "NoUser";
}   
    else {
        foreach ($result as $row){
            
            foreach ($row as $key => $value){
                $return = $return." : ".$value;
                }
            }
        echo "UserReturn".$return;
        }
    }
}
$conn = null;
?>