curl -X POST "10.1.200.200:9200/icentral_test/publication/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query":   { 
    "bool":{
       "must": {"match_all":{}},
       "filter": []
    }  
  },
 "_source":["id","score"],
 "from": 0,
 "size":150,
 "sort":{"_script" : {
         "type" : "string",
         "script" : {
                "lang": "painless",
                "source": "double m = 0; for(obj in params._source.score){ if( obj.name=='\''DSP.relevance'\''){ m = obj.value;}} return m"
                
            
         },
            "order" : "desc"
        }
   }
} 
'


# if( doc.containsKey('\''score'\'')){ return 1} else {return 0}