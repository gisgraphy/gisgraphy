<html>
<head>
<title><@s.text name="import.import"/></title>
<link rel="stylesheet" href="/styles/slidingform.css" type="text/css" media="screen"/>
</head>
<body>

<script type="text/javascript" src="/scripts/jquery-1.6.1.min.js"></script>
<script type="text/javascript" >
     jQuery.noConflict();
</script>
<script type="text/javascript" src="/scripts/slidingform.js"></script>
<script type="text/javascript" >
function enableList(disable,HtmlElementId){
if (disable=='true'){
	jQuery('#'+HtmlElementId).attr("disabled", true);
} else {
	jQuery('#'+HtmlElementId).removeAttr("disabled");
}
}

function checkHousenumber(){
	if ($('disableOpenstreetmaptrue').checked){
		//$('disableHouseNumbertrue').enable();
		//$('disableHouseNumberfalse').enable();
	} else {
		//$('disableHouseNumbertrue').disable();
		//$('disableHouseNumberfalse').disable();
		$('disableHouseNumberfalse').setValue('true');
	}
}

function checkOpenstreetMap(){
	if ($('disableHouseNumbertrue').checked){
		$('disableOpenstreetmaptrue').setValue('true');
	} 
}


<#if step!=1>
goToStep(${step});
</#if>
</script>
            <h1><@s.text name="import.wizard"/></h1>

<#if configGotProblems.toString()=='true'>
<div class="tip redtip">
<@s.text name="import.config.error"/>
</div>
<br/>
</#if>
        <div id="sliding">
 <div id="navigation" style="display:none;">

                    <ul>
                        <li class="selected">
                            <a href="#" id="step1" ><@s.text name="import.welcome" /></a>
                        </li>
                        <li>
                            <a href="#" id="step2" ><@s.text name="import.conf.check" /></a>
                        </li>
 			<li>
                            <a href="#" id="step3" ><@s.text name="import.conf.summary" /></a>
                        </li>
<li>
                            <a href="#" id="step4" ><@s.text name="global.dataset"/></a>
                        </li>
<li>
                            <a href="#" id="step5" ><@s.text name="global.countries"/></a>
                        </li>
<li>
                            <a href="#" id="step6" ><@s.text name="global.placetypes"/></a>
                        </li>
                        <li>

                            <a href="#" id="step7" ><@s.text name="import.options"/></a>
                        </li>
                       
						<li>
                            <a href="#" id="step8" ><@s.text name="global.confirm"/></a>

                        </li>
                    </ul>
                </div>
            <div id="wrappersliding">
                <div id="steps">
			<@s.url id="submitImportUrl"  method="doImport" includeParams="none" />
			<form id="formElem" name="formElem" action="${submitImportUrl}" method="post">
                        <fieldset class="step">
                           <legend>  <img src="/images/software.png" alt="importer wizard" title="importer wizard" class="logostep"/> <span class="titlestep" >Importer wizard</span></legend>
			  
                            <div class="sectionHeader"><@s.text name="import.welcome" /></div>
			       <p>
				<@s.text name="import.about">
					<@s.param>javascript:goToFinalStep();</@s.param>
				</@s.text>
<br/>
</p>
<div class="sectionHeader"><@s.text name="import.save.time" /></div>
<p>
<@s.text name="import.number.of.processor.detected">
	<@s.param>${numberOfProcessors}</@s.param>
</@s.text>
<br/>
<@s.text name="gisgraphy.ask.for.dump">
	<@s.param>http://download.gisgraphy.com/</@s.param>
</@s.text>
<br/>
<@s.text name="gisgraphy.ask.for.custom.dump">
	<@s.param>http://www.gisgraphy.com/premium/</@s.param>
</@s.text>
<div>
	<span class="next"><input type="button"  onclick="goToStep(2);" value="<@s.text name="global.next.step" />" class="gotostep"/></span>
