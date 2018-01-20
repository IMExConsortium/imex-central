#!/usr/bin/python

from pyArango.connection import *
conn = Connection(arangoURL='http://128.97.39.203:8529',username="icentral", password="444ls444")

print conn.hasDatabase('icentral')
