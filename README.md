# User commands

curl -XGET http://127.0.0.1:8001/user

curl -XPOST http://127.0.0.1:8001/user -d '{"name":"btellier","mailAddress":"btellier@linagora.com"}'

curl -XPUT http://127.0.0.1:8001/user/e8279000-a4cc-11e7-a63a-0b6d0478baa4 -d '{"name":"btellier","mailAddress":"btellier@obm.com"}'

curl -XGET http://127.0.0.1:8000/user/52d10350-a4cd-11e7-9ca4-1d906acca4b0

curl -XDELETE http://127.0.0.1:8001/user/e8279000-a4cc-11e7-a63a-0b6d0478baa4