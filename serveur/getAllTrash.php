<?php
$conn=mysqli_connect("mysql.hostinger.fr","u978485106_admin","trashadmin","u978485106_trash");

$sql="SELECT * FROM poubelle";      

$result = $conn->query($sql);

$json = array();

if ($result->num_rows > 0) {
     // output data of each row
     while($row = $result->fetch_assoc()) {
     	 $json[] = $row;
     }

     echo json_encode($json);

} else {
     echo "0 results";
}

$conn->close();
?>

