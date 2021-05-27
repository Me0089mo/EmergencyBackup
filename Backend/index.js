//Express server
const express = require("express");
const app = express();
const fs = require("fs");
const path = require("path");
const cors = require("cors");
const morgan = require("morgan");
//Enable CORS
app.use(cors());
//Enable HTTPS
var http = require("http");
var https = require("https");
var privateKey = fs.readFileSync("sslcert/server.key", "utf8");
var certificate = fs.readFileSync("sslcert/server.crt", "utf8");
var credentials = { key: privateKey, cert: certificate };
//Logs
const accesLogStream = fs.createWriteStream(
  path.join(__dirname, "access.log"),
  { flags: "a" }
);
app.use(morgan("dev", { stream: accesLogStream }));

const bodyParser = require("body-parser");
app.use(bodyParser.urlencoded({ extended: true }));
//Enviromental Variables
const dotenv = require("dotenv");
dotenv.config();
//Database conection
const mongoose = require("mongoose");
mongoose.connect(process.env.DB_CONECCTION, { useNewUrlParser: true });
const db = mongoose.connection;

db.on("error", console.error.bind(console, "connection error:"));

db.once("open", function () {
  console.log("Connection Successful!");
});

//Import routes
const authRoute = require("./routes/auth");
const downloadsRoute = require("./routes/download");
const uploadsRoute = require("./routes/uploads");
const { required } = require("joi");
//Middleware
app.use(express.json());
//Route middleware
app.use("/api/users", authRoute);
app.use("/api/upload", uploadsRoute);
var httpServer = http.createServer(app);
var httpsServer = https.createServer(credentials, app);

httpServer.listen(8080);
httpsServer.listen(8443);
