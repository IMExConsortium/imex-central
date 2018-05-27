curl -X POST "10.1.200.200:9200/icentral_test/publication/_search" -H 'Content-Type: application/json' -d'
{"query":{"bool":{"must":{"match_all":{}},"filter":[ {"term":{"stage":"queue"}}]}},"sort":[{"id":"asc"}],"_source":["id"],"from": 0,"size": 25}
'


