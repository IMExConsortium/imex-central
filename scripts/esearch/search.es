curl -X POST "10.1.200.200:9200/icentral_test/publication/_search?pretty" -H 'Content-Type: application/json' -d'

{"query":{
      "bool":{
       "must":{"match_all":{}},
    "filter":[]}},
    "sort":[

          {"_script" : {"type" : "string",
                        "script" : {
                             "lang": "painless", 
                             "source": "double m = 0; for(obj in params._source.score){  if( obj.name=='\''DSP.relevance'\''){ m = obj.value;}} return m"
                          },
                         "order" : "asc"
                        }
}
          ],
    "_source":["id"],
    "from": 0,
    "size": 25}
'



