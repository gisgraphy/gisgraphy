<html>
<head>
	<title>debug Street</title>
</head>

<body>
	<@s.url  action="debugStreet" id="formurl"/>
	<@s.form action="${formurl}" method="get" id="debugStreet">
					<@s.textfield name="openstreetmapId" required="true" size="25" theme="simple" id="searchTerms" maxlength="200" />
					<@s.submit value="search"/>
	</@s.form>
	<#if openstreetmapId??>	
		<#if nearestMunicipalityByShape?? >
			name : ${nearestMunicipalityByShape.name}</br>
			lat : ${nearestMunicipalityByShape.latitude};long:${nearestMunicipalityByShape.longitude}</br>
			featureid : ${nearestMunicipalityByShape.featureId}</br>
			is municipality : ${nearestMunicipalityByShape.municipality.toString()}</br>
		<#else>
			There is no municipality by shape
		</#if>
		<hr/>
		<#if nearestNonMunicipalityByShape?? >
			name : ${nearestNonMunicipalityByShape.name}</br>
			lat : ${nearestNonMunicipalityByShape.latitude};long:${nearestNonMunicipalityByShape.longitude}</br>
			featureid : ${nearestNonMunicipalityByShape.featureId}</br>
			is municipality : ${nearestNonMunicipalityByShape.municipality.toString()}</br>
		<#else>
			There is no non municipality by shape
		</#if>
		<hr/>
		<#if nearestMunicipalityByVicinity?? >
			name : ${nearestMunicipalityByVicinity.name}<br/>
			lat : ${nearestMunicipalityByVicinity.latitude};long:${nearestMunicipalityByVicinity.longitude}</br>
			featureid : ${nearestMunicipalityByVicinity.featureId}</br>
			is municipality : ${nearestMunicipalityByVicinity.municipality.toString()}</br>
		<#else>
			There is no municipality by vicinity
		</#if>
		<hr/>
		<#if nearestNonMunicipalityByVicinity?? >
			name : ${nearestNonMunicipalityByVicinity.name}</br>
			lat : ${nearestNonMunicipalityByVicinity.latitude};long:${nearestNonMunicipalityByVicinity.longitude}</br>
			featureid : ${nearestNonMunicipalityByVicinity.featureId}</br>
			is municipality : ${nearestNonMunicipalityByVicinity.municipality.toString()}</br>
		<#else>
			There is no non municipality by nearestNonMunicipalityByVicinity
		</#if>
		<hr/>

	<#else>
		Please Enter an openstreetmapId
	</#if>
	
	
</body>
</html>