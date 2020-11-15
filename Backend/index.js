//Express
const express = require("express");
const app = express();
const cors = require("cors");
const dotenv = require('dotenv');
const mongoose = require("mongoose");
//Import routes 
const authRoute = require('./routes/auth');
dotenv.config();

//Middleware
app.use(express.json());
//Route middleware
app.use('/api/users',authRoute);

const port = process.env.port || 5000;
app.use(express.json());
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
