<?php

require('dbconn.php');

$doctorName = $_GET['doctorname'];

$sql = "SELECT * FROM doctorkey WHERE doctorname = '$doctorName'";

$result = mysqli_query($conn, $sql);
$data = array();

if(mysqli_num_rows($result) > 0){

  while($row=mysqli_fetch_assoc($result)){
    $data[] = $row['uniquekey'];
  }

  $json = json_encode($data);
  echo $json;

}else{
  echo "no";
}

$conn -> close();

 ?>
