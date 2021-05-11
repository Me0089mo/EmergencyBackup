
const Joi = require("joi");

const registerValidation = (data) => {
  const schema = Joi.object({
    name: Joi.string().min(2).required(),
    email: Joi.string().email().required(),
    pub_key: Joi.string().required(),
    password: Joi.string().min(6).max(255).required(),
  });
  return schema.validate(data);
};

const loginValidation = (data) => {
  const schema = Joi.object({
    email: Joi.string().email().required(),
    password: Joi.string().min(6).max(255).required(),
  });
  return schema.validate(data);
};

const updateEmailValidator = (data) => {
  const schema = Joi.object({
    email: Joi.string().email().required(),
  });
  return schema.validate(data);
};

const updatePasswordValidator = (data) => {
  const schema = Joi.object({
    password: Joi.string().min(6).max(255).required(),
    new_password: Joi.string().min(6).max(255).required(),
//    confirmation: Joi.string().min(6).max(255).required(),
  });
  return schema.validate(data);
};

module.exports.updateEmailValidator = updateEmailValidator;
module.exports.updatePasswordValidator = updatePasswordValidator;
module.exports.registerValidation = registerValidation;
module.exports.loginValidation = loginValidation;
