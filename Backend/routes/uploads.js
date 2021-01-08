const router = require("express").Router();
var multer  = require('multer');
var upload = multer({ dest: 'uploads/' })


router.post('/', upload.single('avatar'), function (req, res, next) {
  // req.file is the `avatar` file
  // req.body will hold the text fields, if there were any
})