</div>
</p>
                        </fieldset>
                        <fieldset class="step">
                            <legend><@s.text name="import.pre.check"/></legend>
			    <div class="sectionHeader"><@s.text name="import.con.check.checkable"/></div>
                            <p>
				<span class="labeloption"><@s.text name="import.geonames.directory"/> : </span>
                                <#if !DownloadDirectoryAccessible>
					<span class="ko"><@s.text name="global.not.exists"/></span>
					<div class="tip redtip">
						<@s.text name="import.directory.not.accesible"><@s.param>Geonames</@s.param><@s.param>${importerConfig.getOpenStreetMapDir()}</@s.param></@s.text> 
					</div>
				<#else>
					<span class="ok"><@s.text name="global.exists"/></span>
				</#if><br/><br/>
				<span class="labeloption"><@s.text name="import.openstretmap.directory"/> : </span>
				<#if !openStreetMapDownloadDirectoryAccessible>
					<span class="ko"><@s.text name="global.not.exists"/></span>
					<div class="tip redtip">
						<@s.text name="import.directory.not.accesible"><@s.param>Openstreetmap</@s.param><@s.param>${importerConfig.getOpenStreetMapDir()}</@s.param></@s.text> 
					</div>
				<#else>
					<span class="ok"><@s.text name="global.exists"/></span>
				</#if><br/><br/>
				<span class="labeloption"><@s.text name="global.fulltext.engine"/> : </span>
				<#if !fulltextSearchEngineAlive>
					<span class="ko"><@s.text name="global.not.reachable"/></span>
					<div class="tip redtip">
						<@s.text name="import.fulltextEngineNotReachable" ><@s.param>${FulltextSearchEngineURL}</@s.param></@s.text>
					</div>
				<#else>
					<span class="ok"><@s.text name="global.reachable"/></span>
				</#if><br/><br/>
				<span class="labeloption"><@s.text name="import.placetype.regular.expression"/> <img src="/images/help.png" class="icon" alt="help" title="help"/> : </span>
				<#if !regexpCorrects>
					<span class="ko"><@s.text name="global.not.correct"/></span>
					<div class="tip redtip">
						<@s.text name="import.incorrectRegexp"/>
					</div>
				<#else>
					<span class="ok"><@s.text name="global.correct"/></span>
				</#if>
			
			<div class="sectionHeader"><@s.text name="import.con.check.uncheckable"/></div>
				<div class="tip yellowtip">
					<@s.text name="import.free.disk.space">
						<@s.param>70 GO</@s.param>
					</@s.text>
				</div>
			<div class="navigationtoolbar">
				<span class="prev"><input type="button" onclick="goToStep(1);" value="<@s.text name="global.prev.step" />" class="gotostep"/></span><span class="next"><input type="button"  onclick="goToStep(3);" value="<@s.text name="global.next.step" />" class="gotostep"/></span>
