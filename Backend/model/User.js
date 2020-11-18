const { string } = require("joi");
const mongoose = require("mongoose");

const userSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: true,
    },
    email: {
      type: String,
      trim: true,
      lowercase: true,
      unique: true,
      required: true,
    },
    password: {
      type: String,
      required: true,
      min: 6,
      max: 255,
    },
    securityQuestion: {
      type: String,
      required: true,
    },
    answer: {
      type: String,
      required: true,
    },
  },
  { collection: "Usrs" }
);

module.exports = mongoose.model("User", userSchema);
