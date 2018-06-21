curl -X POST "10.1.216.103:9200/icentral/publication/_search?pretty" -H 'Content-Type: application/json' -d'

{"query":{
      "bool":{
       "must":{"match_all":{}},
    "filter":[]}},
    "sort":[

          {"_script" : {"type" : "number",
                        "script" : {
                             "lang": "painless", 
                             "source": "double m = 0; for(obj in params._source.score){  if( obj.name=='\''DSP.relevance'\''){ m = obj.value;}} return m"
                          },
                         "order" : "desc"
                        }
}
          ],
    "_source":["id"],
    "from": 0,
    "size": 25}
'



