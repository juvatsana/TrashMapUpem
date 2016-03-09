<?php
$con=mysqli_connect("mysql.hostinger.fr","u978485106_admin","trashadmin","u978485106_trash");

$longitude = $_GET['longitude'];
$latitude = $_GET['latitude'];

$sql="DELETE FROM poubelle WHERE longitude='$longitude' AND latitude='$latitude'";

if (mysqli_query($con,$sql))
{
   echo "Trash have been deleted successfully";
}
else
{
	echo "ERROR deleteTrash";
}
?>
