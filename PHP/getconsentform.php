<?php

  require('dbconn.php');

  $surgeryname = $_GET['surgeryName'];

  $sql = "SELECT * FROM consentform WHERE surgeryname = '$surgeryname'";

  $result = mysqli_query($conn, $sql);

  $data = array();

  if(mysqli_num_rows($result) > 0){
    //echo "ok";
    while($row=mysqli_fetch_array($result)){
      array_push($data,
        array('content'=>$row[2],
              'ver'=>$row[3]
      ));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("consentform"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;

  }else{
    echo "no";
  }

  $conn -> close();
 ?>
