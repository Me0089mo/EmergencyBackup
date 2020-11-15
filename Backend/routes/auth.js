const router = require("express").Router();
const User = require("../model/User");


router.post("/register", async (req, res) => {
  const user = new User({
    email: req.body.email,
    password: req.body.password,
    securityQuestion: req.body.securityQuestion,
    answer: req.body.answer,
  });
  try {
    // const savedUser = await user.save();
    res.send("succes?")
  } catch (err) {
    res.status(400).send(err);
  }
});

module.exports = router;