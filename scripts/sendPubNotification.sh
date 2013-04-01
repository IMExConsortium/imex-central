#!/bin/bash

QUEUE_DIR="/var/icentral/queue"

for FILE in $QUEUE_DIR/*.queue
do
    if [ -f "$FILE" ];
    then
	echo "Processing $FILE"
	. $FILE
	for ADDRESS in $EMAIL
	do
            case $MODE in
                "RECORD-UPDATE" )                                    
	            /usr/sbin/sendmail -t <<END1
To: $ADDRESS
From: dip@mbi.ucla.edu
Subject: Imex Central Notification

Hello:

$ALERT 

Notification alert for the following publication:

Title: $TITLE
Authors: $AUTHOR

IMEx Entry:
http://dip.doe-mbi.ucla.edu:55602/icentral/pubmgr?id=$ID

PubMed:
http://www.ncbi.nlm.nih.gov/pubmed?term=$PMID

Thank you for using Imex Central
END1
                    ;;
                
                "NEWS_ITEM" )
                    /usr/sbin/sendmail -t <<END2
To: $ADDRESS
From: dip@mbi.ucla.edu
Subject: Imex Central News Announcement

New announcement has been posted to Imex Central:

${NEWS_ITEM}

Thank you for using IMEx Central
END2
                    ;;
                
                "NEW_ACCOUNT" )
                    /usr/sbin/sendmail -t <<END3
To: $ADDRESS
From: dip@mbi.ucla.edu
Subject: Imex Central New Account Created

New Imex Central account has been created:

      Login: ${NEW_LOGIN}      
 First Name: ${FIRST_NAME}
   LastName: ${LAST_NAME}
Affiliation: ${AFFILIATION}
      Email: ${EMAIL}    

END3
                    ;;
            esac
		done
	mv $FILE $QUEUE_DIR/sent/$(basename $FILE)
	fi
done
