const router = require("express").Router();
const jwt = require("jsonwebtoken");
const bcrypt = require("bcryptjs");
const multer = require("multer");
const fs = require("fs");
const {createHmac,} = require("crypto");

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, "uploads");
  },
  filename: function (req, file, cb) {
    const decoded = jwt.verify(req.body.auth, process.env.PRIVATE_KEY);
    const user = decoded._id;
    const dir = "uploads/" + user;
    console.log(typeof file.buffer);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir);
    }
    cb(null, "/" + user + "/" + file.originalname);
  },
});

//var storage = multer.memoryStorage()

var upload = multer({ storage: storage });

router.post("/", upload.single("file"), function (req, res, next) {
  const decoded = jwt.verify(req.body.auth, process.env.PRIVATE_KEY);
  const userID = decoded._id;
  console.log("File being uploaded from:" + userID);

  //console.log(req.file.buffer.toString("utf-8", 0, 32))
  //console.log(req.file.buffer.toString("utf-8",0,33))
});

function calculateHmac(){

}

function readHmac(file){
  const inputStream = fs.createReadStream(file)
  let readBytes = inputStream.read(32)
  console.log(readBytes)
  readBytes = inp
}

function verifyHmac(){

}
module.exports = router;
