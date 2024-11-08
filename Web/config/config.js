// Importações necessárias
const express = require("express");
const app = express();
const handlebars = require("express-handlebars").engine;
const bodyParser = require("body-parser");
const session = require("express-session");

// Importações do Firebase Admin SDK (para Firestore)
const { initializeApp: initializeAdminApp, cert } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");

// Configuração do Firebase Admin SDK para Firestore
const serviceAccount = require("./serviceAccount.json");
initializeAdminApp({
  credential: cert(serviceAccount),
});
const db = getFirestore();

// Importações do Firebase Client SDK (para autenticação)
const { initializeApp } = require("firebase/app");
const { getAuth } = require("firebase/auth");

const firebaseClientConfig = {
  apiKey: "AIzaSyAtdwhmxy0HJ6p3BMSGIS6lEM9y9HDI3rc",
  authDomain: "servicos-e7079.firebaseapp.com",
  projectId: "servicos-e7079",
  storageBucket: "servicos-e7079.firebasestorage.app",
  messagingSenderId: "794464998142",
  appId: "1:794464998142:web:8adb8bd46d4eae3cc44baf"
};

// Inicializar o Firebase Client SDK para autenticação
const firebaseClientApp = initializeApp(firebaseClientConfig);
const firebaseAuth = getAuth(firebaseClientApp);

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
app.use(session({
  secret: "gertrudes",
  resave: false,
  saveUninitialized: true,
}));

module.exports = {
  db,
  app,
  firebaseAuth // Exporta o auth do Firebase Client SDK
};
