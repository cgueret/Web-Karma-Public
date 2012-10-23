#!/usr/bin/python2

import sys
import os
import subprocess

def process(dir):
	tmpFile = "/tmp/everything.nt"
	
	# Clear up tmp file
	if os.path.isfile(tmpFile):
		os.remove(tmpFile)
			
	# Gather all the data
	rootTriples = dir + "/triples/"
	tables = [f for f in os.listdir(rootTriples) if os.path.isfile(rootTriples + f)]
	for table in tables:
		triplesFile = rootTriples + table
		if os.path.isfile(triplesFile):
			message = "Add content of %s -> " % table
			try:
				sysCall = "rapper -q -i guess -o ntriples %s >> %s" % (triplesFile, tmpFile)
				subprocess.check_output(sysCall, shell=True)
				
				sysCall = "cat %s | wc -l" % tmpFile
				processOutput = subprocess.check_output(sysCall, shell=True)
				message += processOutput[:-1] + " triples"
 			except subprocess.CalledProcessError as e:
				message += "Error (see error.log)"
				open('error.log', 'w').write(e.output)
			print message
		else:
			print "Error opening " + table
		
		
if __name__=='__main__':
	if len(sys.argv) != 2:
		print "Indicate a root directory to work with"
		sys.exit(-1)
	process(sys.argv[1])
