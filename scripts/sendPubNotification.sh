#!/bin/bash

QUEUE_DIR="/tmp/var/icentral/queue"

for FILE in $QUEUE_DIR/*.queue
do
	if [ -f "$FILE" ];
	then
		echo "Processing $FILE"
		. $FILE
		for ADDRESS in $EMAIL
		do
			/usr/sbin/sendmail -t <<END
To: $ADDRESS
From: dip@mbi.ucla.edu
Subject: IMEx Central Notification

Hello:

$ALERT 

notification alert for the following publication:

Title: $TITLE
Authors: $AUTHOR

IMEx Entry:
http://dip.doe-mbi.ucla.edu:55602/icentral/pubmgr?id=$ID

PubMed:
http://www.ncbi.nlm.nih.gov/pubmed?term=$PMID


Thank you for using IMEx Central
END
		done
	mv $FILE $QUEUE_DIR/sent/${$(basename "$FILE")/.queue/.sent}
	fi
done
