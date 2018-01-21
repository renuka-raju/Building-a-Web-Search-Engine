<?php

// make sure browsers see this page as utf-8 encoded HTML

header('Content-Type: text/html; charset=utf-8');

include 'SpellCorrector.php';

include 'simple_html_dom.php';

$limit = 10;

$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;

$selection = $_REQUEST['method'];

$results = false;


if ($query)

{

 require_once('solr-php-client/Apache/Solr/Service.php');

 $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample/');

 if (get_magic_quotes_gpc() == 1)

 {

 $query = stripslashes($query);

 }

 try
 {

 if($selection){

   $typed=$query;

   $needspell=false;

   $words=explode(" ",trim($query));

   $correct="";

   foreach($words as $word){

    //echo $word;

    $word=SpellCorrector::correct($word);

    $correct=trim($correct." ".$word);

   }

   if ($correct !== strtolower($query)){

    $needspell=true;

   }

   $query=$correct; 

  }


 if ($selection == 'TF-IDF') {

 $results = $solr->search($query, 0, $limit);

 }

 else if($selection == 'PageRank'){

 $results = $solr->search($query, 0, $limit, array("sort" => "pageRankFile desc"));

 }

 }

 catch (Exception $e)

 {

 die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");

 }

}

//get url from fileid-url map file

function findUrlFromFile($idinput){

$urlval="";

if (($handle = fopen("BostonGlobalMap.csv", "r")) !== FALSE) {

  while (($idurl = fgetcsv($handle)) !== FALSE) {

  if($idurl[0] == $idinput)

    {

    $urlval=$idurl[1];

    break;

    }  

    }

    fclose($handle);

    return $urlval;

}

}

function searchsnippet($path, $desc, $query)

    {

  //echo $path;

        $query=strtolower($query);

        $text = file_get_html($path);

  //echo ($text.trim());

        $ret = findinhtml($text, $query, $desc);

  //echo $ret;

        if($ret!==""){return $ret;}

        else

        {$query_terms = explode(" ",$query);

            foreach($query_terms as $term)

            {   //echo $term;

    $ret = findinhtml($text, $term, $desc);

               if($ret)

                {//echo $ret; 

    return $ret;
                }
             }
         }

        return "";

    }


function findinhtml($text, $query, $desc)

    {

    $retresult="";

    $textout = $text->plaintext;

    $splitted = preg_split( "/[.\n]/", $textout );

    foreach($splitted as $line)

    {

     $line=strtolower($line);

    if (strpos($line, $query) !== false)

    {

    if(strpos($retresult,$line) === false)

     $retresult.=$line;


  if(strlen($retresult)>160){

    $retresult=trimmer($retresult,$query);

    break;}

    }

    }

  if($retresult==="")

  {

  if(strpos($desc, $query) !== false){

  return $desc.'<br>';

  }

  }

  return $retresult;

  }

function trimmer($snippet, $query)

{

  //echo strlen($snippet); echo '<br>';

  $start=0;$pos=0;$end=0;

  $pos = strpos(strtolower($snippet), strtolower($query)) + strlen($query);

  if($pos>160)

    $start = $pos - 160;

  else

    $start = 0;

  $end = $start + 160;

  $snippet = '...' . substr($snippet,$start,$end) . '...';

  
  //echo "<br>".strlen($finalSnippet);

  return $snippet; 

}

?>

<html>

 <head>

 <title>PHP Solr Client Example</title>

 <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.css">

 <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

 <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>

<script type="text/javascript">

$(function() {

var mainurl = "http://localhost:8983/solr/myexample/suggest?q=";

var appendedurl;

$("#q").autocomplete({

source : function(request, response) {

var typedquery=$("#q").val();

if(typedquery.search(' ')>0)

{

var lastword=typedquery.substring(typedquery.lastIndexOf(' ')+1);

if(lastword.trim()=="")

appendedurl=mainurl+$("#q").val() +"&wt=json";

else

appendedurl =mainurl +lastword+ "&wt=json";

}

else

appendedurl = mainurl + $("#q").val() + "&wt=json";

 $.ajax({

 url : appendedurl,

 success : function(data) {

var x=[];

if(typedquery.search(' ')>0)

{

var index=typedquery.lastIndexOf(' ');

var front=typedquery.substring(0,index+1);

var last=typedquery.substring(index+1);

if(last.trim()!='')

{

count=data['suggest']['suggest'][last]['numFound'];

for($i=0;$i<count;$i++)

{

var sug=data['suggest']['suggest'][last]['suggestions'][$i]['term'];

if(last.trim()!='' && sug.indexOf('.') == -1)

{x.push(front+''+data['suggest']['suggest'][last]['suggestions'][$i]['term']);}}}

else

x.push(front);

}

else

{

count=data['suggest']['suggest'][typedquery]['numFound'];

for($i=0;$i<count;$i++)

{

var sug=data['suggest']['suggest'][typedquery]['suggestions'][$i]['term'];

if(sug.indexOf('.') == -1){x.push(data['suggest']['suggest'][typedquery]['suggestions'][$i]['term']);}}}

response(x);

 },

 dataType : 'jsonp',

 jsonp:'json.wrf'

 });},minLength : 1})

 });

