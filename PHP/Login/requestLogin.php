<?php

  require('../dbconn.php');

  $id = $_POST['id'];
  $pwd = $_POST['pwd'];

  $sql = "SELECT uname FROM users WHERE uid = '$id' AND upwd = '$pwd'";

  $result = mysqli_query($conn, $sql);

  if(mysqli_num_rows($result) > 0){

    while ($row = mysqli_fetch_assoc($result)) {
      echo $row['uname'];
    }
  }else{
    echo "no". $conn->error;
  }

  $conn -> close();
 ?>
