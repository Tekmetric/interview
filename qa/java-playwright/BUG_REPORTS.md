# Bugs / Observations — automationexercise.com

> Note: this is a public demo site. Some issues may be intentional or known limitations.

## BUG-1: Cart quantity accepts extremely large numbers and breaks UI/total formatting
**Area:** Cart / Quantity input  
**Steps to reproduce:**
1. Add any product to cart
2. Open Cart page
3. In Quantity field enter a very large number (e.g. 10000000000000)

**Expected:** Quantity should be validated/limited; UI layout remains stable; total formatted correctly  
**Actual:** Quantity accepts huge value, total overflows UI and layout breaks  
**Impact:** Invalid order state / broken checkout UI  
**Evidence:** `bug-screenshots/BUG-1-cart-quantity-overflow.png`

---

## BUG-2: Checkout may fail intermittently due to "too much load / unable to process request"
**Area:** Checkout / Payment confirmation  
**Steps to reproduce:**
1. Login
2. Add product to cart → Proceed to checkout
3. Place Order → Pay and Confirm Order

**Expected:** Order confirmed and success message displayed  
**Actual:** Sometimes site shows overload message and cannot process request  
**Frequency:** Intermittent  
**Impact:** E2E flow unreliable / flaky for automation  
**Evidence:** `bug-screenshots/BUG-2-checkout-overload.png`

---

## UX-1 (Visual): Product grid hover overlay covers product card content unexpectedly
**Area:** Products list / hover UI  
**Steps to reproduce:**
1. Open Products page
2. Hover mouse over a product card

**Expected:** Hover state does not obscure essential content and looks consistent  
**Actual:** Large orange overlay covers most of the card area  
**Impact:** Poor UX / usability issue  
**Evidence:** `bug-screenshots/UX-1-hover-overlay.png`

---

## UX-2: Brand filter is not clearly reflected in product cards summary
**Area:** Brand filtering / product list  
**Steps to reproduce:**
1. Open Products page
2. Click a Brand (e.g. POLO) in left menu
3. Observe product cards

**Expected:** Selected filter and applied brand are clearly visible in list results  
**Actual:** It’s not obvious from cards; user must open product detail to confirm  
**Impact:** Confusing UX / hard to validate filtering  
**Evidence:** `bug-screenshots/UX-2-brand-filter-visibility.png`
