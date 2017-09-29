# User commands

```
curl -XGET http://127.0.0.1:8001/user

curl -XPOST http://127.0.0.1:8001/user -d '{"name":"btellier","mailAddress":"tellier@linagora.com"}'

curl -XPUT http://127.0.0.1:8001/user/e8279000-a4cc-11e7-a63a-0b6d0478baa4 -d '{"name":"btellier","mailAddress":"tellier@obm.com"}'

curl -XGET http://127.0.0.1:8000/user/52d10350-a4cd-11e7-9ca4-1d906acca4b0

curl -XDELETE http://127.0.0.1:8001/user/e8279000-a4cc-11e7-a63a-0b6d0478baa4
```
## Account commands

```
curl -XGET http://127.0.0.1:8002/account/914cbb70-a4f4-11e7-8b0d-c7c0a48a8ee5/history

curl -XGET http://127.0.0.1:8002/account/914cbb70-a4f4-11e7-8b0d-c7c0a48a8ee5

curl -XPUT http://127.0.0.1:8002/account/914cbb70-a4f4-11e7-8b0d-c7c0a48a8ee5 -d '{"type":"credit", "amount":1000}'

curl -XPUT http://127.0.0.1:8002/account/914cbb70-a4f4-11e7-8b0d-c7c0a48a8ee5 -d '{"type":"debit", "amount":1000}'

curl -XPOST http://127.0.0.1:8002/account/f411fd30-a4f2-11e7-a0c9-bddd10fd7d90

curl -XGET http://127.0.0.1:8002/user/f411fd30-a4f2-11e7-a0c9-bddd10fd7d90/account
```
