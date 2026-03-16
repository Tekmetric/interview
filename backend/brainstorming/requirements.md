# Customer Rewards System

## Features

- [Enroll](#enroll)
- [View My Rewards](#view-my-balance)
- [Accrue Rewards](#accrue-rewards)
- [Redeem Rewards](#redeem-rewards)
- [Handle Refunds](#handle-refunds) 
- [Un-enroll](#un-enroll)

## Enroll
- [ ] support multiple customers to one account
- [ ] only allow a customer to maintain one rewards account
  - [ ] if customer is in 30 minute window from deletion, re-instate
## View My Rewards
- [ ] return point-in-time balance
- [ ] return recent rewards activity 
- [ ] support configurable data retention (default 1 year)
## Accrue Rewards
- [ ] calculate points based on purchase
- [ ] notify customer after reaching thresholds
- [ ] support configurable accrual rate for users to adjust
## Redeem Rewards
- [ ] deduct from balance
- [ ] reject attempts to redeem greater than balance
- [ ] do not accrue rewards on purchases where redemption 
## Handle Refunds
- [ ] if purchase is refunded where rewards were used, refund to account balance
- [ ] if purchase is refunded where rewards were not used, calculate rewards value and deduct from balance
- [ ] maintain both original purchase accrual and return deduction as separate transactions to avoid back-dating
## Un-enroll
- [ ] allow multi-customer account to exist if one member un-enrolls
- [ ] async, must support 30-minute re-enrollment window
- [ ] clean up transaction history on delete to avoid clashing histories if new account is created

