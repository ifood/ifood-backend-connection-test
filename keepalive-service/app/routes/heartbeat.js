/**
 * Created by trestini on 13/01/17.
 */
const express = require('express');

/*
Shall ALWAYS return status code 200 when there' something to answer.
Different status codes means that the heartbeat is unresponsive.
The interested part (eg: monitoring tools) have the responsibility to
interpret different status codes.

Heartbeat object:

{
  status: <OK | ALERT>
  message: <descriptive message in case of ALERT>
}
 */

const router = express.Router();

router.route('/')
  .get((req, resp) => {

    let ret = {
      status: "OK"
    };

    resp.status(200).send(ret);

  });

module.exports = router;