</p>
			</div>
                        </fieldset>
			 <fieldset class="step">
                            <legend><@s.text name="import.options"/></legend>
				<p>
				<@s.text name="import.option.moreinfos" />
				<div class="sectionHeader"><@s.text name="import.config.sentence"/> </div>                            
				
				<div class="optionscontainer">
		                       <ul>
						<@s.iterator value="configValuesMap.keySet()" id="item" >
							<li class="listoptions">${item} = ${configValuesMap[item]}<br/></li>
						</@s.iterator>
					</ul>

				</div>
				<div class="navigationtoolbar">
			<span class="prev"><input type="button" onclick="goToStep(2);" value="<@s.text name="global.prev.step" />" class="gotostep"/></span><span class="next"><input type="button"  onclick="goToStep(4);" value="<@s.text name="global.next.step" />" class="gotostep"/></span>
			</div>
			</p>
                        </fieldset>

 <fieldset class="step">
                            <legend><@s.text name="global.dataset"/></legend>
				<div class="sectionHeader"><@s.text name="importer.activate.desactivate"/></div>
					<br/>                          
					<@s.text name="import.config.sentence"/>
  	                                <@s.text name="import.dataset.choose" />.<@s.text name="import.enable.geonames.enable.zip"/>. <@s.text name="import.dataset.explanations"/><br/><br/>
					<@s.text name="import.dataset.status"><@s.param>Geonames</@s.param></@s.text> &nbsp;:&nbsp;
					<@s.if test="geonamesImporterEnabled">
						<span class="ok"><@s.text name="import.enabled"/></span><br/><br/>
					</@s.if>
					<@s.else>
						<span class="ko"><@s.text name="import.disabled"/></span><br/><br/>
					</@s.else>
						<div style="margin-left: 100px;" >
						<@s.text name="import.geonames"/> : <@s.radio name="geonamesImporterEnabled" value="%{geonamesImporterEnabled}" list="%{#@java.util.LinkedHashMap@{'true' : getText('global.yes'), 'false': getText('global.no')}}" id="disableGeonames" theme="simple"  />
							<!--<@s.checkbox value="%{geonamesImporterEnabled}" name="geonamesImporterEnabled" id="disableGeonames" theme="simple"/><@s.text name="import.geonames"/>--> 
						</div>
					<br/>
					<@s.text name="import.dataset.status"><@s.param>Openstreetmap</@s.param></@s.text> :&nbsp;
					<@s.if test="OpenStreetMapImporterEnabled">
						<span class="ok"><@s.text name="import.enabled"/></span><br/><br/>
					</@s.if>
					<@s.else>
					<span class="ko"><@s.text name="import.disabled"/></span><br/><br/>
					</@s.else>
					<div style="margin-left: 100px;" >
							<@s.text name="import.openstreetmap"/> : <@s.radio name="openStreetMapImporterEnabled" value="%{openStreetMapImporterEnabled}" list="%{#@java.util.LinkedHashMap@{'true' : getText('global.yes'), 'false': getText('global.no')}}" id="disableOpenstreetmap" theme="simple" onclick="checkHousenumber()" />
							 <!--<@s.checkbox value="%{openStreetMapImporterEnabled}" name="openStreetMapImporterEnabled" id="disableOpenstreetmap" theme="simple" onclick="checkHousenumber()"/> <@s.text name="import.openstreetmap"/>-->
					</div>
					<br/>
					<@s.text name="import.dataset.status"><@s.param><@s.text name="global.housenumbers"/></@s.param></@s.text> &nbsp;:&nbsp;
					<@s.if test="housenumberImporterEnabled">
						<span class="ok"><@s.text name="import.enabled"/></span><br/><br/>
					</@s.if>
					<@s.else>
						<span class="ko"><@s.text name="import.disabled"/></span><br/><br/>
					</@s.else>
						<div style="margin-left: 100px;" >
						<@s.text name="import.houseNumbers"/> : <@s.radio name="housenumberImporterEnabled" value="%{housenumberImporterEnabled}" list="%{#@java.util.LinkedHashMap@{'true' : getText('global.yes'), 'false': getText('global.no')}}" id="disableHouseNumber" theme="simple"  onclick="checkOpenstreetMap()"/>
							<!--<@s.checkbox value="%{housenumberImporterEnabled}" name="houseNumberImporterEnabled" id="disableHouseNumber" theme="simple"/> <@s.text name="import.houseNumbers"/>-->
					<br/><br/>
					</div>
					<@s.text name="import.dataset.status"><@s.param>Quattroshapes</@s.param></@s.text> &nbsp;:&nbsp;
					<@s.if test="quattroshapesImporterEnabled">
						<span class="ok"><@s.text name="import.enabled"/></span><br/><br/>
					</@s.if>
					<@s.else>
						<span class="ko"><@s.text name="import.disabled"/></span><br/><br/>
					</@s.else>
						<div style="margin-left: 100px;" >
						<@s.text name="import.quattroshapes"/> : <@s.radio name="quattroshapesImporterEnabled" value="%{quattroshapesImporterEnabled}" list="%{#@java.util.LinkedHashMap@{'true' : getText('global.yes'), 'false': getText('global.no')}}" id="disableQuattroshapes" theme="simple"  />
					</div>
					<br/>
						
						<br/><br/>
<div class="navigationtoolbar">
<span class="prev"><input type="button" onclick="goToStep(3);" value="<@s.text name="global.prev.step" />" class="gotostep"/></span><span class="next"><input type="button"  onclick="goToStep(5);" value="<@s.text name="global.next.step" />" class="gotostep"/></span>
</div>
                        </fieldset>
      


	 <fieldset class="step">
                            <legend><@s.text name="global.countries"/></legend>
				<div class="sectionHeader"><@s.text name="import.select.country"/></div>                            
				<p>
