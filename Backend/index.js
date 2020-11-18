//Express server
const express = require("express");
const app = express();
const cors = require("cors");
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
//Middleware
app.use(express.json());
//Route middleware
app.use('/api/users',authRoute);


const port = process.env.port || 5000;
app.use(express.json());
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
