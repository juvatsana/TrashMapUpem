<?php
$con=mysqli_connect("mysql.hostinger.fr","u978485106_admin","trashadmin","u978485106_trash");

$id = $_GET['id'];

$sql="DELETE FROM 'poubelle' WHERE 'id_poubelle'='$id'";

if (mysqli_query($con,$sql))
{
   echo "Trash have been deleted successfully";
}
else
{
	echo "ERROR delete";
}
?>
