#!/usr/bin/python2

import sys
import os
import subprocess

def process(dir):
	rootCSV = dir + "/csv/"
	rootModel = dir + "/models/"
	rootTriples = dir + "/triples/"
	tables = [f for f in os.listdir(rootCSV) if os.path.isfile(rootCSV + f)]
	for table in tables:
		tableName = table.split(".csv")[0]
		tableFile = rootCSV + table
		modelFile = rootModel + tableName + ".n3"
		triplesFile = rootTriples + tableName + ".n3"
		if os.path.isfile(modelFile):
			message = "Process " + table + " -> "
			try:
				sysCall = "sed -e 's/\\N\t/null\t/g' %s > /tmp/table1.csv" % tableFile
				subprocess.check_output(sysCall, shell=True)

				sysCall = "sed -e 's/\"//g' /tmp/table1.csv > /tmp/table.csv"
				subprocess.check_output(sysCall, shell=True)

				sysCall = "mvn exec:java -Dexec.mainClass=\"edu.isi.karma.rdf.OfflineCSVGenerator\" -Dexec.args=\"%s %s %s\"" % (modelFile, '/tmp/table.csv', triplesFile)
				subprocess.check_output(sysCall, shell=True)
				
				sysCall = "rapper -q -g -o ntriples %s | wc -l" % triplesFile
				processOutput = subprocess.check_output(sysCall, shell=True)
				message += processOutput[:-1] + " triples"
 			except subprocess.CalledProcessError as e:
				message += "Error (see error.log)"
				open('error.log', 'w').write(e.output)
			print message
		else:
			print "No model for " + table
		
if __name__=='__main__':
	if len(sys.argv) != 2:
		print "Indicate a root directory to work with"
		sys.exit(-1)
	process(sys.argv[1])
