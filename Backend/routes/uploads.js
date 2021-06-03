const router = require("express").Router();
const jwt = require("jsonwebtoken");
const User = require("../model/User");
const multer = require("multer");
const fs = require("fs");
const {
  createHmac,
  privateDecrypt,
  publicEncrypt,
  createPublicKey,
} = require("crypto");
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

router.post("/", upload.single("file"), async function (req, res, next) {
  let shoul_delete = true;
  let fd;
  try {
    fd = fs.openSync(req.file.path, "r+");
  } catch (error) {
    return res.send({ error: true, message: "no file found" });
  }

  try {
    //Read the whole file
    var file_buffer = Buffer.alloc(req.file.size);
    fs.readSync(fd, file_buffer, 0, req.file.size, 0);
    console.log(file_buffer.toString("hex"));
    //Extract parts from the file
    var file_mac = file_buffer.subarray(0, 32);
    var ciph_mac_key = file_buffer.subarray(32, 160);
    const extras = file_buffer.subarray(160, 304);
    const file_content = file_buffer.subarray(304);
    //Get decipher mac_key
    const mac_key = privateDecrypt(PRIVATE_KEY, ciph_mac_key);
    //Generate mac
    const hmac_obj = createHmac("SHA256", mac_key);
    hmac_obj.update(file_content);
    const hmac_copy = hmac_obj.digest();

    //Verify macs are the same (0 means they're the same; 1 or -1 means ineaquality)
    if (Buffer.compare(hmac_copy, file_mac) === 0) {
      //Search for the user and mark back up as true, then send response
      let decoded = jwt.verify(req.header("authorization"), PRIVATE_KEY);
      const user = await User.findOne({ _id: decoded._id });
      user.hasBackup = true;
      //Replace old mac in the file
      const new_mac_key = generateHmacKey()
      
      const new_key = publicEncrypt(
        "-----BEGIN PUBLIC KEY-----\n" +
          user.pub_key +
          "-----END PUBLIC KEY-----",
        new_mac_key
      );

      const new_mac = createHmac("SHA256", new_key);
      new_mac.update(file_content);
      file_buffer = Buffer.concat([
        new_mac.digest(),
        new_key,
        extras,
        file_content,
      ]);
      fs.writeSync(fd, file_buffer, 0, file_buffer.size, 0);
      fs.closeSync(fd);
      await user.save();
      return res.status(200).send({ success: true, message: "Success" });
    }
    //Close the file and delete
    fs.closeSync(fd);
    fs.unlinkSync(req.file.path);
    return res.send({
      error: true,
      message: "cannot prove integrity; the file has been disposed",
    });
  } catch (error) {
    console.log(error);
    fs.closeSync(fd);
    fs.unlink(req.file.path, (err) => {
      return res.send({
        error: true,
        message: "cannot prove integrity; the file has been dispossed",
      });
    });
  }
});

function generateHmacKey() {
  const key = await subtle.generateKey({
    name: 'HMAC',
    hash: 'SHA-256'
  }, true, ['sign', 'verify']);

  return key;
}

module.exports = router;
