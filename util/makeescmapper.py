import sys
import os

scriptdir = os.path.dirname(os.path.abspath(__file__))

with open(sys.argv[1], "r") as infile, open(sys.argv[2], "w") as outfile, open(scriptdir + "/EStreamControlMessageMap.java.input", "r") as srcfile:
	srcparts = srcfile.read().split("ADDSTUFFHERE")
	outfile.write(srcparts[0])
	weareinside = False
	for line in infile:
		l = line.strip()
		if weareinside:
			if "}" in l:
				weareinside = False
				outfile.write(srcparts[1]);
				break;
			parts = l.split(" = ")
			if "LAST_SETUP_MESSAGE" in parts[0]:
				continue
			ending = "Msg"
			if "QuitRequest" in parts[0]:
				ending = ""
			print("\t\tmap(" + parts[0] + "_VALUE, " + parts[0].replace("k_EStreamControl", "C") + ending + ".class);", file=outfile)
		elif "enum EStreamControlMessage" in l:
			weareinside = True
