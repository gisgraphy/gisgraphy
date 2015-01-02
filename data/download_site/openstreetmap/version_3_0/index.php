<?php
include("header.inc");
?>
<style>
.flag{
width:25px;
vertical-align:middle;
padding-left : 10px;
padding-right:10px;
padding-bottom:5px;
}
pre {
font-size:11px;
font-family:Arial,Verdana,sans-serif;
background-color:#EBF5FC;
font-size:1em;
padding:25px;
text-align:left;
text-indent:10px;
}
</style>
<?php
function _format_bytes($size)
{
  $sizes = array(" Bytes", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB");
      if ($size == 0) { return('n/a'); } else {
      return (round($size/pow(1024, ($i = floor(log($size, 1024)))), $i > 1 ? 2 : 0) . $sizes[$i]); }
}
 $dirlist .= '<li><img src="/flags/directory_back.png" alt="^" title="^" class="flag" /><a href="..">Parent directory</a></li>';
 if ($handle = opendir('.')) {
   while (false !== ($file = readdir($handle)))
      {
        if (is_dir($file)){
        if ($file != "." && $file != ".." && $file!="flags" && $file!="css" && $file!="scripts" && $file!="images"){
                $dirlist .= '<li><img src="/flags/directory.png" alt=">" title=">" class="flag" /><a href="'.$file.'">'.$file.'</a></li>';
        }
        }else if ($file!="favicon.ico" && $file != "index.php" && $file!="readme.txt" && $file!="robots.txt"){

                $countryArray=explode(".",$file);
                if (count(countryArray)>0){
                $country=strtoupper($countryArray[0]);
                $last_modification_date=date ("F d Y ", filemtime($file));
                $file_size=_format_bytes(filesize($file));
                $fileList .= '<li><img src="/flags/'.$country.'.png" alt=">" title=">" class="flag" /><a href="'.$file.'">'.$file.'</a>&nbsp;&nbsp;'.$file_size.'&nbsp;&nbsp;(last update : '.$last_modification_date.')</li>';
}
       }
}
  closedir($handle);
  }
?>

<h1>Download server</h1>
<pre><?php
if (file_exists("readme.txt")){ include("readme.txt"); } ?>
</pre>
<ul>
<?=$dirlist?>
<?=$fileList?>
</ul>
<?php include("footer.inc");?>
