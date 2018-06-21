curl -X POST "10.1.216.103:9200/icentral/publication/_search?pretty" -H 'Content-Type: application/json' -d'

{"size":0,
  "aggregations": {
    "publication": {
       "nested" : {
           "path": "score" 
        },
        "aggregations": {
          "scr": {
             "terms": { "field" : "score.name.keyword", "size" : 500 }   
          }   
        }
    }
  }
}

'



