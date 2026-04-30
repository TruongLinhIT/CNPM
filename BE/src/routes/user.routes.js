const express = require('express');
const { getAllUsers, createUser, updateUser, deleteUser } = require('../controllers/user.controller');
// Since JWT is removed, we might not use the standard authenticate middleware
// or we need to refactor it. For now, let's keep it simple.

const router = express.Router();

router.get('/', getAllUsers);
router.post('/', createUser);
router.put('/:id', updateUser);
router.delete('/:id', deleteUser);

module.exports = router;