<@s.text name="import.country.explanation"/>.<br/>
<@s.radio name="importallcountries" list="%{#@java.util.LinkedHashMap@{'true' : getText('import.all.countries'), 'false': getText('import.select.country')}}" onclick="enableList(this.value,'countriesList');" theme="simple" value="true"/>
<br/><br/>
<select name="countryCodes" id="countriesList" multiple="true" size="7" disabled="true">
    <option value="AD">Andorra</option>
    
    <option value="AF">Afghanistan</option>
    <option value="AX">Aland Islands</option>
    <option value="AL">Albania</option>
    <option value="DZ">Algeria</option>
    <option value="AS">American Samoa</option>
    <option value="AO">Angola</option>
    <option value="AI">Anguilla</option>
    <option value="AQ">Antarctica</option>
    <option value="AG">Antigua and Barbuda</option>
    <option value="AR">Argentina</option>
    <option value="AM">Armenia</option>
    <option value="AW">Aruba</option>
    <option value="AU">Australia</option>
    <option value="AT">Austria</option>
    <option value="AZ">Azerbaijan</option>
    
    <option value="BS">Bahamas</option>
    <option value="BH">Bahrain</option>
    <option value="BD">Bangladesh</option>
    <option value="BB">Barbados</option>
    <option value="BY">Belarus</option>
    <option value="BE">Belgium</option>
    <option value="BZ">Belize</option>
    <option value="BJ">Benin</option>
    <option value="BM">Bermuda</option>
    <option value="BT">Bhutan</option>
    <option value="BO">Bolivia</option>
    <option value="BQ">Bonaire, Saint Eustatius and Saba</option>
    <option value="BA">Bosnia and Herzegovina</option>
    <option value="BW">Botswana</option>
    <option value="BV">Bouvet Island</option>
    <option value="BG">Bulgaria</option>
    <option value="BF">Burkina Faso</option>
    <option value="BI">Burundi</option>
    <option value="BR">Brazil</option>
    <option value="IO">British Indian Ocean Territory</option>
    <option value="VG">British Virgin Islands</option>
    <option value="BN">Brunei</option>
    
    <option value="CM">Cameroon</option>
    <option value="KH">Cambodia</option>
    <option value="CA">Canada</option>
    <option value="CV">Cape Verde</option>
    <option value="KY">Cayman Islands</option>
    <option value="CF">Central African Republic</option>
    <option value="TD">Chad</option>
    <option value="CL">Chile</option>
    <option value="CN">China</option>
    <option value="CX">Christmas Island</option>
    <option value="CC">Cocos Islands</option>
    <option value="CO">Colombia</option>
    <option value="KM">Comoros</option>
    <option value="CK">Cook Islands</option>
    <option value="CR">Costa Rica</option>
    <option value="HR">Croatia</option>
    <option value="CU">Cuba</option>
    <option value="CW">Curaçao</option>
    <option value="CY">Cyprus</option>
    <option value="CZ">Czech Republic</option>
    
    <option value="CD">Democratic Republic of the Congo</option>
    <option value="DK">Denmark</option>
    <option value="DJ">Djibouti</option>
    <option value="DM">Dominica</option>
    <option value="DO">Dominican Republic</option>
    
    <option value="TL">East Timor</option>
    <option value="EC">Ecuador</option>
    <option value="EG">Egypt</option>
    <option value="SV">El Salvador</option>
    <option value="GQ">Equatorial Guinea</option>
    <option value="ER">Eritrea</option>
    <option value="EE">Estonia</option>
    <option value="ET">Ethiopia</option>

    <option value="FK">Falkland Islands</option>
    <option value="FO">Faroe Islands</option>
    <option value="FJ">Fiji</option>
    <option value="FI">Finland</option>
    <option value="FR">France</option>
    <option value="GF">French Guiana</option>
    <option value="PF">French Polynesia</option>
    <option value="TF">French Southern Territories</option>

    <option value="DE">Germany</option>
    <option value="GA">Gabon</option>
    <option value="GD">Grenada</option>
    <option value="GE">Georgia</option>
    <option value="GG">Guernsey</option>
    <option value="GH">Ghana</option>
    <option value="GI">Gibraltar</option>
    <option value="GL">Greenland</option>
    <option value="GM">Gambia</option>
    <option value="GN">Guinea</option>
    <option value="GP">Guadeloupe</option>
    <option value="GR">Greece</option>
    <option value="GT">Guatemala</option>
    <option value="GU">Guam</option>
    <option value="GW">Guinea-Bissau</option>
    <option value="GY">Guyana</option>

    <option value="HT">Haiti</option>
    <option value="HM">Heard Island and McDonald Islands</option>
    <option value="HN">Honduras</option>
    <option value="HK">Hong Kong</option>
    <option value="HU">Hungary</option>
    
    <option value="IS">Iceland</option>
    <option value="ID">Indonesia</option>
    <option value="IN">India</option>
    <option value="IR">Iran</option>
    <option value="IQ">Iraq</option>
    <option value="IE">Ireland</option>
    <option value="IM">Isle of Man</option>
    <option value="IL">Israel</option>
    <option value="IT">Italy</option>
    <option value="CI">Ivory Coast</option>

    <option value="JM">Jamaica</option>
    <option value="JP">Japan</option>
    <option value="JE">Jersey</option>
    <option value="JO">Jordan</option>

    <option value="KZ">Kazakhstan</option>
    <option value="KE">Kenya</option>
    <option value="KI">Kiribati</option>
    <option value="XK">Kosovo</option>
    <option value="KW">Kuwait</option>
    <option value="KG">Kyrgyzstan</option>

    <option value="LA">Laos</option>
    <option value="LV">Latvia</option>
    <option value="LB">Lebanon</option>
    <option value="LS">Lesotho</option>
    <option value="LR">Liberia</option>
    <option value="LY">Libya</option>
    <option value="LI">Liechtenstein</option>
    <option value="LT">Lithuania</option>
    <option value="LU">Luxembourg</option>

    <option value="MO">Macao</option>
    <option value="MK">Macedonia</option>
    <option value="MG">Madagascar</option>
    <option value="MW">Malawi</option>
    <option value="MY">Malaysia</option>
    <option value="MV">Maldives</option>
    <option value="ML">Mali</option>
    <option value="MT">Malta</option>
    <option value="MH">Marshall Islands</option>
    <option value="MQ">Martinique</option>
    <option value="MR">Mauritania</option>
    <option value="MU">Mauritius</option>
    <option value="YT">Mayotte</option>
    <option value="MX">Mexico</option>
    <option value="FM">Micronesia</option>
    <option value="MD">Moldova</option>
    <option value="MC">Monaco</option>
    <option value="MN">Mongolia</option>
    <option value="ME">Montenegro</option>
    <option value="MS">Montserrat</option>
    <option value="MA">Morocco</option>
    <option value="MZ">Mozambique</option>
    <option value="MM">Myanmar</option>

    <option value="NA">Namibia</option>
    <option value="NR">Nauru</option>
    <option value="NP">Nepal</option>
    <option value="NL">Netherlands</option>
    <option value="NC">New Caledonia</option>
    <option value="NZ">New Zealand</option>
    <option value="NI">Nicaragua</option>
    <option value="NE">Niger</option>
    <option value="NG">Nigeria</option>
    <option value="NU">Niue</option>
    <option value="NF">Norfolk Island</option>
    <option value="KP">North Korea</option>
    <option value="MP">Northern Mariana Islands</option>
    <option value="NO">Norway</option>
    
    <option value="OM">Oman</option>

    <option value="PK">Pakistan</option>
    <option value="PW">Palau</option>
    <option value="PS">Palestinian Territory</option>
    <option value="PA">Panama</option>
    <option value="PG">Papua New Guinea</option>
    <option value="PY">Paraguay</option>
    <option value="PE">Peru</option>
    <option value="PH">Philippines</option>
    <option value="PN">Pitcairn</option>
    <option value="PL">Poland</option>
    <option value="PT">Portugal</option>
    <option value="PR">Puerto Rico</option>
    
    <option value="QA">Qatar</option>

    <option value="CG">Republic of the Congo</option>
    <option value="RE">Reunion</option>
    <option value="RO">Romania</option>
    <option value="RU">Russia</option>
    <option value="RW">Rwanda</option>

    <option value="BL">Saint Barthélemy</option>
    <option value="SH">Saint Helena</option>
    <option value="KN">Saint Kitts and Nevis</option>
    <option value="LC">Saint Lucia</option>
    <option value="MF">Saint Martin (MF)</option>
    <option value="SX">Saint martin (SX)</option>
    <option value="PM">Saint Pierre and Miquelon</option>
    <option value="VC">Saint Vincent and the Grenadines</option>
    <option value="WS">Samoa</option>
    <option value="SM">San Marino</option>
    <option value="ST">Sao Tome and Principe</option>
    <option value="SA">Saudi Arabia</option>
    <option value="SN">Senegal</option>
    <option value="RS">Serbia</option>
    <option value="SC">Seychelles</option>
    <option value="SL">Sierra Leone</option>
    <option value="SG">Singapore</option>
    <option value="SK">Slovakia</option>
    <option value="SI">Slovenia</option>
    <option value="SB">Solomon Islands</option>
    <option value="SO">Somalia</option>
    <option value="ZA">South Africa</option>
    <option value="GS">South Georgia and the South Sandwich Islands</option>
    <option value="KR">South Korea</option>
    <option value="SS">South sudan</option>
    <option value="ES">Spain</option>
    <option value="LK">Sri Lanka</option>
    <option value="SD">Sudan</option>
    <option value="SR">Suriname</option>
    <option value="SJ">Svalbard and Jan Mayen</option>
    <option value="SZ">Swaziland</option>
    <option value="SE">Sweden</option>
    <option value="CH">Switzerland</option>
    <option value="SY">Syria</option>

    <option value="TC">Turks and Caicos Islands</option>
    <option value="TG">Togo</option>
    <option value="TH">Thailand</option>
    <option value="TJ">Tajikistan</option>
    <option value="TK">Tokelau</option>

    <option value="TW">Taiwan</option>
    <option value="TZ">Tanzania</option>
    <option value="TO">Tonga</option>
    <option value="TT">Trinidad and Tobago</option>
    <option value="TN">Tunisia</option>
    <option value="TR">Turkey</option>
    <option value="TM">Turkmenistan</option>
    <option value="TV">Tuvalu</option>

    <option value="UG">Uganda</option>
    <option value="UA">Ukraine</option>
    <option value="AE">United Arab Emirates</option>
    <option value="GB">United Kingdom</option>
    <option value="UM">United States Minor Outlying Islands</option>
    <option value="US">United States of america</option>
    <option value="UY">Uruguay</option>
    <option value="VI">US Virgin Islands</option>
    <option value="UZ">Uzbekistan</option>

    <option value="VU">Vanuatu</option>
    <option value="VA">Vatican</option>
    <option value="VE">Venezuela</option>
    <option value="VN">Vietnam</option>

    <option value="WF">Wallis and Futuna</option>
    <option value="EH">Western Sahara</option>

    <option value="YE">Yemen</option>

    <option value="ZM">Zambia</option>
    <option value="ZW">Zimbabwe</option>
