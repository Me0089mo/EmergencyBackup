const router = require("express").Router();
const User = require("../model/User");
const fs = require("fs");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const PRIVATE_KEY = fs.readFileSync("rsa.private", { encoding: "utf-8" });
const PUBLIC_KEY = fs.readFileSync("rsa.public", { encoding: "utf-8" });
const {
  updatePubKeyValidator,
  updateEmailValidator,
  updatePasswordValidator,
  registerValidation,
  loginValidation,
} = require("../model/validation");

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
      server_pub_key: PUBLIC_KEY,
      hasBackup: user.hasBackup,
    },
    PRIVATE_KEY
  );
  return res.header("auth-token", token).send(token);
});

// For login request we should use POST method. Because our login data is secure which needs security. When use POST method the data is sent to server in a bundle. But in GET method data is sent to the server followed by the url like append with url request which will be seen to everyone.
// So For secure authentication and authorization process we should use POST method.
router.post("/login", async (req, res) => {
  //Data Validation
  const { error } = loginValidation(req.body);
  if (error)
    return res
      .status(400)
      .send({ error: true, message: error.details[0].message });
  //Check if registered
  const user = await User.findOne({ email: req.body.email });
  if (!user)
    return res.status(401).send({ error: true, message: "User not found" });
  // Check password
  const passCorrect = await bcrypt.compare(req.body.password, user.password);
  if (!passCorrect)
    return res.status(401).send({ error: true, message: "Invalid password" });

  const token = jwt.sign(
    {
      _id: user._id,
      name: user.name,
      user_pub_key: user.pub_key,
      server_pub_key: PUBLIC_KEY,
      hasBackup: user.hasBackup,
    },
    PRIVATE_KEY
  );
  return res.header("auth-token", token).send(token);
});

router.put("/update_password", async (req, res) => {
  const { error } = updatePasswordValidator(req.body);
  if (error)
    return res
      .status(400)
      .send({ success: false, message: error.details[0].message });

  let decoded = "";
  try {
    decoded = jwt.verify(req.header("authorization"), PRIVATE_KEY);
  } catch (error) {
    return res.status(401).send({ error: true, message: "unauthorized" });
  }

  const user = await User.findOne({ _id: decoded._id });
  if (!user)
    return res.status(401).send({ success: false, message: "User not found" });

  // Check password
  const passCorrect = await bcrypt.compare(req.body.password, user.password);
  if (!passCorrect)
    return res.status(401).send({
      success: false,
      message: "Contraseña incorrecta",
    });

  const salt = await bcrypt.genSalt(10);
  const hashed_password = await bcrypt.hash(req.body.new_password, salt);
  user.password = hashed_password;
  await user.save();
  return res.status(200).send({ success: true, message: "Success" });
});

router.put("/update_email", async (req, res) => {
  const { error } = updateEmailValidator(req.body);
  if (error)
    return res
      .status(400)
      .send({ error: true, message: error.details[0].message });
  let decoded = "";
  try {
    decoded = jwt.verify(req.header("authorization"), PRIVATE_KEY);
  } catch (error) {
    return res.status(401).send({ error: true, message: "unauthorized" });
  }

  const user = await User.findOne({ _id: decoded._id });
  if (!user)
    return res.status(401).send({ error: true, message: "User not found" });
  user.email = req.body.email;
  await user.save();
  return res.status(200).send({ success: true, message: "Success" });
});

router.put("/update_key", async (req, res) => {
  const { error } = updatePubKeyValidator(req.body);
  if (error)
    return res
      .status(400)
      .send({ success: false, message: error.details[0].message });

  let decoded = "";
  try {
    decoded = jwt.verify(req.header("authorization"), PRIVATE_KEY);
  } catch (error) {
    return res.status(401).send({ error: true, message: "unauthorized" });
  }

  const user = await User.findOne({ _id: decoded._id });
  if (!user)
    return res.status(401).send({ success: false, message: "User not found" });

  // Check password
  const passCorrect = await bcrypt.compare(req.body.password, user.password);
  if (!passCorrect)
    return res.status(401).send({
      success: false,
      message: "Contraseña incorrecta",
    });

  user.pub_key = req.body.pub_key;
  await user.save();
  return res.status(200).send({ success: true, message: "Success" });
});

router.get("/has_backup", async (req, res) => {
  let decoded = "";
  try {
    decoded = jwt.verify(req.header("authorization"), PRIVATE_KEY);
  } catch (error) {
    return res.status(401).send({ error: true, message: "unauthorized" });
  }

  const user = await User.findOne({ _id: decoded._id });
  if (!user)
    return res.status(401).send({ success: false, message: "User not found" });

  return res.status(200).send(user.hasBackup);
});

module.exports = router;
