<?php

  require('dbconn.php');

  // $docname = $_POST['docname'];
  // $patname = $_POST['patname'];
  // $content = $_POST['content'];
  // $papername = $_POST['papername'];
  // $paperver = $_POST['paperver'];
  // $time = $_POST['time'];
  // $userid = $_POST['userid'];
  //
  // $sql = "INSERT INTO agreement (time, docname, patname, content, papername, paperver, userid) VALUES ('$time', '$docname', '$patname', '$content', '$papername', '$paperver', '$userid')";

  $userid = $_POST['userid'];
  $time = $_POST['time'];
  $documentkey = $_POST['documentkey'];

  $sql = "INSERT INTO userkey (userid, documentkey) VALUES ('$userid','$documentkey')";

  if($conn -> query($sql) === TRUE ){
    echo "ok";
  }else{
    echo "error: ". $sql . "<br>" . $conn->error;
  }

  $conn -> close();

 ?>
