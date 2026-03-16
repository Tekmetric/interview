## Customer
- PK: id
- first name
  - non-null
- last name
  - non-null
- rewards account
  - FK
## Rewards Account
- PK: id
- balance
  - non-null
  - default 0
## Rewards Activity
- CK: customer
- CK: rewards account
- timestamp
- credit/debit
- new rewards balance
