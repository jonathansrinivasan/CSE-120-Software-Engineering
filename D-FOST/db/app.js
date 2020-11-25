const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const mongoClient = require('mongodb').MongoClient;
const crypto = require('crypto');
const mongoose = require('mongoose');
app.use(bodyParser.urlencoded({ extended: true }));

const url = "mongodb+srv://dkumar7:WeBetterWinThisShit@cse120appcluster-buzvh.mongodb.net/test?retryWrites=true&w=majority";

app.use(express.json());

mongoClient.connect(url, {useNewUrlParser: true, useUnifiedTopology: true}, (err, db) =>{
    if (err){
        console.log("error connecting to mongo client");
    }
    else{

        const myDb = db.db('TestDB');
        const documents = myDb.collection('documents');
        const user = myDb.collection('users');



        app.post('/insertCreatedDoc', (req, res) => {
          console.log(req.body);
          function setIDNum(id){
              req.body.header.id = (id + 1).toString();
              req.body.header.location.longitude = (req.body.header.location.longitude).toString();
              req.body.header.location.latitude = (req.body.header.location.latitude).toString();
              console.log(req.body.header.id);
              var data = {
                "header":{
                    "id": req.body.header.id,
                    "title": req.body.header.title,
                    "username": req.body.header.username,
                    "keywords": req.body.header.keywords,
                    "location":{
                        "latitude": req.body.header.location.latitude,
                        "longitude": req.body.header.location.longitude,
                        "climate_tag": req.body.header.location.climate_tag
                    },
                    "date": new Date(req.body.header.date)
                },
                "sections": req.body.sections
            }
            documents.insertOne(data, (err, result) =>{
                console.log('succesfully added document')
            })
            user.updateOne({"username": req.body.header.username}, {$push:{myArticles: req.body.header.id}}, function(err, result1){
                console.log('succesfully added  to user list')
                res.status(200).send(JSON.stringify({"insertStatus": "SUCCESS"}))
            })
          }
          documents.countDocuments({}, function(err, result) {
            if (err) {
              res.send(err);
            } else {
                setIDNum(result);
            }
          });
        })

        // just for some personal stuff (TONY)
        app.post('/getUserInfo', (req, res) => {
          user.findOne({"username": req.body.username}, function(err, result) {
            if (err) throw err;
            if (result != null) {
              console.log(result);
              res.status(200).send(JSON.stringify(result));
            } else {
              console.log('could not find user');
            }
          });
        });

        app.post('/sectionRequest', (req, res) => {
          console.log("in sectionRequest")
          user.findOne({"username": req.body.username}, function(err, result){
            console.log(result.subscribedArticles)
              if(result.subscribedArticles == null){
                var data = {
                    "id": req.body.doc_id,
                    "section":[req.body.doc_section_id]
                }
                user.updateOne({"username": req.body.username}, {$push:{subscribedArticles: data}},function(err, result1){
                    console.log('succesfully added  to user subscribed list new')
                    res.status(200).send(JSON.stringify({"insertStatus": "SUCCESS"}))
                })
              }
              else{
                user.findOne({"username": req.body.username, "subscribedArticles.id":req.body.doc_id}, function(err, result){
                    if(result == null){
                        var data = {
                            "id": req.body.doc_id,
                            "section":[req.body.doc_section_id]
                        }
                        user.updateOne({"username": req.body.username}, {$push:{subscribedArticles: data}},function(err, result1){
                            console.log('succesfully added  to user subscribed list new')
                            res.status(200).send(JSON.stringify({"insertStatus": "SUCCESS"}))
                        })
                    }
                    else{
                        user.updateOne({"username": req.body.username, "subscribedArticles.id":req.body.doc_id}, {$push:{"subscribedArticles.$[].section": req.body.doc_section_id}},function(err, result1){
                            console.log('succesfully added  to user subscribed list')
                            res.status(200).send(JSON.stringify({"insertStatus": "SUCCESS"}))
                        })
                    }
                })
            }
          })
        })

        app.post('/sectionRequestRedirect', (req, res) => {
          // TODO: IMPLEMENT
          console.log('called Section Request Redirect')
          var data = {}
          // first find the subscribed articles
          user.findOne({"username": req.body.username}, function(err, result) {
            if (err) throw err;
            if (result != null) {
              data['subscribedArticles'] = result.subscribedArticles;
              documents.findOne({"header.id": req.body.doc_id}, function(err, result) {
                if (err) throw err;
                if (result != null) {
                  data['docInfo'] = result;
                  res.status(200).send(JSON.stringify(data));
                }
              })
            }
          })
        })

        app.post('/findMyDocuments', (req, res) => {
          documents.find({"header.username": req.body[0].username}).toArray(function(err, result) {
            if (result != null && result.length) {
              console.log(result);
              res.status(200).send(JSON.stringify(result));
            } else {
              res.send(JSON.stringify(["no data"]));
            }
          });
        });


        app.post('/findDocuments', (req,res) =>{
          console.log(req.body[0]);
            var key = req.body[0].keywords;
            var dat = req.body[0].date;
            var tag = req.body[0].climate_tag;
            var data;
            var q1 = {};
            var query = {"header.keywords": { $in: key },"header.date":{ $gte: new Date(dat[0]), $lt:new Date(dat[1])}, "header.location.climate_tag":{"$regex":tag}}
            documents.find(query).toArray(function(err, result){
                if (result != null && result.length){
                    data = result;
                    console.log(result);
                    res.status(200).send(data);
                }
                else{
                  console.log('no result')
                  res.send(JSON.stringify(["no data"]));
                }
            })
        })

        app.post('/getAllowedUserDocs', (req,res)=> {
          var data = {"username": req.body[0].username}
          console.log(req.body)
          user.findOne({"username": data.username}, function(err, result) {
            if (err) throw err;
            if (result != null) {
              console.log('found user');
              console.log(JSON.stringify(result.subscribedArticles))
              console.log(result.subscribedArticles)
              res.status(200).send(JSON.stringify(result.subscribedArticles));
            }
          })
        })

        app.post('/register', (req, res) =>{
            var salt = crypto.randomBytes(16).toString('hex');
            var hash = crypto.pbkdf2Sync(req.body.password, salt, 1000, 64, `sha512`).toString(`hex`);
            data = {
                "username": req.body.username,
                "password" : hash,
                "Name": req.body.name,
                "myArticles": [],
                "subscribedArticles": [],
                "salt": salt
            };

            const query = {username: data.username}

            user.findOne({username: req.body.username}, (err, result)=>{
                if(result != null){
                    console.log('account exists')
                    data["access"] = "DENIED";
                    res.send(JSON.stringify(data));
                }
                else{
                    user.insertOne(data, (err, result) =>{
                        console.log('succesfully registered')
                        res.status(200).send(data)
                    })
                }
            })
        })

        app.post('/login', (req, res) =>{
            console.log(req.body);
            var data = {
                "username": req.body.username,
                "password": req.body.password
            }
            user.findOne({"username": data.username} , function(err, result){
                if (err) throw err;
                if(result == null){
                    console.log("Cannnot login\nUsername or password is incorrect");
                }
                else{
                    var hash_input = crypto.pbkdf2Sync(req.body.password, result.salt, 1000, 64, `sha512`).toString(`hex`);
                    if(hash_input == result.password){
                        data["access"] = "GRANTED";
                        console.log("Logged In");
                        res.send(JSON.stringify(data));
                    }
                    else{
                      console.log("Not logged in");
                        data["access"] = "DENIED";
                        res.send(JSON.stringify(data));
                    }
                }
            });
        })
    }
})


app.listen(3000, () => {
    console.log("Listening on port 3000...")
})