</script>



 </head>

 <body>

 <form accept-charset="utf-8" method="get">

 <label for="q">Search:</label>

 <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>

 <br>

 <input type="radio" name="method"

 value="TF-IDF">TF-IDF

 <input type="radio" name="method"

 value="PageRank">PageRank

 <br>

 <input type="submit"/>

 </form>



<?php

if($needspell)

{

echo ('<div style="color:#646865;font-size:125%"> showing results for <u style="color:#124599;font-size:125%"><a href="Source.php?q='.$query.'&method='.$_GET['method'].'" style="font-size:110%">'.$query.'</u></div></a>');

echo ('<div style="color:#646865;font-size:110%"> instead of <a href="Source.php?q='.$typed.'&method='.$_GET['method'].'" style="font-size:110%">'.$typed.'</a></div><br />');

}

?>

<?php

if ($results)

{

 $total = (int) $results->response->numFound;

 $start = min(1, $total);

 $end = min($limit, $total);

?>

 <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>

 <ol>

<?php

foreach ($results->response->docs as $doc)

 {

 $title=$doc->title;

 //$url=$doc->og_url;

 $id=$doc->id;

 $url=findUrlFromFile(end(explode('/', $id)));

 $description=$doc->description;

 //echo $url;

 //echo $title;

 //echo $id;

 //echo $description;

?>

 <li>

 <table style="border: 1px solid black; text-align: left">

 <?php 

 if($title) {

 ?>

 <tr>

 <th><?php echo htmlspecialchars('title', ENT_NOQUOTES, 'utf-8'); ?></th>

 <td><?php echo "<a target='_blank' href='$url'>$title</a>";?></td>

 </tr>

 <?php

 }

 else{

 ?>

 <tr>

 <th><?php echo htmlspecialchars('title', ENT_NOQUOTES, 'utf-8'); ?></th>

 <td><?php echo htmlspecialchars('N/A', ENT_NOQUOTES, 'utf-8'); ?></td>

 </tr>

 <?php

 }

 ?>

 <tr>

 <th><?php echo htmlspecialchars('url', ENT_NOQUOTES, 'utf-8'); ?></th>

 <td><?php echo "<a target='_blank' href='$url'>$url</a>";?></td>

 </tr>

 <tr>

 <th><?php echo htmlspecialchars('id', ENT_NOQUOTES, 'utf-8'); ?></th>

 <td><?php echo htmlspecialchars($id, ENT_NOQUOTES, 'utf-8'); ?></td>

 </tr>

 <?php

 if ($description)

 {

 ?>

 <tr>

 <th><?php echo htmlspecialchars('description', ENT_NOQUOTES, 'utf-8'); ?></th>

 <td><?php echo htmlspecialchars($description, ENT_NOQUOTES, 'utf-8'); ?></td>

 </tr>

 <?php

 }

 else

 {

 ?>

 <tr>

 <th><?php echo htmlspecialchars('description', ENT_NOQUOTES, 'utf-8'); ?></th>

 <td><?php echo htmlspecialchars('N/A', ENT_NOQUOTES, 'utf-8'); ?></td>

 </tr>

 <?php

 }

 $snippet=searchsnippet($id, $description, $query);

 //echo $snippet;

 ?>

<tr>

 <th><?php echo htmlspecialchars('snippet', ENT_NOQUOTES, 'utf-8'); ?></th>

 <td><?php echo $snippet; ?></td>

</tr>

 </table>

 </li>

 <?php

 }

 ?>

 </ol>

 <?php

 }

 ?>

 </body>

</html>

