const router = require("express").Router();
const jwt = require("jsonwebtoken");
const bcrypt = require("bcryptjs");
const multer = require("multer");
const fs = require("fs");
const { createHmac, privateDecrypt, publicEncrypt } = require("crypto");
const { create } = require("domain");
const { stringify } = require("querystring");
const PRIVATE_KEY = fs.readFileSync("rsa.private", { encoding: "utf-8" });

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, "uploads");
  },
  filename: function (req, file, cb) {
    let decoded;
    try {
      decoded = jwt.verify(req.header("authorization"), PRIVATE_KEY);
      const user = decoded._id;
      const dir = "uploads/" + user;

      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir);
      }
      cb(null, "/" + user + "/" + file.originalname);
    } catch (error) {
      cb(error, false);
    }
  },
});

//var storage = multer.memoryStorage()

var upload = multer({
  fileFilter: (req, _file, cb) => {
    try {
      jwt.verify(req.header("authorization"), PRIVATE_KEY);
      cb(null, true);
    } catch (error) {
      cb(error, false);
    }
  },
  storage: storage,
});

router.post("/", upload.single("file"), function (req, res, next) {
  let shoul_delete = true;
  let fd;
  try {
    fd = fs.openSync(req.file.path, "r");
  } catch (error) {
    retunr;
  }
  //Read the whole file
  const file_buffer = Buffer.alloc(req.file.size);
  fs.readSync(fd, file_buffer, 0, req.file.size, 0);
  //Extract parts from the file
  const file_mac = file_buffer.subarray(0, 32);
  const ciph_mac_key = file_buffer.subarray(32, 160);
  const file_content = file_buffer.subarray(304);
  //Get decipher mac_key
  const mac_key = privateDecrypt(PRIVATE_KEY, ciph_mac_key);
  //Generate mac
  const hmac_obj = createHmac("SHA256", mac_key);
  hmac_obj.update(file_content);
  const hmac_copy = hmac_obj.digest();
  //Close the file before deletion
  fs.closeSync(fd);
  //Verify macs are the same
  if (Buffer.compare(hmac_copy, file_mac) === 0) {
    res.send({ error: false, message: "success" });
  } else {
    fs.unlinkSync(req.file.path);
    res.send({
      error: true,
      message: "cannot prove integrity; the file has been dispossed",
    });
  }
});

module.exports = router;
