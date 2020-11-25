//const http = require('http');
const express = require('express');
const bodyParser = require('body-parser')
const app = express();
const mongoClient = require('mongodb').MongoClient;

const hostname = '127.0.0.1';
const port = 3000;

const url = "mongodb+srv://dkumar7:WeBetterWinThisShit@cse120appcluster-buzvh.mongodb.net/test?retryWrites=true&w=majority";

app.use(express.json());
app.use(bodyParser.json());

// mongoClient.connect(url, (err, db) => {
//   if (err) {
//     console.log("error connecting to mongo client")
//   } else {
// class ClassName {
//   constructor() {
//
//   }
// }
//     const myDb = db.db('TestDB')
//     const documents = myDb.collections('documents')
//     const user = myDb.collection('users')
//
//     app.post('/findDocuments', (req, res) => {
//       if (req != null) {
//         console.log('Got body: ', req.body);
//       } else {
//         res.sendStatus(400);
//       }
//
//       // save the stuff below for after we get the query
//       // var query = {/*Insert Query*/}
//       // if (result != null) {
//       //
//       // } else {
//       //   res.status(400).send()
//       // }
//     })
//   }
// });

app.post('/login', (req, res) => {
  let data = {'username':'admin', 'password':'admin'};
  if (req != null) {
    console.log('Got body', req.body);
    if (data.username == req.body.username && data.password == req.body.password) {
      res.send(JSON.stringify(data));
    } else {
      res.sendStatus(400);
    }
  } else {
    res.sendStatus(400);
  }
});

app.post('/findDocuments', (req, res) => {
  let data = {'test':'works'};
  if (req != null) {
    console.log('Got body', req.body);
    res.send(JSON.stringify(data));
  } else {
    res.sendStatus(400);
  }
});

app.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}`);
});

// const server = http.createServer((req, res) => {
//   res.statusCode = 200;
//   res.setHeader('Content-Type', 'text/plain');
//   res.end('Hello World');
// });

// server.listen(port, hostname, () => {
//   console.log(`Server running at http://${hostname}:${port}/`);
// });
