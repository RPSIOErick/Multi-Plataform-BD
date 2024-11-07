const express = require("express");
const app = express();
const handlebars = require("express-handlebars").engine;
const bodyParser = require("body-parser");

var PORT = 8081;

const {
  initializeApp,
  applicationDefault,
  cert,
} = require("firebase-admin/app");

const {
  getFirestore,
  Timestamp,
  FieldValue,
} = require("firebase-admin/firestore");

const serviceAccount = require("./serviceAccount.json");

initializeApp({
  credential: cert(serviceAccount),
});

const db = getFirestore();

// Registra o helper "eq" para comparar valores
app.engine('handlebars', handlebars({
  helpers: {
      eq: function (a, b) {
          return a === b;
      }
  }
}));

app.set('view engine', 'handlebars');

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

app.use(express.static('public'));
app.use('/node_modules', express.static('node_modules'));

module.exports = {

    db,
    app

}