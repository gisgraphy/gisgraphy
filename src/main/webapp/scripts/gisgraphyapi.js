

/*
 * Utility method that eval a json feed into a variable
 * the json feed and the variableName are required, if one of the parameters is missing : an exception is thrown
 * returns the object corresponding to the variable name 
 */
JSONToVar = function(jsonFeed,variableName){
	if (typeof jsonFeed == 'undefined'){
		throw "The JSON feed is a required parameter";
	}
	if (typeof variableName == 'undefined'){
		throw "The varName feed is a required parameter"; 
	}
	eval(variableName+"="+jsonFeed);
	return eval(variableName);
}

/* Default callback function that put the json feed in a variable 'fullTextQueryResults'*/
defaultCallback= function(jsonFeed){
 	JSONToVar(jsonFeed,'queryResults');
}

/*
 * Check parameters from a form
 * formName the HTML id of the form to check
 * return true if parameters are corrects
 */
 checkParameters = function(formName){
 	if (typeof $(formName)['q'] != 'undefined'){
	 	if (($(formName)['q'].value == '')){
			alert('The search term is required');
			$(formName)['q'].focus();
			return false;
		}
		if ($(formName)['q'].value.length > GisgraphyQuery.FULLTEXTQUERY_MAXLENGTH ){
			alert('The search term must have less than '+GisgraphyQuery.FULLTEXTQUERY_MAXLENGTH +' characters');
			$(formName)['q'].focus();
			return false;
		}
	}
if (pointIsRequired==true){
	if (typeof $(formName)['lat'] != 'undefined'){
		if ($(formName)['lat'].value == ''){
			alert('The latitude is mandatory');
			$(formName)['lat'].focus();
			return false;
		}
		if ($(formName)['lat'].value > 90 || $(formName)['lat'].value < -90){
			alert('The latitude must be > -90 and < 90');
			$(formName)['lat'].focus();
			return false;
		}
	}
	if (typeof $(formName)["lng"] != 'undefined'){
		 if ($(formName)["lng"].value == ''){
			alert('The longitude is mandatory');
			$(formName)["lng"].focus();
			return false;
		}
		 if ($(formName)["lng"].value > 180 || $(formName)["lng"].value < -180){
			alert('The longitude must be > -180 and < 180');
			$(formName)["lng"].focus();
			return false;
		}
	}
}
	return true;
 }

/*
 * Class to execute query from a form
 */
 GisgraphyQuery = Class.create({

/*
 * Default Constructor 
 */
  initialize: function(formName, callbackParam){
	this.formName = formName;
	this.form = $(formName);
	this.URL= this.form.action
	this.callback = callbackParam || defaultCallback ;
	if (this.form == 'undefined'){
		this.parameters = {} ;
	}
	else {
		this.parameters = this.form.serialize(true) ;
	}
  },
/*
 * Set a parameter for the Ajax query"
 * The parameter value and the parameter name are both required.
 * The parameters are to be a string
 */
  setParameter : function(parameterName,parameterValue){
		if ((typeof parameterName == 'undefined') ||(typeof parameterValue == 'undefined')) {
			throw "parameterName and parameterValue are both required parameters";
		}
		eval("this.parameters."+parameterName+"='"+parameterValue+"'");
  },
/*
 * Set the URL for the Ajax query"
 * The parameter value and the parameter name are both required.
 * The parameter value is to be a string
 */
  setURL : function(URLValue){
		if (typeof URLValue == 'undefined') {
			throw "URLValue is mandatory";
		}
		this.URL = URLValue;
  },
/*
 * Execute the query
 */
  execute : function(){
	request = this;
	//overide format
	this.parameters.format=GisgraphyQuery.JSON_FORMAT;
	checkParameters(this.formName);
	new Ajax.Request(this.URL, {
	  method: 'get',
	  evalJSON : true,
	  onSuccess: function(transport) {
	    if (transport.responseText){
	     request.callback(transport.responseText)
	    } else {
	      alert("No response from server");
	      }
	  },
	  onFailure : function(transport){
	  	alert("An error has occured : "+ transport.responseText.error);
	  }, 
	  encoding : "UTF-8",
	  parameters : this.parameters
	});
 }
});

//Constants
GisgraphyQuery.JSON_FORMAT="JSON";
GisgraphyQuery.FULLTEXTQUERY_MAXLENGTH = 200;
