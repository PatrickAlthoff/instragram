<?php
$path = "upload/";
$content = file_get_contents("php://input");

$time = date('d M Y H:i:s');

file_put_contents($path.$name.$time.".xml", $content);

$myfile = simplexml_load_file("$path"."$name"."$time".".xml") or die ("Unable to open file!");

$valueID = $myfile->users[0]->user[0]->timestamp;
$valueNAME = $myfile->users[0]->user[0]->username;
$valueEMAIL = $myfile->users[0]->user[0]->email;
$valuePASS = $myfile->users[0]->user[0]->password;
$valuePIC = $myfile->users[0]->user[0]->imagedata;

$servername = "db742957111.db.1and1.com";
$username = "dbo742957111";
$password = "Instragram1!";
$dbname = "db742957111";

$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

$stmt = $conn->prepare("SELECT * FROM users WHERE email = '$valueEMAIL';");

if ($stmt->execute()){
    $result=$stmt->fetchAll(PDO::FETCH_ASSOC);
    if(count($result)==0){
        try {
        // set the PDO error mode to exception
        $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $sql = "INSERT INTO users (_id, username, email, password)
            VALUES ($valueID, '$valueNAME', '$valueEMAIL', '$valuePASS')";
        // use exec() because no results are returned
        $conn->exec($sql);
        $sql2 = "INSERT INTO follows (userKey, follows)
            VALUES ($valueID, '')";
        $conn->exec($sql2);
          
        $sqlPic = "INSERT INTO user_pic (userkey, base64) VALUES ($valueID, '$valuePIC')";
        $conn->exec($sqlPic);
        }
        catch(PDOException $e)
        {
        echo $sql . "<br>" . $e->getMessage();
        }

        $conn = null;

        echo $valueID.":Is_Ok";
    }
    else{
	echo "Not_ok" ;
}
}
else{
	echo "Error2";
}

?>