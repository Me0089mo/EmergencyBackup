const router = require("express").Router();
const User = require("../model/User");
const bcrypt  = require("bcryptjs")
const jwt = require("jsonwebtoken")
const {registerValidation, loginValidation} = require("../validation")

router.post("/register", async (req, res) => {
  //Data Validation
  const {error} = registerValidation(req.body);
  if (error)return res.status(400).send(error.details[0].message);
  //Check already registered
  const usrExist = await User.findOne({email:req.body.email})
  if(usrExist) return res.status(400).send('User already exists');
  //Salt and Hash
  const salt = await bcrypt.genSalt(10);
  const hashed_password = await bcrypt.hash(req.body.password,salt);
  //Creating user
  const user = new User({
    name:req.body.name,
    email:req.body.email,
    password:hashed_password,
    hasBackup:false
  });
  try {
    const savedUser =  await user.save();
    res.send('Success on register');
  } catch (err) {
    console.log(req)
    console.log(err)
    res.status(400).send(err);
  }
});

// For login request we should use POST method. Because our login data is secure which needs security. When use POST method the data is sent to server in a bundle. But in GET method data is sent to the server followed by the url like append with url request which will be seen to everyone.

// So For secure authentication and authorization process we should use POST method.
router.post("/login", async (req, res) => {
  console.log('login attempt')
  console.log(req.body);
  //Data Validation
  const {error} = loginValidation(req.body);
  if (error)return res.send(error.details[0].message);
  //Check if registered
  const user = await User.findOne({email:req.body.email})
  if(!user) return res.send('User not found');
  // Check password
  const passCorrect = await bcrypt.compare(req.body.password,user.password);
  if(!passCorrect) return res.send('Invalid password');
  const token  = jwt.sign({_id:user._id,name:user.name, hasBackup:user.hasBackup},process.env.PRIVATE_KEY)
  return res.header('auth-token',token).send(token);
});

module.exports = router;
