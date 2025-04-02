import { Cart } from '../../types/Cart.ts';
import Button from '../Button/Button.tsx';

import { NumericFormat } from 'react-number-format';

interface PaymentSummaryProps {
  cart: Cart;
}

const PaymentSummary = ({ cart }: PaymentSummaryProps) => {
  return (
    <div className="flex h-fit w-full max-w-full flex-col gap-4 rounded-lg bg-white p-2 md:max-w-100 md:p-4">
      <span className="text-xl text-neutral-800">Payment Summary</span>
      <div className="flex flex-col gap-3">
        <div className="flex justify-between border-b border-b-neutral-300 pb-2">
          <span className="text-base text-neutral-800">Products price:</span>
          <span className="text-base text-neutral-800">
            <NumericFormat
              value={cart.total}
              displayType="text"
              thousandSeparator
              decimalSeparator="."
              decimalScale={2}
              fixedDecimalScale
              prefix="$"
            />
          </span>
        </div>
        <div className="flex justify-between border-b border-b-neutral-300 pb-2">
          <span className="text-base text-neutral-800">Shipping fee:</span>
          <span className="text-base text-neutral-800">Free</span>
        </div>
        <div className="flex justify-between">
          <span className="text-xl font-bold text-neutral-800">Total</span>
          <span className="text-xl font-bold text-neutral-800">
            <NumericFormat
              value={cart.total}
              displayType="text"
              thousandSeparator
              decimalSeparator="."
              decimalScale={2}
              fixedDecimalScale
              prefix="$"
            />
          </span>
        </div>
      </div>
      <Button>Proceed to Pay</Button>
    </div>
  );
};

export default PaymentSummary;
