
#curl -X DELETE "10.1.200.200:9200/icentral_test"

curl -X PUT "10.1.216.103:9200/_all/_settings" -H 'Content-Type: application/json' -d'
{
  "index.max_result_window" : "200000"
}
'