</select>
<br/><@s.text name="global.useshifttoselectmore"/> <br/><br/><br/><br/>

				<div class="navigationtoolbar">
<span class="prev"><input type="button" onclick="goToStep(4);" value="<@s.text name="global.prev.step" />" class="gotostep"/></span><span class="next"><input type="button"  onclick="goToStep(6);" value="<@s.text name="global.next.step" />" class="gotostep"/></span></div>
</p>
                        </fieldset>

	 <fieldset class="step">
                            <legend><@s.text name="global.placetypes"/></legend>
				<div class="sectionHeader"><@s.text name="import.select.placetype"/></div>                            
				<p>
<@s.text name="featureclasscode.explanation"/><br/><br/>
<@s.radio name="importallplacetype" list="%{#@java.util.LinkedHashMap@{'true' : getText('import.all.placetype'), 'false': getText('import.select.placetype')}}" onclick="enableList(this.value,'placetypesList');" theme="simple" value="true"/><br/><br/>
</span><@s.select listKey="toString()" id="placetypesList" listValue="toString()" name="placetypes" list="placetypesList" multiple="true" size="7"  theme="simple" disabled="true"/>
<br/><@s.text name="global.useshifttoselectmore"/> <br/><br/><br/>
				<div class="navigationtoolbar">
