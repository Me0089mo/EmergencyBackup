//Express server
const express = require("express");
const app = express();
const fs = require('fs')
const path = require('path')
const cors = require("cors");
const morgan = require('morgan')

//Logs
const accesLogStream = fs.createWriteStream(path.join(__dirname,'access.log'),{flags:'a'})
app.use(morgan('dev',{stream:accesLogStream}))

const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));
//Enviromental Variables
const dotenv = require('dotenv');
dotenv.config();
//Database conection
const mongoose = require("mongoose");
mongoose.connect(process.env.DB_CONECCTION, {useNewUrlParser: true});
const db = mongoose.connection;


db.on("error", console.error.bind(console, "connection error:"));

db.once("open", function() {
  console.log("Connection Successful!");
});

//Import routes 
const authRoute = require('./routes/auth');
const { required } = require("joi");
//Middleware
app.use(express.json());
//Route middleware
app.use('/api/users',authRoute);


const port = process.env.port || 5000;
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
