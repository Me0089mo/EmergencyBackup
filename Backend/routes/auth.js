const router = require("express").Router();
const User = require("../model/User");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const { registerValidation, loginValidation } = require("../validation");
const { updatePasswordValidator } = require("../model/validation");

router.post("/register", async (req, res) => {
  //Data Validation
  const { error } = registerValidation(req.body);
  if (error) return res.status(400).send(error.details[0].message);
  //Check already registered
  const usrExist = await User.findOne({ email: req.body.email });
  if (usrExist) return res.status(406).send("User already exists");
  //Salt and Hash
  const salt = await bcrypt.genSalt(10);
  const hashed_password = await bcrypt.hash(req.body.password, salt);
  //Creating user
  const new_user = new User({
    name: req.body.name,
    email: req.body.email,
    password: hashed_password,
    pub_key: req.body.pub_key,
    hasBackup: false,
  });
  try {
    await new_user.save();
  } catch (err) {
    return res.status(500).send(err);
  }
  const user = await User.findOne({ email: req.body.email });
  const token = jwt.sign(
    {
      _id: user._id,
      name: user.name,
      user_pub_key: user.pub_key,
      server_pub_key: process.env.PUBLIC_KEY,
      hasBackup: user.hasBackup,
    },
    process.env.PRIVATE_KEY
  );
  return res.header("auth-token", token).send(token);
});

router.get("/download", async (req, res) => {
  console.log(req.get("Authorization"));
  const decoded = jwt.verify(req.get("Authorization"), process.env.PRIVATE_KEY);

  const userID = decoded._id;
  const dir = "uploads/" + userID;
  console.log("File being donwloaedd from:" + userID);
  return res.download(dir + "/backUpFile");
});

// For login request we should use POST method. Because our login data is secure which needs security. When use POST method the data is sent to server in a bundle. But in GET method data is sent to the server followed by the url like append with url request which will be seen to everyone.
// So For secure authentication and authorization process we should use POST method.
router.post("/login", async (req, res) => {
  //Data Validation
  const { error } = loginValidation(req.body);
  if (error) return res.status(400).send(error.details[0].message);

  //Check if registered
  const user = await User.findOne({ email: req.body.email });
  if (!user) return res.status(401).send("User not found");
  // Check password
  const passCorrect = await bcrypt.compare(req.body.password, user.password);
  if (!passCorrect) return res.status(401).send("Invalid password");

  const token = jwt.sign(
    {
      _id: user._id,
      name: user.name,
      user_pub_key: user.pub_key,
      server_pub_key: process.env.PUBLIC_KEY,
      hasBackup: user.hasBackup,
    },
    process.env.PRIVATE_KEY
  );
  return res.header("auth-token", token).send(token);
});

router.put("/update_password", async (req, res) => {
  const { error } = updatePasswordValidator(req.body);
  if (error) return res.status(400).send(error.details[0].message);
  console.log(req.header("authorization"));
  const decoded = jwt.verify(
    req.header("authorization"),
    process.env.PRIVATE_KEY
  );

  const userID = decoded._id;
});
module.exports = router;
