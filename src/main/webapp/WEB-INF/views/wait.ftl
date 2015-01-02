<html>
<head>
	<title><@s.text name="import.import"/></title>
  	<meta http-equiv="refresh" content="60;url=<@s.url id='thisUrl' action='import' />"/>
</head>

<body>
    <p style="border: 1px solid silver; padding: 5px; background: #ffd; text-align: center;margin-left:auto;margin-right:auto;">
       
      <@s.if test="importInProgress">
      <@s.text name="import.processingRequest"/>
     <br/><img src="/images/loading.gif" width=20px /> <@s.text name="import.wait.importSince"/> ${importFormatedTimeElapsed}<br/> 
     <br/><@s.text name="import.time.info" ><@s.param>10</@s.param><@s.param>30</@s.param></@s.text>. <@s.text name="import.time.why"><@s.param>http://www.gisgraphy.com/faq.html#why-import-long</@s.param><@s.param>http://www.gisgraphy.com/faq.html#how-long-import</@s.param></@s.text>
        
    </@s.if>
     <@s.if test="importAlreadyDone">
     <br/><@s.text name="import.took.time"/> ${importFormatedTimeElapsed}  
    </@s.if>
    
    </p>
    <br/>
    <div class="tip greentip" ><b><@s.text name="import.message"/></b> : <span id="messagebox" name="messageBox"><@s.text name="import.message.no.message"/></span> </div>
    <br/>
   
    <table style="width:100%;border:1px solid;">
    <tr>
    <th><@s.text name="import.importer.label" /></th>
    <th><@s.text name="import.status.label" /></th>
    <@s.iterator value="importerStatusDtoList" >
     <tr>
         <td> <img src="/images/<@s.property value='status' />.png" alt="<@s.property value='status' />" title="<@s.property value='status' />"/><@s.set name="processorNameHumanReadable" value="splitCamelCase(processorName)"/>${processorNameHumanReadable?lower_case?cap_first }<br/></td>
         <td>
	         <@s.property value="percent" />% :
		         <table style="width:100px;border:1px solid;padding:0px;margin:0px;vertical-align:middle;" ><tr>
		         <td style="width:${percent}%;background-color:#00DD00;padding:0px;margin:0px"> </td>
		         <td></td>
		         </tr>
		         </table>
	         <@s.if test="status.toString().equals('PROCESSING')">
	         <@s.property value="numberOfLineProcessed" /> / <@s.property value="numberOfLineToProcess" /> (<@s.property value="numberOfLinelefts" /> <@s.text name="importer.line.lefts" />)
	         <br/><@s.text name="import.currently.sentence" /> <@s.property value="currentFileName" /> <@s.text name="import.line.sentence" /> <@s.property value="currentLine" />
	          <#if (statusMessage?? && !statusMessage.equals(""))>
	         <script type="text/javascript">$('messagebox').innerHTML="<@s.property value="statusMessage" />"</script>
				</#if>
	         </@s.if>
	         <@s.if test="status.toString().equals('ERROR')">
	         <@s.property value="numberOfLineProcessed" /> / <@s.property value="numberOfLineToProcess" /> (<@s.property value="numberOfLinelefts" /> <@s.text name="importer.line.lefts" />)
	         <br/><span style="color:#FF0000">Error : <@s.property value="statusMessage" /></span>
	         </@s.if>
         </td>
     </tr>
 </@s.iterator>
 </table>

    
	</p>
	<@s.text name="global.legend"/> :<br/>
	<blockquote><@s.iterator value="statusEnumList" var="statusEnumValue">
		<img src="/images/<@s.property />.png" alt="<@s.property/>" title="<@s.property />"/> : <@s.text name="${statusEnumValue}" />&nbsp;&nbsp;<br/>
	</@s.iterator>
	</blockquote>
	<br/><br/>
	 <@s.text name="import.unknowstatus.info" />
	 <br/><br/>
    <p/>
    <@s.url id="thisUrl" action="import" includeParams="all" />
    <@s.text name="import.refreshText"/> <a href="${thisUrl}"><@s.text name="global.refresh"/></a>.
	
</body>
</html>