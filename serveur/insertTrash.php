<?php
$con=mysqli_connect("mysql.hostinger.fr","u978485106_admin","trashadmin","u978485106_trash");

$username = $_GET['username'];
$password = $_GET['password'];

$long = $_GET['longitude'];
$lat = $_GET['latitude'];
$com = $_GET['commentaire'];
$date = $_GET['date'];

//Test User
$connexion = mysqli_query($con,"SELECT * FROM user where login='$username' and pass='$password'");
$row = mysqli_fetch_array($connexion);
$data = $row[0];
if($data)
{
	$sql="INSERT INTO poubelle (longitude, latitude,commentaire,date_creation) VALUES ('$long','$lat','$com','$date')";
	if (mysqli_query($con,$sql))
	{
	   echo "Trash have been inserted successfully";
	}
	else
	{
		echo "ERROR INSERT TRASH";
	}
}

else
{
	echo "User dont exist";
}

?>
