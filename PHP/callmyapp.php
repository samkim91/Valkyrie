<?php

  $value = $_GET['value'];

  // echo $value;
 ?>

<html>
  <head>

  </head>

  <body>
    <!-- <a href="Intent://call?value=<?=$value?>#Intent;scheme=callmyapp;package=com.example.teamproject;end"> 앱 열기 </a> -->
    <a href="callmyapp://call?value=<?=urlencode($value)?>" id="autoRun"></a>
    <!-- Intent://takePhoto#Intent;scheme=callMyApp;package=com.test.myapp;end -->
    
    <script>
      autoRun.click();
    </script>
  </body>

</html>
