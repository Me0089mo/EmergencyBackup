const router = require("express").Router();
const jwt = require("jsonwebtoken");
const bcrypt = require("bcryptjs");

router.post("/", upload.single("file"), function (req, res, next) {
  const decoded = jwt.verify(req.body.auth, process.env.PRIVATE_KEY);
  const userID = decoded._id;
  console.log("File being uploaded from:" + userID);

  //console.log(req.file.buffer.toString("utf-8", 0, 32))
  //console.log(req.file.buffer.toString("utf-8",0,33))
});

module.exports = router;

app.get("/", (req, res) => {
  const decoded = jwt.verify(req.body.auth, process.env.PRIVATE_KEY);
  const userID = decoded._id;
  const dir = "uploads/" + user;
  console.log("File being donwloaedd from:" + userID);
  res.download("dir/*");
  //   picModel.find({ _id: req.params.id }, (err, data) => {
  //     if (err) {
  //       console.log(err);
  //     } else {
  //       var path = __dirname + "/public/" + data[0].picspath;
  //     }
  //   });
});
