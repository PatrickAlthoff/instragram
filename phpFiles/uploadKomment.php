<?php
$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");

$valueID = $myfile->Kommentar[0]->postid;
$valueNAME = $myfile->Kommentar[0]->username;
$valuePIC = $myfile->Kommentar[0]->userPic;
$valueKOMM = $myfile->Kommentar[0]->komment;
$valueDATE = $myfile->Kommentar[0]->postTime;
$valueKEY = $myfile->Kommentar[0]->userKey;

$valueNAME = $valueNAME.":";
$valuePIC = $valuePIC.":";
$valueKOMM = $valueKOMM.":_:";
$valueDATE = $valueDATE.":";
$valueKEY = $valueKEY.":";

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$requestKomm= $conn->prepare("SELECT * FROM kommentare WHERE postID= $valueID;");

if($requestKomm->execute()){
    $result=$requestKomm->fetchAll(PDO::FETCH_ASSOC);
        if(count($result)>0){
          $updateKomment = "UPDATE kommentare SET username= CONCAT(username, '$valueNAME'), userPics= CONCAT(userPics,'$valuePIC'), kommentar=CONCAT(kommentar, '$valueKOMM'), postTime=CONCAT(postTime,'$valueDATE'), userKey=CONCAT(userKey, '$valueKEY') WHERE postID=$valueID";
          $conn->exec($updateKomment);  
          echo "UpdatedKomm.";
            }
        else{
            try {
            // set the PDO error mode to exception
            $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $sql = "INSERT INTO kommentare (postID, username, userPics, kommentar, postTime, userKey)
            VALUES ($valueID, '$valueNAME', '$valuePIC', '$valueKOMM', '$valueDATE', '$valueKEY')";
            // use exec() because no results are returned
            $conn->exec($sql);
            echo "New Kommentar Entry created successfully";
            }
            catch(PDOException $e){
            echo $sql . "<br>" . $e->getMessage();
            }
            }
            
}
$conn = null;

?>