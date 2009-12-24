<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>Ceci est une page de test avec des balises PHP</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Page de test</h2>
            <?php
             $userInfo = array (
                
             );
             displayInfo($userInfo);
             function displayInfo ($userInfo) {
                 echo "Les infos sont : <br/>";
                 echo  $userInfo['name']."<br/>";
                 echo  $userInfo['surname']."<br/>";
                 echo  $userInfo['phoneNumber']."<br/>";
             }
            ?>
    </body>
</html>