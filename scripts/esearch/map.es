
curl -X DELETE "10.1.200.200:9200/icentral_test"
#curl -X PUT "10.1.200.200:9200/icentral_test" -H 'Content-Type: application/json' -d'{}'
curl -X PUT "10.1.200.200:9200/icentral_test" -H 'Content-Type: application/json' -d'
{
  "mappings":{
       "publication":{
                "properties": {
                    "abstract": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "attachement": {
                        "type": "nested",
                        "properties": {
                            "author": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "body": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "date": {
                                "type": "date",
                                "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                            },
                            "format": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "subject": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            }
                        }
                    },
                    "author": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "comment": {
                        "type": "nested",
                        "properties": {
                            "author": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "body": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "date": {
                                "type": "date",
                                "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                            },
                            "flag": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "subject": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            }
                        }
                    },
                    "contact": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "crtime": {
                        "type": "date",
                        "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                    },
                    "curator": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "curator_group": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "doi": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "id": {
                        "type": "long",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                       
                        
                    },
                    "imexid": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "issue": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "journal_id": {
                        "type": "long"
                    },
                    "journal_nlmid": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "journal_title": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "owner": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "pages": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "pmid": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "acttime": {
                        "type": "date",
                        "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                    },
                    "modtime": {
                        "type": "date",
                        "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                    },
                    "pubdate": {
                        "type": "date",
                        "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                    },
                    "reldate": {
                        "type": "date",
                        "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                    },
                    "score": {
                        "type": "nested",
                        "properties": {
                            "author": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "date": {
                                "type": "date",
                                "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                            },
                            "name": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "value": {
                                "type": "long"
                            }
                        }
                    },
                    "stage": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "state": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "timestamp": {
                        "type": "date",
                        "format": "yyyy-MM-dd HH:mm:ss.SSS"
                    },
                    "title": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "volume": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "xpubdate": {
                        "type": "date",
                        "format": "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis||yyyy-MM-dd HH:mm:ss.SSS"
                    },
                    "year": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    }
                }
            }
}
}
'