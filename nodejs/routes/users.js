var express = require('express');
var router = express.Router();

router.post('/SendData', function(req, res) {
  var db = req.db;
  var collection = db.collection('CropDetail');
  collection.insert(req.body,{}, function(err, result){
    res.send(
        (err === null) ? { msg: '1' } : { msg: err }
    );
  });
});
module.exports = router;
