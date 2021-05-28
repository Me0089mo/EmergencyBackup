const router = require("express").Router();
const jwt = require("jsonwebtoken");
const fs = require("fs");
const PRIVATE_KEY = fs.readFileSync("rsa.private", { encoding: "utf-8" });

router.get("/", async (req, res) => {
  let decoded = "";
  try {
    decoded = jwt.verify(req.header("authorization"), PRIVATE_KEY);
  } catch (error) {
    return res.status(401).send({ error: true, message: "unauthorized" });
  }
  const userID = decoded._id;
  const dir = "uploads/" + userID;

  if (!fs.existsSync(dir)) {
    return res.status(404).send({ error: true, message: "no files found" });
  }

  return res.send({ files: fs.readdirSync(dir) });
});

router.get("/:filename", async (req, res) => {
  let decoded = "";
  try {
    decoded = jwt.verify(req.header("authorization"), PRIVATE_KEY);
  } catch (error) {
    return res.status(401).send({ error: true, message: "unauthorized" });
  }
  const userID = decoded._id;
  const dir = "uploads/" + userID;
  // if (!fs.existsSync(dir)) {
  //   return res.status(404).send({ error: true, message: "no files found" });
  // }
  if (!fs.existsSync(`${dir}/${req.params.filename}`)) {
    return res.status(404).send({ error: true, message: "no files found" });
  }
  return res.download(`${dir}/${req.params.filename}`, (err) => {});
});

module.exports = router;
