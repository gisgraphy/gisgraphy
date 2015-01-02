<#import "macros/gisgraphysearch.ftl" as gisgraphysearch>
			<#if errorMessage!= ''>
				<div class="tip redtip">
					<div class="importantMessage">Error : ${errorMessage}</div>
				</div>
			<#elseif displayResults>
		 		<@gisgraphysearch.displayFulltextResults fulltextResponseDTO=responseDTO editable=admin/>
		 	</#if>