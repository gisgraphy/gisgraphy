Want to be notify when import has finished ? you can define an URL that will be called when the import will be done :
 <@s.url id="sethookURL" action="hook" method="definehookurl"/>
						<@s.form action="${sethookURL}" method="get" id="hookform">
							<@s.textfield name="hookURL" size="100" theme="simple" id="hookurl" value="${importerManagerHookURL}" />
							<@s.hidden name="decorate" id="decorate" value="none" />
						</@s.form>
					<input type="button" value="set hook URL" onclick="return saveHookurl();"/>
					<@s.url id="status" action="import" method="status"/>

<br/><br/>
					<a href="${status}">Go back to the importer status</a>
<script type="text/javascript" >
function saveHookurl(){
	$("hookform").request(
    		{ 
		onComplete: function(transport){
			  	alert("The url has been saved to "+transport.transport.responseText);
		  	} ,
   		 onFailure : function(transport){
			  	alert("an error has occured when saving the hook url");
		  }
		 }
   	 );
	return false;
}
</script>