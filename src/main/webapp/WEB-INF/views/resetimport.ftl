<html>
<head>
<title><@s.text name="import.reset"/></title>
</head>
<body>
<br/>
			<div class="clear"><br/></div>
	 			<div>
	 			<#if resetFailed>
	 				<@s.text name="import.reset.failed"/> : ${failedMessage}<br/><br/>
	 				<@s.text name="import.reset.failed.next.instructions"/>
	 			<#else>
	 				<@s.text name="import.reset.done"/>
	 				<br><br/>
					<#if errorsAndWarningMessages.size()!=0 >
						<@s.text name="import.reset.listMessages"/> : 
		 				<br><br/>
		 				<ul>
							<#list errorsAndWarningMessages as messages>
								<li>${messages}</li>
							</#list>
						</ul>
					<#else>
						<@s.text name="import.reset.noErrorMessages"/>
 						<br/><br/>
						<@s.text name="import.reimport">
							<@s.param>/admin/importconfirm.html</@s.param>
						</@s.text><@s.text name="global.or"/>&nbsp;
						<@s.text name="gisgraphy.ask.for.custom.dump">
							<@s.param>http://www.gisgraphy.com/premium/</@s.param>
						</@s.text>
					</#if>
				</#if>
				</div>
			 <div class="clear"><br/></div>
</body>
</html>