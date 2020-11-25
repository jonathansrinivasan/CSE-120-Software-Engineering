const url = "mongodb+srv://dkumar7:WeBetterWinThisShit@cse120appcluster-buzvh.mongodb.net/test?retryWrites=true&w=majority"
var scripts = require('./scripts');

var data = {
    "summary":["yeild","profit"],
    "date_range":["2002-01-01","2020-03-01"]
}

//scripts.findDocuments(url,data);

var tagOne = {
    "id": "1201-01",
    "summary": "yeild was 70% and profit was $800",
    "latitude": "66.75",
    "longitude": "-179.75",
    "climate_tag":"ET",
    "Date": "2012-07-29"
}
//scripts.insertTag01Documents(url,tagOne);
//scripts.insertPaidDocuments(url,tagOne);
var user = {
    "username": 'testuser1045',
    "password" : 'testPassword',
    "myArticles": ['1014-01','1014-02'],
    "subscribedArticles": ['1024-02']
}

//scripts.logIn(url, user);

var sec = {
    "username":'dkumar7',
    "doc_id":'1357841'
}
scripts.findSection(url, sec);
