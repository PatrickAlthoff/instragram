<?php
$uploadName = "fileupload1";
$uploadFolder = "upload/";
$fullServerLink = "http://intranet-secure.de/instragram/";
if(!filde_exists($uploadFolder)){
	mkdir($uploadFolder, 0777);
}
	$temp_name = $_FILES[$uploadName]['tmp_name'];
	$orginal_name = $_FILES[$uploadName]['name'];
	$filesize = $_FILES[$uploadName]['size'];
	if($temp_name != "" && exif_imagetype($temp_name) != false && $filesize <= 10485760) {
		$file_extension = ".".end((explode(".", $original_name)));
		$newFile = $uploadFolder.round(microtime(true)*1000).$file_extension;
		if (move_uploaded_file($temp_name, $newFile)){
			echo $fullServerLink.$newFile;
		}
		else {
			echo utf8_encode("Datei konnte nicht Upgeloadet werden. Bitte erneut versuchen.")
		}
		else {
			echo utf8_encode("Kein Bild erhalten. (max. 10MB)")
		}
	}
?>