<span class="prev"><input type="button" onclick="goToStep(5);" value="<@s.text name="global.prev.step" />" class="gotostep "/></span><span class="next"><input type="button"  onclick="goToStep(7);" value="<@s.text name="global.next.step" />" class="gotostep"/></span></div>
</p>
                        </fieldset>

	 <fieldset class="step">
                            <legend><@s.text name="global.options"/></legend>
				<div class="sectionHeader"><@s.text name="import.select.options"/></div>                            
				<p>
				<@s.text name="import.only.important.options"/>. <@s.text name="import.option.moreinfos"/>.
				<div style="margin-left: 20px;" >
					<@s.checkbox value="%{fillIsInEnabled}" name="fillIsInEnabled" id="fillIsInEnabled" theme="simple"/> <@s.text name="import.options.is_in"/>
				<br/><@s.text name="import.options.is_in.explanation"><@s.param>http://www.gisgraphy.com/premium/</@s.param><@s.param>javascript:goToStep(4);</@s.param></@s.text>
				</div>
				<div style="margin-left: 20px;" >
					<@s.checkbox value="%{importEmbededAlternateNames}" name="importEmbededAlternateNames" id="importEmbededAlternateNames" theme="simple"/> <@s.text name="import.options.embededAlternatenames"/>
				<br/><@s.text name="import.options.embededAlternatenames.explanation"/>
				</div>
				<div style="margin-left: 20px;" >
					<@s.checkbox value="%{RetrieveFileEnable}" name="RetrieveFileEnable" id="RetrieveFileEnable" theme="simple"/> <@s.text name="import.options.retrievefiles"/>
				<br/><@s.text name="import.options.retrievefiles.explanations"/>
				</div>
			<div class="navigationtoolbar">	
