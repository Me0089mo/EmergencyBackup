const { string, bool, boolean } = require("joi");
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
    hasBackup:{
      type:Boolean,
      require:true
    },
    pub_key:{
      type:String,
      require:true
    }
  },
  { collection: "Users" }
);

module.exports = mongoose.model("User", userSchema);
