/*
    Author: Devanshu Kumar and Keerthana Madadi
    This script contains all the functions to communicate with the MongoDB cluster and other essential functions for back-end process
*/
const MongoClient = require('mongodb').MongoClient;
function findDocuments(url, d){
    MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true}, (err, db) => {
        if(err) {
           console.log(err);
           process.exit(0);
        }
        var dbo = db.db('TestDB');
        var query = {"summary":{"$regex": d.summary[0],"$regex": d.summary[1]},"Date":{ $gte: new Date(d.date_range[0]), $lt:new Date(d.date_range[1])}}
        dbo.collection("documents").find(query).toArray(function(err, res){
            if (err) throw err;
            console.log(res);
            db.close();
        });
    });
}
module.exports.findDocuments = findDocuments;

function insertUsers(url, d){
    MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true}, (err, db) => {
        if(err) {
           console.log(err);
           process.exit(0);
        }
        var dbo = db.db('TestDB');
        data = {
            "username": d.username,
            "password" : d.password,
            "myArticles": d.myArticles,
            "subscribedArticles": d.subscribedArticles,
        };
        dbo.collection("users").insertOne(data, function(err, res){
            if (err) throw err;
            console.log("User inserted");
            db.close();
        });
    });
}
module.exports.insertUsers = insertUsers;

function insertTag01Documents(url, d){
    MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true}, (err, db) => {
        if(err) {
           console.log(err);
           process.exit(0);
        }
        var dbo = db.db('TestDB');
        data = {
            "id": d.id,
            "summary": d.summary,
            "latitude": d.latitude,
            "longitude": d.longitude,
            "climate_tag": d.climate_tag,
            "Date" : new Date(d.Date)
        };
        dbo.collection("documents").insertOne(data, function(err, res){
            if (err) throw err;
            console.log("Document inserted");
            db.close();
        });
    });
}
module.exports.insertTag01Documents = insertTag01Documents;

function logIn(url, d){
    MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true}, (err, db) => {
        if(err) {
           console.log(err);
           process.exit(0);
        }
        var dbo = db.db('TestDB');
    
        var user = {
            "username": d.username,
            "password": d.password
        };

        dbo.collection("users").findOne(user,function(err, res){
            if (err) throw err;
            if(res == null){
                console.log("Cannnot login\nUsername or password is incorrect");
            }
            else{
                console.log("Logged In");
            }
            db.close();
        });
    });
}
module.exports.logIn = logIn;

function insertPaidDocuments(url, d){
    MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true}, (err, db) => {
        if(err) {
           console.log(err);
           process.exit(0);
        }
        var dbo = db.db('TestDB');
        //var json = JSON.parse(d);
        data = {
            "id": d.id,
            "summary": d.summary,
            "latitude": d.latitude,
            "longitude": d.longitude,
            "climate_tag": d.climate_tag,
            "Date" : new Date(d.Date)
        };
        console.log(d);
        dbo.collection("documents").insertOne(d, function(err, res){
            if (err) throw err;
            console.log("Document inserted");
            db.close();
        });
    });
}
module.exports.insertPaidDocuments = insertPaidDocuments;

function findSection(url, d){
    MongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true}, (err, db) => {
        if(err) {
           console.log(err);
           process.exit(0);
        }
        
        var dbo = db.db('TestDB');
        var section;
        dbo.collection("users").findOne({"username": d.username}, function(err, res){
            console.log("inside");
            if(res == null || res.subscribedArticles == null){
                console.log("Articles not found");
            }
            else{
                dbo.collection("users").findOne({"username": d.username, "subscribedArticles.id" : d.doc_id},function(err, res){
                    
                    if(res == null){
                        console.log("Sections not found");
                    }
                    else{
                        
                        var doc=res.subscribedArticles;
                        for(var i=0; i<doc.length;i++)
                        {//console.log(res.subscribedArticles[i].id+" "+d.doc_id);
                            if(res.subscribedArticles[i].id == d.doc_id){
                                console.log(res.subscribedArticles[i].section);
                                section = res.subscribedArticles[i].section;
                            }
                            
                        }
                        
                        dbo.collection("documents").find({"header.id":d.doc_id}).toArray(function(err, res1){
                            console.log(res1);
                            if (res1 != null){
                                var data = {
                                    "sections" : section,
                                    "doc_info" : res1
                                }
                                console.log(data);
                                //res1.status(200).send(data);
                            }
                            else{
                              //res1.send(JSON.stringify(["no data"]));
                            }
                        });
                    }

                    //sections[], docs
                });
            }
        });
    });
}
module.exports.findSection = findSection;