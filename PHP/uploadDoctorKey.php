<?php
  require('dbconn.php');

  $doctorName = $_GET['doctorname'];
  $uniqueKey = $_GET['uniquekey'];

  $sql = "INSERT INTO doctorkey (doctorname, uniquekey) VALUES ('$doctorName','$uniqueKey')";

  if($conn -> query($sql) === TRUE ){
    echo "ok";
  }else{
    echo "error: ". $sql . "<br>" . $conn->error;
  }

  $conn -> close();

 ?>
