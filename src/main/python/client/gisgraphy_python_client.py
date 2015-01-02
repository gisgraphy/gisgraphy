""" client for gisgraphy fulltext search """
from pprint import pprint
import urllib
import urllib2

GISGRAPHY_PARAMETERS_NAMES = ("q", "style",
                               "indent", "from", "to", "lang", "placetype")

GISGRAPHY_BASE_URL = "http://services.gisgraphy.com/fulltext/fulltextsearch?"
PYTHON_PARAMETER_VALUE = "PYTHON"
GISGRAPHY_FORMAT_PARAMETER_NAME = "format"

class GisgraphyException(Exception):
    "Gisgraphy exception"

class FulltextQuery:
    """ a fulltext query to be executed"""
    def __init__(self, search_words):
        """init method"""
        self.parameters = dict().fromkeys(GISGRAPHY_PARAMETERS_NAMES, "")
        self.parameters["q"] = search_words
        self.result = None
        
    def set_parameter(self, parameter_name, parameter_value):
        """set a parameter in the fulltext query, 
        raise an exception if not in GISGRAPHY_PARAMETERS_NAMES"""
        if parameter_name not in GISGRAPHY_PARAMETERS_NAMES:
            raise Exception(str(parameter_name)+ \
                            " is not a gisgraphy parameters")
        self.parameters[parameter_name] = str(parameter_value)
        
    def get_url(self):
        """return the url of the query"""
        gisgraphy_url = GISGRAPHY_BASE_URL
        parameter_counter = 0
        self.parameters[GISGRAPHY_FORMAT_PARAMETER_NAME] = \
        PYTHON_PARAMETER_VALUE
        for parameter_name, parameter_value in self.parameters.items():
            if parameter_counter == 0:
                gisgraphy_url = gisgraphy_url+parameter_name+"="+ \
                str(urllib.quote((parameter_value)))
            else:
                gisgraphy_url = gisgraphy_url+"&"+parameter_name \
                +"="+str(urllib.quote((parameter_value)))
            parameter_counter = parameter_counter +1
        return gisgraphy_url
    
    def execute(self):
        """execute the query"""
        con = urllib2.urlopen(self.get_url())
        python_data = con.read()
        self.result = eval(python_data)
        
    def get_results(self):
        """return the result of the executed query,
         execute the query first, if the query is not executed"""
        if not self.is_executed():
            self.execute()
        return self.result
    
    def is_executed(self):
        """return true if the query is executed"""
        if self.result == None:
            return False
        else:
            return  True
    
    def get_number_of_results(self):
        """return the number of results of the executed query,
         execute the query first, if the query is not executed"""
        if not self.is_executed():
            self.execute()
        else:
            return len(self.result["response"]["docs"])
    def get_qtime(self):
        """return how long the query tooks, return -1 if not executed"""
        if not self.is_executed():
            return -1
        return self.result["responseHeader"]["QTime"]

def example_of_use():
    """an example of use"""
    searchwords = ""
    while True :
        print "entrer votre recherche"
        searchwords = raw_input()
        if searchwords == "exit":
            break
        query = FulltextQuery(searchwords)
        query.set_parameter("indent", "true")
        query.set_parameter("from", 1)
        query.set_parameter("to", 1)
        url = query.get_url()
        print url
        query.execute()
        data = query.get_results()
        pprint(data)
        num = query.get_number_of_results()
        numstr = str(num) +" results were founds in " \
        +str(query.get_qtime())+" ms" 
        print "#"*len(numstr)+"\n"+numstr+"\r"+"#"*len(numstr)
    print "end of search...exiting"
    
example_of_use()         
                