<span class="prev"><input type="button" onclick="goToStep(6);" value="<@s.text name="global.prev.step" />" class="gotostep"/></span><span class="next"><input type="button"  onclick="goToStep(8);" value="<@s.text name="global.next.step" />" class="gotostep"/></span>
</p>
                        </fieldset>

                                         
			<fieldset class="step">
                            <legend><@s.text name="global.confirm"/></legend>
				<div class="sectionHeader">Premium</div>
			    	<p>
			   	<@s.text name="import.finalstep"/>. <@s.text name="premium.desc"/>
				</p>
				<div class="premiumsection">
				<a href="http://www.gisgraphy.com/premium" id="gopremium"><@s.text name="premium.learn.more"/></a>
					</div>
				
				<div class="sectionHeader"><@s.text name="global.confirm"/></div>
				<p>
					<@s.text name="import.confirm.sentence"/> 
					<@s.url id="importUrl" action="import" method="import" includeParams="all" /> 
				</p>
					<div class="submit">
						<@s.form action="${importUrl}" method="get" id="runImport"><@s.submit  value="%{getText('menu.admin.import')}" theme="simple" /></@s.form>&nbsp; &nbsp; 
					</div>
					
			
<div class="navigationtoolbar">
<span class="prev"><input type="button" onclick="goToStep(7);" value="<@s.text name="global.prev.step" />" class="gotostep"/></span></span>
</div>
                        </fieldset>
                </div>
            </div>
        </div>
<script type="text/javascript" >
var resp;
function checkConfig(){
	<@s.url id="checkConfigUrl"  method="checkConfig" includeParams="none" ><@s.param name="decorate" value="none"/></@s.url>
	new Ajax.Request('${checkConfigUrl}'+"?decorate=none", {
	  onSuccess: function(response) {
resp=response.responseText
  		 if (response.responseText.indexOf("true")>=0){
   				$("configCheckplaceholder").update('<span class="ok"><@s.text name="import.config.ok"/></span>');
  		 } else{
  				$("configCheckplaceholder").update('<span class="ko"><@s.text name="import.config.ko"/></span>');
  		 }
	  }, onFailure: function(response) {
	  	alert("An error occured when checking configuration");
	  }
	}
	);
}

function initStep(){
<#if step!=1>
goToStep(${step});
</#if>
}
</script>
</br>
</body>
</html>