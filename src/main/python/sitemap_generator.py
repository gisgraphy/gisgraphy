import math
import random
""" generate a sitemap"""
MAX_SITEMAP_ENTRY_PER_FILE = 50000
SITEMAP_BASE_FILENAME = 'gisgraphy-sitemap-'
SITEMAP_INDEX_FILENAME = 'gisgraphy-sitemap-index.xml'
BUFFER_SIZE = 4500000
PRIORITY=["0.4","0.5","0.6"]

class SitemapGenerator:
	def __init__(self,filePath):
		"""doc"""
		self.filePath = filePath
		self.indexSitemapDesc = open(SITEMAP_INDEX_FILENAME,'w');
		
		
	def write_index_file_header(self):
		self.indexSitemapDesc.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")

	def write_index_file_footer(self):
		self.indexSitemapDesc.write("</sitemapindex>")

	def write_index_file_entry(self,filename):
		self.indexSitemapDesc.write("<sitemap><loc>http://services.gisgraphy.com/"+filename+"</loc><lastmod>2009-03-01</lastmod></sitemap>")
	
	def generate_sitemap(self):
		"""doc"""
		self.write_index_file_header()
		self.readFile();
		self.write_index_file_footer()
		self.indexSitemapDesc.close();

	def readFile(self):
		"""doc"""
		fdesc = open(self.filePath,'r');
		count = 1;
		while 1:
			lines = fdesc.readlines(BUFFER_SIZE)
    			if not lines:
      				break
			else :
				if len(lines) > MAX_SITEMAP_ENTRY_PER_FILE:
					raise "you should decrease the buffer size value"
				count = count + 1
				sitemapFilename = self.generate_sitemap_file(lines,count);
				self.write_index_file_entry(sitemapFilename);

	def generate_sitemap_file(self, lines, increment):
		"""doc"""
		filename = SITEMAP_BASE_FILENAME+str(increment)+".xml"
		print "will generate "+filename+" with "+str(len(lines))+ " lines"
		fileSitemap = open(filename,'w')
		fileSitemap.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.google.com/schemas/sitemap/0.84\">")
		for line in lines:
			        splited = line.split('\t')
				if len(splited) == 19:
					featureId= splited[0]
					population= splited[14]
					fileSitemap.write(self.generate_sitemap_node(featureId,population))
				else : 
					print "invalid size : "+str(len(splited))
		fileSitemap.write("</urlset>")
		fileSitemap.close();
		return filename;
		
		
	
	def generate_sitemap_node(self,featureId,population):
		"""doc"""
		return "<url><loc>http://services.gisgraphy.com/displayfeature.html?featureId="+str(featureId)+"</loc><lastmod>2009-09-30</lastmod><changefreq>monthly</changefreq><priority>"+str(self.calculate_priority(population))+"</priority></url>"
	
	def calculate_priority(self,population):
		#print int(population;
		if ((int(population))==0):
			#print PRIORITY[random.randint(0,2)];
			return PRIORITY[random.randint(0,2)];
		priority = (int(population) * 0.5)/18000;
		round_priority = (math.ceil(priority*10)/10);
		if (round_priority >= 1):
			return 1;
		if (round_priority <= 0.1):
			return 0.1;
		else :
			#print population;		
			#print round_priority;
			return round_priority;



		

def generate():
	generator = SitemapGenerator("/home/david/Bureau/dist2/data/import/allCountries.txt");
	generator.generate_sitemap()

generate()
