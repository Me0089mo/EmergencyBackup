const router = require("express").Router();
const User = require("../model/User");
const bcrypt  = require("bcryptjs")
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
    securityQuestion:req.body.securityQuestion,
    answer:req.body.answer,
  });
  try {
    const savedUser =  await user.save();
    res.send(savedUser);
  } catch (err) {
    res.status(400).send(err);
  }
});




module.exports = router;