<#--
	List containing all the included JS Files
-->
<#assign jsIncludes = []>
<#assign cssIncludes = []>

<#-- 
	Macro to include JavaScript Files :
	 - Makes sure it does not include the same file several times

	Parameters: 
	 - jsName : the name of the JS file, without the directory (e.g. Toolbox.js) 
-->
<#macro includeJs jsName>
	<#if jsIncludes?seq_contains(jsName)>
		<#-- Already included -->
	<#else>
		<script type="text/javascript" src="${jsName}" ></script>
		<#assign jsIncludes=jsIncludes + [jsName]>
	</#if>
</#macro>

<#macro includeCss cssName>
	
	<#if cssIncludes?seq_contains(cssName)>
		<#-- Already included -->
	<#else>
		<link href="${cssName}" rel="stylesheet" type="text/css" />
		<#assign cssIncludes=cssIncludes + [cssName]>
	</#if>

</#macro>